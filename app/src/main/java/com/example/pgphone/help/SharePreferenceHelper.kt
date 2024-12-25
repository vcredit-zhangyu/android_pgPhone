package com.example.pgphone.help

import com.example.pgphone.utils.SharePreferenceUtils

object SharePreferenceHelper {

    private val SHARE_OAID = "common_oaid"
    private val SHARE_DEVICE_ID = "common_device_id"
    private val SHARE_CLIENT_TYPE = "common_client_type"

    // 存储oaid
    fun setOaid(phoneContent: String) {
        SharePreferenceUtils().putString(SHARE_OAID, phoneContent)
    }

    // 获取oaid
    fun getOaid():String? {
        return SharePreferenceUtils().getString(SHARE_OAID, "")
    }

    // 存储deviceId
    fun setDeviceId(deviceId: String) {
        SharePreferenceUtils().putString(SHARE_DEVICE_ID, deviceId)
    }

    // 获取deviceId
    fun getDeviceId():String? {
        return SharePreferenceUtils().getString(SHARE_DEVICE_ID, "")
    }

    // 存储客户类型
    fun setClientType(type: Int) {
        SharePreferenceUtils().putInt(SHARE_CLIENT_TYPE, type)
    }

    // 获取客户类型
    fun getClientType(defaultValue: Int = -1): Int {
        return SharePreferenceUtils().getInt(SHARE_CLIENT_TYPE, defaultValue)
    }


}