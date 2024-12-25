package com.example.pgphone.constant

import android.app.Activity
import com.example.pgphone.api.LocalPackageInfo
import com.example.pgphone.entity.MainDeviceInfoEntity
import com.example.pgphone.help.DeviceInfoHelper
import com.example.pgphone.help.SharePreferenceHelper

/**
 * 假数据类
 */
object DataCenter {

    /**
     * 获取首页设备信息初始化数据
     */
    fun getMainDeviceInfoData2(context: Activity): MutableList<MainDeviceInfoEntity> {
        val dataList = ArrayList<MainDeviceInfoEntity>()
//        dataList.add(MainDeviceInfoEntity(0, "机型", DeviceInfoHelper.getPhoneModel()))
        dataList.add(MainDeviceInfoEntity(1, "名称", "${DeviceInfoHelper.getPhoneBrand()} ${DeviceInfoHelper.getPhoneModel()}"))
        dataList.add(MainDeviceInfoEntity(2, "设备号", SharePreferenceHelper.getDeviceId() ?: getDefaultString()))
//        dataList.add(MainDeviceInfoEntity(3, "型号", DeviceInfoHelper.getPhoneModel()))
        dataList.add(MainDeviceInfoEntity(4, "系统", DeviceInfoHelper.getUploadSystemVersionName()))
        dataList.add(MainDeviceInfoEntity(5, "系统版本", DeviceInfoHelper.getUploadSystemVersionCode()))
        dataList.add(MainDeviceInfoEntity(6, "分辨率", DeviceInfoHelper.getPhoneResolutionRatio(context)))
        dataList.add(MainDeviceInfoEntity(7, "状态", getDefaultString()))
        dataList.add(MainDeviceInfoEntity(8, "负责人", getDefaultString()))
        dataList.add(MainDeviceInfoEntity(9, "申请到期日", getDefaultString()))
//        dataList.add(MainDeviceInfoEntity(10, "其他功能", getDefaultString()))
        return dataList
    }

    fun getMainDeviceInfoData(context: Activity): MutableList<MainDeviceInfoEntity> {
        val dataList = ArrayList<MainDeviceInfoEntity>()
        dataList.add(MainDeviceInfoEntity(1, "名称", ""))
        dataList.add(MainDeviceInfoEntity(2, "设备号", SharePreferenceHelper.getDeviceId() ?: getDefaultString()))
        dataList.add(MainDeviceInfoEntity(4, "系统", DeviceInfoHelper.getUploadSystemVersionName()))
        dataList.add(MainDeviceInfoEntity(5, "系统版本", DeviceInfoHelper.getUploadSystemVersionCode()))
        dataList.add(MainDeviceInfoEntity(6, "分辨率", DeviceInfoHelper.getPhoneResolutionRatio(context)))
        dataList.add(MainDeviceInfoEntity(7, "状态", getDefaultString()))
        dataList.add(MainDeviceInfoEntity(8, "负责人", getDefaultString()))
        dataList.add(MainDeviceInfoEntity(9, "申请到期日", getDefaultString()))
        return dataList
    }

    /**
     * 获取默认填充字符串
     */
    fun getDefaultString(): String {
        return "--"
    }

    /**
     * 填充需要收集的包名和对应code（后台统计使用）
     * @return 返回包名列表
     */
    fun getPackageList(): MutableList<LocalPackageInfo> {
        val packageList = ArrayList<LocalPackageInfo>()
        return packageList
    }

}