package com.example.network

import com.example.network.config.ApiServiceConfig
import com.example.network.extention.addFuncHeader
import com.example.network.interceptors.EncryptAndDecryptInterceptor
import com.example.network.interceptors.TimeOutInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class OkHttpClientApiService(private val apiServiceConfig: ApiServiceConfig) {

    private var httpClient: OkHttpClient? = null

    init {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(TimeOutInterceptor())
        apiServiceConfig.interceptorList?.forEach {
            builder.addInterceptor(it)
        }
        builder.addInterceptor(EncryptAndDecryptInterceptor(apiServiceConfig.encryptConfig, apiServiceConfig.decryptConfig))
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(getLogInterceptor()) //添加日志打印
        }
        apiServiceConfig.requestConfig.sslFactoryAndManager?.run {
            second?.let {
                builder.sslSocketFactory(first, it)   // 添加HTTPS单项或双向证书校验
            }
        }
        builder.connectTimeout(apiServiceConfig.timeOutConfig.connectTimeOut, TimeUnit.MILLISECONDS)
        builder.readTimeout(apiServiceConfig.timeOutConfig.readTimeOut, TimeUnit.MILLISECONDS)
        builder.writeTimeout(apiServiceConfig.timeOutConfig.writeTimeOut, TimeUnit.MILLISECONDS)
        builder.retryOnConnectionFailure(false) //后端有压力，暂时关闭
        httpClient = builder.build()
    }

    fun getApiServiceConfig(): ApiServiceConfig {
        return apiServiceConfig
    }

    //Get请求
    fun doGet(url: String, headerMap: Map<String?, String?>?): Response? {
        val requestBuilder = Request.Builder().get()
            .url(url)
            .addFuncHeader(getApiServiceConfig().requestConfig.getCommonRequestHeadersFunc?.invoke())
            .addFuncHeader(headerMap)
        return executeRequest(httpClient, requestBuilder.build())
    }

    //Post请求
    fun doPost(url: String, headerMap: Map<String?, String?>?, requestBody: RequestBody): Response? {
        val requestBuilder = Request.Builder().post(requestBody)
            .url(url)
            .addFuncHeader(getApiServiceConfig().requestConfig.getCommonRequestHeadersFunc?.invoke())
            .addFuncHeader(headerMap)
        return executeRequest(httpClient, requestBuilder.build())
    }

    private fun executeRequest(httpClient: OkHttpClient?, request: Request): Response? {
        return httpClient?.newCall(request)?.execute()
    }

    private fun getLogInterceptor(): HttpLoggingInterceptor {
        val loginInterceptor = HttpLoggingInterceptor()
        loginInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loginInterceptor
    }
}