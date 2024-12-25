package com.example.pgphone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pgphone.constant.IntentConstant
import com.example.pgphone.help.ActivityHelper

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.run {
            if (action == IntentConstant.restartAlarmAction) {
                ActivityHelper.startMainActivity(context)
            }
        }
    }

}