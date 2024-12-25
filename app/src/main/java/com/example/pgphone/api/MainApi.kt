package com.example.pgphone.api

import com.google.gson.annotations.SerializedName
import com.example.network.OkHttpClientApiService
import com.example.network.callback.ApiCallBackListener
import com.example.network.extention.executeCall
import com.example.network.request.GetRequest
import com.example.network.request.PostRequest
import com.example.pgphone.entity.DownloadProductEntity
import java.io.Serializable

object MainApi {

    private var apiService: OkHttpClientApiService = ApiManager.getApiService()

    /**
     * 提交设备信息
     */
    fun commitPhoneInfo(bodyMap: Map<String, Any>): ApiCallBackListener<CommonResponse<Any>> {
        return PostRequest<CommonResponse<Any>>("device/info/commit")
            .setApiService(apiService)
            .addParamMap(bodyMap)
            .executeCall()
    }

    fun getPhoneInfo(deviceId: String): ApiCallBackListener<CommonResponse<PhoneInfo>> {
        return GetRequest<CommonResponse<PhoneInfo>>("device/info/query")
            .setApiService(apiService)
            .addParam("deviceNo", deviceId)     // 设备唯一编号
            .executeCall()
    }

    fun commitPhoneUseRecord(appContent: ArrayList<UploadUseInfo>): ApiCallBackListener<CommonResponse<Any>> {
        return PostRequest<CommonResponse<Any>>("device/app/commit")
            .setApiService(apiService)
            .addParamObj(appContent)
            .executeCall()
    }

    /**
     * 版本检测更新
     */
    fun checkVersionUpdate(): ApiCallBackListener<CommonResponse<UpdateInfo>> {
        return GetRequest<CommonResponse<UpdateInfo>>("app/version/query")
            .setApiService(apiService)
            .addParam("appPlatform", "android")     // 平台 ios android
            .executeCall()
    }

    fun commitFeedbackInfo(deviceId: String, content: String): ApiCallBackListener<CommonResponse<Any>> {
        return PostRequest<CommonResponse<Any>>("device/feedback/commit")
            .setApiService(apiService)
            .addParam("deviceNo", deviceId)
            .addParam("content", content)
            .executeCall()
    }

    fun updatePhoneInfo(deviceId: String, deviceSystemVersion: String): ApiCallBackListener<CommonResponse<Any>> {
        return PostRequest<CommonResponse<Any>>("device/info/update")
            .setApiService(apiService)
            .addParam("deviceId", deviceId)
            .addParam("deviceSystemVersion", deviceSystemVersion)
            .executeCall()
    }

    fun updateActiveDate(deviceId: String, activeDateTime: String): ApiCallBackListener<CommonResponse<Any>> {
        return PostRequest<CommonResponse<Any>>("device/active/record")
            .setApiService(apiService)
            .addParam("deviceNo", deviceId)                 // 设备编号
            .addParam("activeDateTime", activeDateTime)     // 活跃时间
            .executeCall()
    }

}

data class UpdateInfo(

    @SerializedName(value = "appVersion")
    val appVersion: String? = null,         // 版本号

    @SerializedName(value = "appPlatform")
    val appPlatform: String? = null,        // 平台

    @SerializedName(value = "versionRemark")
    val versionRemark: String? = null,      // 版本介绍

    @SerializedName(value = "downloadUrl")
    val downloadUrl: String? = null,       // 下载链接

)

data class PhoneInfo(

    @SerializedName(value = "deviceCharge")
    val deviceCharge: String? = null,   // 设备负责人

    @SerializedName(value = "expireDate")
    val expireDate: String? = null,     // 到期日期

    @SerializedName(value = "deviceStatus")
    val deviceStatus: String? = null,   // 状态

    @SerializedName(value = "deviceType")
    val deviceType: String? = null,

    @SerializedName(value = "deviceName")
    val deviceName: String? = null,

    @SerializedName(value = "deviceNo")
    val deviceNo: String? = null,

    @SerializedName(value = "deviceModel")
    val deviceModel: String? = null,

    @SerializedName(value = "deviceSystem")
    val deviceSystem: String? = null,

    @SerializedName(value = "deviceSystemVersion")
    val deviceSystemVersion: String? = null,    // 设备系统版本

    @SerializedName(value = "deviceResolution")
    val deviceResolution: String? = null,
) : Serializable

data class LocalPackageInfo(
    @SerializedName(value = "packageName")
    val packageName: String = "",

    @SerializedName(value = "packageCode")
    val packageCode: String = "",
)

/**
 * 上传使用信息
 */
data class UploadUseInfo(
    @SerializedName(value = "deviceNo")
    val deviceNo: String = "",

    @SerializedName(value = "app")
    val app: String = "",

    @SerializedName(value = "appTime")
    val appTime: String = "",
)

class DownloadProductResultInfo {

    @SerializedName("android")
    val android: DownloadProductResultMainInfo? = null

}

data class DownloadProductResultMainInfo(

    @SerializedName("name")
    val name: String? = "",

    @SerializedName("display_name")
    val display_name: String? = "",

    @SerializedName("app")
    val app: List<DownloadProductEntity>? = null,
)