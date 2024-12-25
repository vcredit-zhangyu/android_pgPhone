package com.example.network.base

import com.example.network.OkHttpClientApiService
import com.example.network.model.MediaTypeEnum
import com.example.network.config.AddTokenConfig
import com.example.network.config.ApiServiceConfig
import com.example.network.config.DecryptConfig
import com.example.network.config.EncryptConfig
import com.example.network.config.RequestConfig
import com.example.network.config.ResponseConfig
import com.example.network.config.SignConfig
import com.example.network.config.TimeOutConfig
import okhttp3.Interceptor
import okhttp3.MediaType

abstract class BaseApiManager {
    private val httpService: OkHttpClientApiService by lazy { init() }

    private fun init(): OkHttpClientApiService {
        val apiServiceConfig = ApiServiceConfig()
        apiServiceConfig.requestMediaType = getRequestMediaType() ?: MediaTypeEnum.TYPE_JSON
        apiServiceConfig.getBaseUrlFunc = getBaseUrlFuc()
        apiServiceConfig.requestConfig = RequestConfig(getCommonRequestHeadersFunc = getCommonRequestHeadersFunc())
        apiServiceConfig.responseConfig = ResponseConfig(businessErrorCodeHandlerCallBack = getHandleBusinessErrorCodeFunc())
        apiServiceConfig.addTokenConfig = getAddTokenConfig()
        apiServiceConfig.signConfig = getSignConfig()
        apiServiceConfig.needEncrypt = getNeedEncrypt()
        apiServiceConfig.encryptConfig = getEncryptConfig()
        apiServiceConfig.timeOutConfig = getTimeOutConfig() ?: TimeOutConfig()
        apiServiceConfig.decryptConfig = getDecryptConfig()
        apiServiceConfig.interceptorList = getInterceptorList()
        return OkHttpClientApiService(apiServiceConfig)
    }

    abstract fun getRequestMediaType(): MediaType?

    abstract fun getDecryptConfig(): DecryptConfig?

    abstract fun getTimeOutConfig(): TimeOutConfig?

    abstract fun getEncryptConfig(): EncryptConfig?

    abstract fun getNeedEncrypt(): Boolean

    abstract fun getAddTokenConfig(): AddTokenConfig?

    abstract fun getInterceptorList(): List<Interceptor>?

    abstract fun getSignConfig(): SignConfig?

    abstract fun getBaseUrlFuc(): (() -> String)?

    abstract fun getCommonRequestHeadersFunc(): (() -> Map<String?, String?>)?

    abstract fun getHandleBusinessErrorCodeFunc(): ((errorCode: Int?) -> Boolean)?

    fun getApiService(): OkHttpClientApiService {
        return httpService
    }
}