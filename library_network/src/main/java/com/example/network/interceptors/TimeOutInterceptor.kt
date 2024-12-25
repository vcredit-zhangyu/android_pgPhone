package com.example.network.interceptors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

class TimeOutInterceptor : Interceptor {

    companion object {
        const val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
        const val READ_TIMEOUT = "READ_TIMEOUT"
        const val WRITE_TIMEOUT = "WRITE_TIMEOUT"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val connectTimeout = chain.connectTimeoutMillis()
        val readTimeout = chain.readTimeoutMillis()
        val writeTimeout = chain.writeTimeoutMillis()

        val request: Request = chain.request()
        val funcConnectTimeOut: Int = if (request.header(CONNECT_TIMEOUT) != null) request.header(CONNECT_TIMEOUT)!!.toInt() else connectTimeout
        val funcReadTimeOut: Int = if (request.header(READ_TIMEOUT) != null) request.header(READ_TIMEOUT)!!.toInt() else readTimeout
        val funcWriteTimeOut: Int = if (request.header(WRITE_TIMEOUT) != null) request.header(WRITE_TIMEOUT)!!.toInt() else writeTimeout

        return chain.withConnectTimeout(funcConnectTimeOut, TimeUnit.MILLISECONDS)
                .withReadTimeout(funcReadTimeOut, TimeUnit.MILLISECONDS)
                .withWriteTimeout(funcWriteTimeOut, TimeUnit.MILLISECONDS)
                .proceed(request)
    }
}