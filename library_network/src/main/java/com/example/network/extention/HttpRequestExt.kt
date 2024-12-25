package com.example.network.extention

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.example.network.callback.ApiCallBackListener
import com.example.network.callback.ErrorType
import com.example.network.request.HttpRequest
import com.example.network.response.ICommonResponse
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

inline fun <reified R> HttpRequest<R>.executeCall(): ApiCallBackListener<R> {
    val apiCallBackListener = ApiCallBackListener<R>()
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("exceptionHandler", "${coroutineContext[CoroutineName]} ：$throwable")
    }
    GlobalScope.launch(Dispatchers.Main + exceptionHandler) {
        try {
            val responseResult = withContext(Dispatchers.IO) {
                doRequest()
            }

            if (responseResult?.isSuccessful == true) {
                //请求成功
                val responseData = withContext(Dispatchers.IO) {
                    GsonBuilder().create().fromJson<R>(responseResult.body()?.string(), object :TypeToken<R>(){}.type)
                }
                if (responseData is ICommonResponse<*>) {
                    if (responseData.isBusinessSuccess()) {
                        apiCallBackListener.requestSuccess(responseData)
                    } else {
                        val needRequestError = apiService?.getApiServiceConfig()?.responseConfig?.businessErrorCodeHandlerCallBack?.invoke(responseData.code)
                        if(needRequestError == true){
                            apiCallBackListener.requestError(ErrorType.BUSSINESS_ERROR, responseData.code, responseData.message)
                        }
                    }
                } else if (responseData is R) {
                    apiCallBackListener.requestSuccess(responseData)
                }
            } else {
                apiCallBackListener.requestError(ErrorType.NETWORK_ERROR, responseResult?.code(), "服务器异常")
            }
        } catch (e: Throwable) {
            when (e) {
                is SocketTimeoutException -> {
                    apiCallBackListener.requestError(ErrorType.NETWORK_ERROR, -1, "当前排队人数较多，请稍后再试")
                }
                is UnknownHostException -> {
                    apiCallBackListener.requestError(ErrorType.NETWORK_ERROR, -1, "网络异常")
                }
                is ConnectException -> {
                    apiCallBackListener.requestError(ErrorType.NETWORK_ERROR, -1, "网络异常")
                }
                else -> {
                    apiCallBackListener.requestError(ErrorType.OTHER_ERROR, -1, e.message)
                }
            }
            e.printStackTrace()
        }
    }
    return apiCallBackListener
}

