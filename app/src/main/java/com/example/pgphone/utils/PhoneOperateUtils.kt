package com.example.pgphone.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast

/**
 * 手机操作相关工具类
 */
object PhoneOperateUtils {

    /**
     * 复制到剪切板
     */
    fun copyClipboard(context: Context, content: String?, label: String = "Label"): Boolean {
        if (content.isNullOrEmpty()) {
            return false
        }
        val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText(label, content)
        clip.setPrimaryClip(mClipData)
        return true
    }

    /**
     * 获取剪切板内容
     */
    fun pasteClipboard(context: Context): String {
        var content: String = ""
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        try {
            val data = cm.primaryClip
            if (data != null) {
                val item = data.getItemAt(0)
                content = item.text.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (!TextUtils.isEmpty(content)) {
            content
        } else {
            ""
        }
    }

    /**
     * 打开三方app
     */
    fun startThirdApp(context: Context, packageName: String, url: String) {
        // 根据包名获取启动首页的intent 这个intent有可能为空
        var isExit = true
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("openUrl", url)
                context.startActivity(intent)
            } else {
                isExit = false
            }
        } catch (e: Exception) {
            isExit = false
        }
        if (!isExit) {
            Toast.makeText(context, "该app未安装", Toast.LENGTH_LONG).show()
        }
    }

}