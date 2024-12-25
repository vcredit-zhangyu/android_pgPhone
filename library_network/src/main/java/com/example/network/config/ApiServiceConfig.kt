package com.example.network.config

import com.example.network.model.MediaTypeEnum
import com.example.network.model.ParamPlaceEnum
import okhttp3.Interceptor
import okhttp3.MediaType
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class ApiServiceConfig {
    var getBaseUrlFunc: (() -> String)? = null
    var requestMediaType: MediaType? = MediaTypeEnum.TYPE_JSON
    var requestConfig: RequestConfig = RequestConfig()
    var responseConfig: ResponseConfig = ResponseConfig()
    var timeOutConfig: TimeOutConfig = TimeOutConfig()

    var addTokenConfig: AddTokenConfig? = null
    var signConfig: SignConfig? = null
    var needEncrypt: Boolean = false
    var encryptConfig: EncryptConfig? = null
    var decryptConfig: DecryptConfig? = null
    var interceptorList: List<Interceptor>? = null

}

data class RequestConfig(
    var getCommonRequestHeadersFunc: (() -> Map<String?, String?>)? = null,
    var sslFactoryAndManager: Pair<SSLSocketFactory, X509TrustManager?>? = null,
)

data class TimeOutConfig(
    var connectTimeOut: Long = 15000,
    var readTimeOut: Long = 20000,
    var writeTimeOut: Long = 20000,
)

data class ResponseConfig(
    var businessErrorCodeHandlerCallBack: ((errorCode: Int?) -> Boolean)? = null,
)

data class AddTokenConfig(
    var tokenParamName: String,
    var tokenParamPlace: ParamPlaceEnum = ParamPlaceEnum.FROM_OR_TO_HEADER,
    var getRequestTokenFunc: (() -> String?)? = null,
)

data class SignConfig(
    var signParamName: String,
    var signFunction: ((paramStr: Map<String, Any>?) -> String?)?,
    var signParamPlace: ParamPlaceEnum = ParamPlaceEnum.FROM_OR_TO_BODY,
)

data class EncryptConfig(var encryptedKeyName: String?)

data class DecryptConfig(var decryptKeyName: String?)