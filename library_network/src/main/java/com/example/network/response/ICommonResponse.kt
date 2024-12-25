package com.example.network.response

interface ICommonResponse<T> {
    var code: Int?
    var message: String?
    var data: T?
    fun isBusinessSuccess(): Boolean
}
