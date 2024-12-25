package com.example.network.request

import com.example.network.OkHttpClientApiService
import com.example.network.config.SignConfig
import com.example.network.config.TimeOutConfig
import com.example.network.interceptors.TimeOutInterceptor
import com.example.network.model.ParamPlaceEnum
import com.example.network.model.RequestParam
import okhttp3.Response


abstract class HttpRequest<R>(private val pathOrIntactUrl: String) {
    var requestHeaderMap = LinkedHashMap<String?, String?>()
    var requestParamMap = LinkedHashMap<String, RequestParam>()
    var apiService: OkHttpClientApiService? = null
    var signConfig: SignConfig? = null

    open fun addHeader(headerMap: Map<String, String>?): HttpRequest<R> {
        headerMap?.run {
            requestHeaderMap.putAll(this)
        }
        return this
    }

    open fun addHeader(name: String, value: String): HttpRequest<R> {
        requestHeaderMap[name] = value
        return this
    }

    open fun addParam(name: String, value: Any?, encoded: Boolean = false): HttpRequest<R> {
        value?.run {
            requestParamMap[name] = RequestParam(value, encoded)
        }
        return this
    }

    open fun addParamMap(bodyMap: Map<String, Any>?): HttpRequest<R> {
        bodyMap?.forEach {
            requestParamMap[it.key] = RequestParam(it.value, false)
        }
        return this
    }

    open fun setApiService(apiService: OkHttpClientApiService): HttpRequest<R> {
        this.apiService = apiService
        this.signConfig = apiService.getApiServiceConfig().signConfig
        val tokenConfig = apiService.getApiServiceConfig().addTokenConfig
        if (tokenConfig?.getRequestTokenFunc?.invoke()?.isEmpty() == false) {
            // token不为空
            when (tokenConfig.tokenParamPlace) {
                ParamPlaceEnum.FROM_OR_TO_HEADER -> {
                    requestHeaderMap[tokenConfig.tokenParamName] =
                        tokenConfig.getRequestTokenFunc?.invoke()
                }

                ParamPlaceEnum.FROM_OR_TO_BODY -> {
                    tokenConfig.getRequestTokenFunc?.invoke()?.run {
                        requestParamMap[tokenConfig.tokenParamName] = RequestParam(this)
                    }
                }

                ParamPlaceEnum.BODY_AND_HEADER -> {
                    tokenConfig.getRequestTokenFunc?.invoke()?.run {
                        requestHeaderMap[tokenConfig.tokenParamName] = this
                        requestParamMap[tokenConfig.tokenParamName] = RequestParam(this)
                    }
                }

                else -> {

                }
            }
        }
        return this
    }

    open fun setSignConfig(signConfig: SignConfig): HttpRequest<R> {
        this.signConfig = signConfig
        return this
    }

    open fun setTimeOutConfig(timeOutConfig: TimeOutConfig): HttpRequest<R> {
        addHeader(TimeOutInterceptor.CONNECT_TIMEOUT, timeOutConfig.connectTimeOut.toString())
        addHeader(TimeOutInterceptor.READ_TIMEOUT, timeOutConfig.readTimeOut.toString())
        addHeader(TimeOutInterceptor.WRITE_TIMEOUT, timeOutConfig.writeTimeOut.toString())
        return this
    }

    open fun buildRequestUrl(): String {
        return if (pathOrIntactUrl.startsWith("http")) pathOrIntactUrl else apiService?.getApiServiceConfig()?.getBaseUrlFunc?.invoke() + pathOrIntactUrl
    }

    abstract fun doRequest(): Response?

}