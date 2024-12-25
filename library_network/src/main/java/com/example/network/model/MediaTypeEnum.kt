package com.example.network.model

import okhttp3.MediaType

object MediaTypeEnum {
    // 请求的ContentType 主要为以下四种
    val TYPE_JSON = MediaType.parse("application/json;charset=UTF-8")
    val TYPE_FORM = MediaType.parse("application/x-www-form-urlencoded")
    val TYPE_MULTIPART_FORM = MediaType.parse("multipart/form-data")
    val TYPE_STREAM = MediaType.parse("application/octet-stream")

    val TYPE_TEXT = MediaType.parse("text/plain")
    val TYPE_IMAGE_JPEG = MediaType.parse("image/jpeg")
    val TYPE_IMAGE_PNG = MediaType.parse("image/png")
}