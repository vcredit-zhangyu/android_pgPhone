package com.example.network.request

import android.net.Uri
import com.example.network.model.ParamPlaceEnum
import com.example.network.model.RequestParam
import okhttp3.Response
import java.net.URLEncoder

class GetRequest<R>(pathOrIntactUrl: String) : HttpRequest<R>(pathOrIntactUrl) {
    override fun buildRequestUrl(): String {
        if (requestParamMap.any { it.value.encoded }) {
            //如有任何字段无需进行urlEncode,则采用拼接的方式
            val urlStringBuilder = StringBuilder()
            urlStringBuilder.append(super.buildRequestUrl())
            urlStringBuilder.append("?")
            signConfig?.run {
                if (!this.signFunction?.invoke(requestParamMap).isNullOrEmpty()) {
                    if (this.signParamPlace == ParamPlaceEnum.FROM_OR_TO_BODY) {
                        requestParamMap[this.signParamName] = RequestParam(this.signFunction?.invoke(requestParamMap)!!)
                    } else if (this.signParamPlace == ParamPlaceEnum.FROM_OR_TO_HEADER) {
                        requestHeaderMap[this.signParamName] = this.signFunction?.invoke(requestParamMap)!!
                    }
                }
            }
            requestParamMap.forEach {
                var paramValue = if (it.value.encoded) {
                    it.value.requestParamValue.toString()
                } else {
                    URLEncoder.encode(it.value.requestParamValue.toString(), "utf-8")
                }
                urlStringBuilder.append("${it.key}=${paramValue}")
                urlStringBuilder.append("&")
            }
            urlStringBuilder.deleteCharAt(urlStringBuilder.lastIndex)
            return urlStringBuilder.toString()
        } else {
            val builder = Uri.parse(super.buildRequestUrl()).buildUpon()
            requestParamMap.forEach {
                builder.appendQueryParameter(it.key, it.value.requestParamValue.toString())
            }
            signConfig?.run {
                if (!this.signFunction?.invoke(requestParamMap).isNullOrEmpty()) {
                    if (this.signParamPlace == ParamPlaceEnum.FROM_OR_TO_BODY) {
                        builder.appendQueryParameter(this.signParamName, this.signFunction?.invoke(requestParamMap))
                    } else if (this.signParamPlace == ParamPlaceEnum.FROM_OR_TO_HEADER) {
                        requestHeaderMap[this.signParamName] = this.signFunction?.invoke(requestParamMap)!!
                    }
                }
            }
            return builder.toString()
        }
    }

    override fun doRequest(): Response? {
        return apiService?.doGet(buildRequestUrl(), requestHeaderMap)
    }
}