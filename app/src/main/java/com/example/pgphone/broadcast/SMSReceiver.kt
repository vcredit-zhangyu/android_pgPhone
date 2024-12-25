package com.example.pgphone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log

class SMSReceiver : BroadcastReceiver() {

    private val TAG = javaClass.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.v(TAG, "---短信广播：${intent.action}")
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            bundle?.apply {
                val pdus = bundle.get("pdus") as Array<*>
                for (i in pdus.indices) {
                    val sms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        SmsMessage.createFromPdu(pdus[i] as ByteArray, intent.getStringExtra("format"))
                    } else {
                        null
                    }
                    val phoneNumber = sms?.originatingAddress
                    val message = sms?.messageBody
                    val time = sms?.timestampMillis
                    val serviceCenterAddress = sms?.serviceCenterAddress
                    val status = sms?.status

                    val stringBuilder = StringBuilder()
                    stringBuilder.append("phoneNumber=")
                    stringBuilder.append(phoneNumber)
                    stringBuilder.append("---")
                    stringBuilder.append("message=")
                    stringBuilder.append(message)
                    stringBuilder.append("---")
                    stringBuilder.append("time=")
                    stringBuilder.append(time)
                    stringBuilder.append("---")
                    stringBuilder.append("serviceCenterAddress=")
                    stringBuilder.append(serviceCenterAddress)
                    stringBuilder.append("---")
                    stringBuilder.append("status=")
                    stringBuilder.append(status)
                    stringBuilder.append("---")
                    Log.v(TAG, "---短信内容：${stringBuilder.toString()}")
//                    SmsDispatcher.dispatch(context, phoneNumber.orEmpty(), message, time)
                }
            }
        }
    }
}