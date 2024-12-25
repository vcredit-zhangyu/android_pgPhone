package com.example.pgphone.broadcast

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DeviceManageReceiver: DeviceAdminReceiver() {

    private val TAG = javaClass.simpleName


    override fun onEnabled(context: Context, intent: Intent) {
        Log.d(TAG, "设备管理可用 ------onEnabled-------")
        super.onEnabled(context, intent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "设备管理不可用 --------onReceive-----")
        super.onReceive(context, intent)
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
//        return super.onDisableRequested(context, intent);
        // "这是一个可选的消息，警告有关禁止用户的请求";
        return "关闭后不可使用一些功能"
    }

}