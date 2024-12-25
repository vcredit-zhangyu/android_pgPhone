package com.example.pgphone.help

import android.content.Context
import com.bun.miitmdid.core.ErrorCode
import com.bun.miitmdid.core.MdidSdkHelper
import com.bun.miitmdid.interfaces.IIdentifierListener
import com.bun.miitmdid.interfaces.IdSupplier
import com.example.pgphone.utils.LogUtil

/**
 * 获取oaid帮助类
 */
class OaidHelper(var appIdsUpdater: AppIdsUpdater?) : IIdentifierListener {

    val TAG = javaClass.simpleName

    fun getDeviceIds(cxt: Context) {
        val timeBefore = System.currentTimeMillis()
        val reflectValue = callFromReflect(cxt)
        LogUtil.v(TAG, "oaid->code码${reflectValue}")
        val timeAfter = System.currentTimeMillis()
        val offset = timeAfter - timeBefore
        LogUtil.v(TAG, "oaid->初始化耗时: ${offset}毫秒")
        when (reflectValue) {
            ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT -> {      // 1008612 不支持的设备
                appIdsUpdater?.onIdsValid("")
            }

            ErrorCode.INIT_ERROR_LOAD_CONFIGFILE -> {       // 1008613 加载配置文件出错
                appIdsUpdater?.onIdsValid("")
            }

            ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT -> {    // 1008611 不支持的设备厂商
                appIdsUpdater?.onIdsValid("")
            }

            ErrorCode.INIT_ERROR_RESULT_DELAY -> {          // 1008614 获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
                // 正常情况
            }

            ErrorCode.INIT_HELPER_CALL_ERROR -> {           // 1008615 反射调用出错
                appIdsUpdater?.onIdsValid("")
            }

            else -> {
                appIdsUpdater?.onIdsValid("")
            }
        }
        LogUtil.v(TAG, "oaid->return value:${reflectValue}")
    }


    /**
     * 通过反射调用，解决android 9以后的类加载升级，导至找不到so中的方法
     */
    private fun callFromReflect(cxt: Context): Int {
        return try {
            MdidSdkHelper.InitSdk(cxt, true, this)
        } catch (e: Exception) {
            e.printStackTrace()
            ErrorCode.INIT_HELPER_CALL_ERROR
        }
    }

    override fun OnSupport(b: Boolean, idSupplier: IdSupplier?) {
        if (idSupplier?.isSupported == true) {
            appIdsUpdater?.onIdsValid(idSupplier.oaid)
        } else {
            appIdsUpdater?.onIdsValid("")
        }
    }

    interface AppIdsUpdater {
        fun onIdsValid(ids: String?)
    }

}