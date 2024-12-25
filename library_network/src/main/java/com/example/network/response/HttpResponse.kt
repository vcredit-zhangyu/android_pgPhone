package com.example.network.response

import okhttp3.Headers
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class HttpResponse(private val resultCode: Int? = -1, val responseHeader: Headers?, body: InputStream?) {

    private var body: InputStream? = null

    val isSuccess: Boolean
        get() = resultCode in 200..299

    init {
        this.body = cloneInputStream(body)
    }

    @Throws(IOException::class)
    private fun cloneInputStream(body: InputStream?): InputStream? {
        body?.run {
            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            while (true) {
                val length = body.read(buffer)
                if (length == -1) break
                outputStream.write(buffer, 0, length)
            }
            return ByteArrayInputStream(outputStream.toByteArray())
        }
        return null
    }

    fun clear() {
        body?.run {
            try {
                body!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            body = null
        }
    }
}