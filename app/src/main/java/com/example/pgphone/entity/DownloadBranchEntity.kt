package com.example.pgphone.entity

import com.google.gson.annotations.SerializedName

data class DownloadBranchEntity(
    @SerializedName("name")
    val name: String?,
    @SerializedName("display_name")
    val display_name: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("sort_index")
    val sort_index: String?,
    @SerializedName("server")
    val server: List<DownloadServerEntity>?,
)
