package com.example.network.request

import com.google.gson.Gson
import com.example.network.OkHttpClientApiService
import com.example.network.config.EncryptConfig
import com.example.network.config.SignConfig
import com.example.network.config.TimeOutConfig
import com.example.network.interceptors.EncryptAndDecryptInterceptor
import com.example.network.model.FileRequestParam
import com.example.network.model.MediaTypeEnum
import com.example.network.model.ParamPlaceEnum
import com.example.network.model.RequestParam
import okhttp3.*
import java.io.File
import java.lang.Exception

open class PostRequest<R>(pathOrIntactUrl: String) : HttpRequest<R>(pathOrIntactUrl) {

    private var jsonRequestObj: Any? = null
    private var mediaType: MediaType? = MediaTypeEnum.TYPE_JSON
    private var fileRequestParamList = ArrayList<FileRequestParam>()
    private var encryptConfig: EncryptConfig? = null

    override fun setApiService(apiService: OkHttpClientApiService): PostRequest<R> {
        mediaType = apiService.getApiServiceConfig().requestMediaType
        this.encryptConfig = apiService.getApiServiceConfig().encryptConfig
        addEncryptHeaderTag(apiService.getApiServiceConfig().needEncrypt)
        super.setApiService(apiService)
        return this
    }

    override fun addHeader(headerMap: Map<String, String>?): PostRequest<R> {
        super.addHeader(headerMap)
        return this
    }

    override fun addHeader(name: String, value: String): PostRequest<R> {
        super.addHeader(name, value)
        return this
    }

    override fun addParam(name: String, value: Any?, encoded: Boolean): PostRequest<R> {
        super.addParam(name, value, encoded)
        return this
    }

    override fun addParamMap(bodyMap: Map<String, Any>?): PostRequest<R> {
        super.addParamMap(bodyMap)
        return this
    }

    fun addParamObj(jsonRequestObj: Any): PostRequest<R> {
        this.jsonRequestObj = jsonRequestObj
        return this
    }

    override fun setSignConfig(signConfig: SignConfig): PostRequest<R> {
        super.setSignConfig(signConfig)
        return this
    }

    override fun setTimeOutConfig(timeOutConfig: TimeOutConfig): PostRequest<R> {
        super.setTimeOutConfig(timeOutConfig)
        return this
    }

    fun addFileParam(name: String, value: File?, fileContentType: MediaType? = MediaTypeEnum.TYPE_STREAM): HttpRequest<R> {
        value?.run {
            fileRequestParamList.add(FileRequestParam(name, this, fileContentType))
        }
        return this
    }

    fun addFileParam(name: String, filePath: String?, fileContentType: MediaType? = MediaTypeEnum.TYPE_STREAM): PostRequest<R> {
        try {
            if (filePath != null && File(filePath).exists()) {
                fileRequestParamList.add(FileRequestParam(name, File(filePath), fileContentType))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun setRequestMediaType(mediaType: MediaType?): PostRequest<R> {
        this.mediaType = mediaType
        return this
    }

    fun setNeedEncrypt(needEncrypt: Boolean): PostRequest<R> {
        addEncryptHeaderTag(needEncrypt)
        return this
    }

    private fun addEncryptHeaderTag(needEncrypt: Boolean) {
        if (needEncrypt) {
            addHeader(EncryptAndDecryptInterceptor.NEED_ENCRYPT, "true")
        } else {
            requestHeaderMap.remove(EncryptAndDecryptInterceptor.NEED_ENCRYPT)
        }
    }

    private fun createRequestBodyByType(
        contentType: MediaType?,
        paramMap: LinkedHashMap<String, RequestParam>?,
        fileList: List<FileRequestParam>,
    ): RequestBody {
        signConfig?.run {
            if (!this.signFunction?.invoke(requestParamMap).isNullOrEmpty()) {
                if (this.signParamPlace == ParamPlaceEnum.FROM_OR_TO_BODY) {
                    paramMap?.put(this.signParamName, RequestParam(this.signFunction?.invoke(requestParamMap)!!, true))
                } else if (this.signParamPlace == ParamPlaceEnum.FROM_OR_TO_HEADER) {
                    requestHeaderMap[this.signParamName] = this.signFunction?.invoke(requestParamMap)!!
                }
            }
        }
        return when (contentType) {
            MediaTypeEnum.TYPE_JSON -> {
                val mParamMap = HashMap<String, Any>()
                paramMap?.forEach {
                    mParamMap[it.key] = it.value.requestParamValue
                }
                if (jsonRequestObj != null) {
                    RequestBody.create(MediaTypeEnum.TYPE_JSON, Gson().toJson(jsonRequestObj))
                } else {
                    RequestBody.create(MediaTypeEnum.TYPE_JSON, Gson().toJson(mParamMap))
                }
            }

            MediaTypeEnum.TYPE_FORM -> {
                val formBodyBuilder = FormBody.Builder()
                paramMap?.forEach {
                    if (it.value.encoded) {
                        formBodyBuilder.addEncoded(it.key, it.value.requestParamValue.toString())
                    } else {
                        formBodyBuilder.add(it.key, it.value.toString())
                    }
                }
                formBodyBuilder.build()
            }

            MediaTypeEnum.TYPE_MULTIPART_FORM -> {
                val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                paramMap?.forEach {
                    multipartBodyBuilder.addFormDataPart(it.key, it.value.requestParamValue.toString())
                }
                fileList.forEach {
                    val fileRequestBody = RequestBody.create(it.fileContentType, it.file)
                    multipartBodyBuilder.addFormDataPart(it.requestParamName, it.file.name, fileRequestBody)
                }
                multipartBodyBuilder.build()
            }

            else -> {
                //其他类型的content-type
                return when {
                    fileList.isEmpty() -> RequestBody.create(contentType, "")
                    fileList.size == 1 -> RequestBody.create(contentType, fileList[0].file)
                    else -> {
                        createRequestBodyByType(MediaTypeEnum.TYPE_MULTIPART_FORM, paramMap, fileRequestParamList)
                    }
                }
            }
        }
    }

    override fun doRequest(): Response? {
        return apiService?.doPost(buildRequestUrl(), requestHeaderMap, createRequestBodyByType(mediaType, requestParamMap, fileRequestParamList))
    }
}