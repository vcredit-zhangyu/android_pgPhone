package com.example.network.extention

import okhttp3.Request

fun Request.Builder.addFuncHeader(headerMap: Map<String?, String?>?): Request.Builder {
    headerMap?.forEach {
        if (it.key != null && it.value != null) {
            header(it.key!!, it.value!!)
        }
    }
    return this
}