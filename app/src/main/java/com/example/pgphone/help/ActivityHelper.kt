package com.example.pgphone.help

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.pgphone.MainActivity
import com.example.pgphone.broadcast.AlarmReceiver
import com.example.pgphone.constant.IntentConstant


object ActivityHelper {

    /**
     * 启动MainActivity
     */
    fun startMainActivity(context: Context) {
        val appIntent = Intent(context, MainActivity::class.java)
        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(appIntent)
    }

    /**
     * 设置闹钟
     */
    fun setAlarm(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = IntentConstant.restartAlarmAction
        val sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager?
        alarmManager?.set(AlarmManager.RTC, System.currentTimeMillis(), sender)     // c为设置闹钟的时间的Calendar对象
    }

}