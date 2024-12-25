package com.example.pgphone.api

import com.google.gson.annotations.SerializedName
import com.example.network.response.ICommonResponse

class CommonResponse<T> : ICommonResponse<T> {

    @SerializedName("code")
    override var code: Int? = null

    @SerializedName("msg")
    override var message: String? = null

    @SerializedName("data")
    override var data: T? = null

    override fun isBusinessSuccess(): Boolean {
        return code == 0
    }

}