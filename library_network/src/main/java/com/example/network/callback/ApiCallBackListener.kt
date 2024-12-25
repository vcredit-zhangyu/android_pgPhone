package com.example.network.callback

class ApiCallBackListener<R> {
    private var successListener: ((R?) -> Unit)? = null     // 正确的回调 完整的数据，未加工
    private var errorListener: ((errorType: ErrorType, errorCode: Int?, errorMsg: String?) -> Unit)? = null//错误的回调
    private var completeListener: (() -> Unit)? = null      // 接口完成的回调

    // 完整的数据结构返回
    fun setSuccessListener(successListener: (R?) -> Unit): ApiCallBackListener<R> {
        this@ApiCallBackListener.successListener = successListener
        return this@ApiCallBackListener
    }

    fun setErrorListener(errorListener: (errorType: ErrorType, errorCode: Int?, errorMsg: String?) -> Unit): ApiCallBackListener<R> {
        this@ApiCallBackListener.errorListener = errorListener
        return this@ApiCallBackListener
    }

    fun setCompleteListener(completeListener: () -> Unit): ApiCallBackListener<R> {
        this@ApiCallBackListener.completeListener = completeListener
        return this@ApiCallBackListener
    }

    fun requestSuccess(completeData: R?) {
        successListener?.invoke(completeData)
    }

    fun requestError(errorType: ErrorType, errorCode: Int?, errorMsg: String?) {
        errorListener?.invoke(errorType, errorCode, errorMsg)
    }

    fun requestComplete() {
        completeListener?.invoke()
    }
}

enum class ErrorType {
    NETWORK_ERROR,
    BUSSINESS_ERROR,
    OTHER_ERROR
}
