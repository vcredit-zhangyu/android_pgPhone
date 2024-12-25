package com.example.pgphone.utils

import android.util.Log
import com.example.pgphone.BuildConfig

/**
 * Log打印工具类
 */
object LogUtil {

    /**
     * isPrint: print switch, true will print. false not print
     */
    private val isPrint = BuildConfig.DEBUG
    private var defaultTag = javaClass.simpleName

    fun setTag(tag: String) {
        defaultTag = tag
    }

    fun i(o: Any?): Int {
        return if (isPrint && o != null) Log.i(defaultTag, o.toString()) else -1
    }

    /**
     * ******************** Log默认tag **************************
     */

    fun v(m: String?) {
        if (isPrint && m != null) Log.v(defaultTag, m)
    }

    fun d(m: String?) {
        if (isPrint && m != null) Log.d(defaultTag, m)
    }

    fun i(m: String?) {
        if (isPrint && m != null) Log.i(defaultTag, m)
    }

    fun w(m: String?) {
        if (isPrint && m != null) Log.w(defaultTag, m)
    }

    fun e(m: String?) {
        if (isPrint && m != null) Log.e(defaultTag, m)
    }

    /**
     * ******************** Log **************************
     */
    fun v(tag: String, msg: String?) {
        if (isPrint && msg != null){
            Log.v(tag, msg)
//            printMsg(Log.VERBOSE, tag, msg)
        }
    }

    fun d(tag: String?, msg: String?) {
        if (isPrint && msg != null) Log.d(tag, msg)
    }

    fun i(tag: String?, msg: String?) {
        if (isPrint && msg != null) Log.i(tag, msg)
    }

    fun w(tag: String?, msg: String?) {
        if (isPrint && msg != null) Log.w(tag, msg)
    }

    fun e(tag: String?, msg: String?) {
        if (isPrint && msg != null) Log.e(tag, msg)
    }

    private fun getLogMessage(vararg msg: Any?): String? {
        if (msg.isNotEmpty()) {
            val sb = StringBuilder()
            for (s in msg) {
                if (s != null) {
                    sb.append(s.toString())
                }
            }
            return sb.toString()
        }
        return ""
    }

    /**
     * ******************** Log with Throwable **************************
     */
    fun v(tag: String?, msg: String?, tr: Throwable?) {
        if (isPrint && msg != null) Log.v(tag, msg, tr)
    }

    fun d(tag: String?, msg: String?, tr: Throwable?) {
        if (isPrint && msg != null) Log.d(tag, msg, tr)
    }

    fun i(tag: String?, msg: String?, tr: Throwable?) {
        if (isPrint && msg != null) Log.i(tag, msg, tr)
    }

    fun w(tag: String?, msg: String?, tr: Throwable?) {
        if (isPrint && msg != null) Log.w(tag, msg, tr)
    }

    fun e(tag: String?, msg: String?, tr: Throwable?) {
        if (isPrint && msg != null) Log.e(tag, msg, tr)
    }

    private const val MAX_LEN = 4000
    private val LINE_SEP = System.getProperty("line.separator")

    private fun printMsg(type: Int, tag: String, msg: String) {
        val len = msg.length
        val countOfSub: Int = len / MAX_LEN
        if (countOfSub > 0) {
            var index = 0
            for (i in 0 until countOfSub) {
                printSubMsg(type, tag, msg.substring(index, index + MAX_LEN))
                index += MAX_LEN
            }
            if (index != len) {
                printSubMsg(type, tag, msg.substring(index, len))
            }
        } else {
            printSubMsg(type, tag, msg)
        }
    }

    private fun printSubMsg(type: Int, tag: String, msg: String) {
        Log.println(type, tag, msg)
    }

}