package com.example.pgphone.entity

import com.google.gson.annotations.SerializedName

data class DownloadServerEntity(
    @SerializedName("name")
    val name: String?,
    @SerializedName("display_name")
    val display_name: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("sort_index")
    val sort_index: String?,
    @SerializedName("port")
    val port: String?,
    @SerializedName("can_download")
    val can_download: Boolean?,
    @SerializedName("download_url")
    val download_url: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("create_date")
    val create_date: String?,
    @SerializedName("write_date")
    val write_date: String?,
    @SerializedName("android_version")
    val android_version: String?,
    @SerializedName("version_code")
    val version_code: String?,
)
