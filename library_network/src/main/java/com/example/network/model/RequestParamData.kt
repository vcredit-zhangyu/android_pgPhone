package com.example.network.model

import okhttp3.MediaType
import java.io.File

data class FileRequestParam(
    val requestParamName: String,
    val file: File,
    val fileContentType: MediaType?,
)

data class RequestParam(
    val requestParamValue: Any,
    val encoded: Boolean = false,
)