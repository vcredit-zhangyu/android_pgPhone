package com.example.pgphone.help

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.pgphone.utils.LogUtil

/**
 * 设备唯一id帮助类
 */
object DeviceIdHelper {

    private val TAG = javaClass.simpleName

    fun initOaid(context: Context) {
        // 获取OAID等设备标识符
        val oaidHelper = OaidHelper(appIdsUpdater)
        oaidHelper.getDeviceIds(context)
    }

    private val appIdsUpdater = object : OaidHelper.AppIdsUpdater {
        override fun onIdsValid(ids: String?) {
            Handler(Looper.getMainLooper()).post {
                ids?.run {
                    if (isNotEmpty()) {
                        SharePreferenceHelper.setOaid(this)
                    }
                }
                LogUtil.v(TAG, "mobileSafeMSA-oaid:${ids}")
            }
        }
    }

}