package com.example.network.interceptors

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.example.network.config.DecryptConfig
import com.example.network.config.EncryptConfig
import com.example.network.model.MediaTypeEnum
import com.example.network.encrypt.AesEcbUtils
import com.example.network.encrypt.RSAUtils
import okhttp3.*
import okio.Buffer
import okio.BufferedSource
import okio.Okio
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class EncryptAndDecryptInterceptor(private val encryptConfig: EncryptConfig?, private val decryptConfig: DecryptConfig?) : Interceptor {

    companion object {
        const val NEED_ENCRYPT = "need_encrypt"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var aesKey: String? = null
        encryptConfig?.encryptedKeyName?.run {
            if (request.header(NEED_ENCRYPT) == "true") {
                try {
                    aesKey = AesEcbUtils.getAesKey()
                    val encKey = RSAUtils.buildRSAEncryptByPublicKey(aesKey, RSAUtils.publicKey)
                    request = request.newBuilder().addHeader(this, request.header(NEED_ENCRYPT) ?: "").removeHeader(NEED_ENCRYPT)
                        .addHeader("encKey", encKey).build()
                    request = encryptRequest(request, aesKey!!, encKey)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (!aesKey.isNullOrEmpty()) {
            val response = chain.proceed(request)
            val mediaType = response.body()?.contentType()
            var charset = Charset.forName("UTF-8")
            val contentType = response.body()?.contentType()
            contentType?.run {
                charset = charset(charset) ?: Charset.forName("UTF-8")
            }
            return if (mediaType == MediaTypeEnum.TYPE_JSON && !aesKey.isNullOrEmpty()) {
                val responseBodyString = readString(response.body()?.byteStream(), charset)
                val responseJsonElement = JsonParser.parseString(responseBodyString)
                if (responseJsonElement.isJsonObject && responseJsonElement.asJsonObject?.get("data") != null && responseJsonElement.asJsonObject.get(
                        "data"
                    ).isJsonPrimitive && responseJsonElement.asJsonObject.get("data").asJsonPrimitive.isString && !responseJsonElement.asJsonObject.get(
                        "data"
                    ).asString.isNullOrEmpty()
                ) {
                    try {
                        val dataJsonElement =
                            JsonParser.parseString(AesEcbUtils.decrypt(responseJsonElement.asJsonObject.get("data").asString, aesKey))
                        responseJsonElement.asJsonObject.remove("data")
                        responseJsonElement.asJsonObject.add("data", dataJsonElement)
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                    }
                }
                response.newBuilder().body(ResponseBody.create(mediaType, Gson().toJson(responseJsonElement))).build()
            } else {
                response.newBuilder().body(ResponseBody.create(mediaType, readString(response.body()?.byteStream(), charset) ?: "")).build()
            }
        }
        return chain.proceed(request)
    }

    private fun encryptRequest(request: Request, aesKey: String, encKey: String): Request {
        if (request.method() == "POST") {
            val buffer = Buffer()
            val requestBody = request.body()
            requestBody?.writeTo(buffer)
            var charset = Charset.forName("UTF-8")
            val contentType = requestBody?.contentType()
            contentType?.run {
                charset = charset(charset) ?: Charset.forName("UTF-8")
            }
            val paramString = buffer.readString(charset)
            if (!paramString.isNullOrEmpty()) {
                val jsonObject = JSONObject()
                jsonObject.put("sign", encKey)
                jsonObject.put("data", AesEcbUtils.encrypt(paramString, aesKey))
                val body = RequestBody.create(contentType, jsonObject.toString())
                return request.newBuilder().post(body).build()
            }

        }
        return request
    }

    @Throws(IOException::class)
    fun readString(inputStream: InputStream?, charset: Charset): String? {
        inputStream?.run {
            val source: BufferedSource = Okio.buffer(Okio.source(inputStream)) //创建BufferedSource
            return source.readString(charset)
        }
        return null
    }
}