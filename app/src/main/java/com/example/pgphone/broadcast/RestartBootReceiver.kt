package com.example.pgphone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.pgphone.constant.IntentConstant
import com.example.pgphone.help.ActivityHelper


class RestartBootReceiver : BroadcastReceiver() {

    private val TAG = javaClass.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // 在这里启动你的App
            Log.v(TAG, "---我的app启动了")
//            ActivityHelper.startMainActivity(context)
            ActivityHelper.setAlarm(context)
        } else if (IntentConstant.restartBootReceiverAction == intent.action) {
            Log.v(TAG, "---我的广播收到了")
//            ActivityHelper.startMainActivity(context)
            ActivityHelper.setAlarm(context)
        }
    }

}