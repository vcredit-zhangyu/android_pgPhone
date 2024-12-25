package com.example.pgphone.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateUtils {

    companion object {
        const val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
        const val DATE_FORMAT_HH_MM_SS = "HH:mm:ss"
        const val DATE_FORMAT_MM_SS = "mm:ss"
    }

    fun date2String(date: Date, formatPattern: String = "yyyyMMddHHmmss"): String {
        return SimpleDateFormat(formatPattern, Locale.CHINA).format(date)
    }

    fun date2String(date: Long, formatPattern: String = "yyyyMMddHHmmss"): String {
        return SimpleDateFormat(formatPattern, Locale.CHINA).format(Date(date))
    }

    /**
     * 字符串转换成时间对象
     * @param dateString
     * @param formatStr
     * @return
     */
    fun string2date(dateString: String?, formatStr: String = "yyyyMMddHHmmss"): Date? {
        var formatDate: Date? = null
        formatDate = try {
            val format: DateFormat = SimpleDateFormat(formatStr)
            format.parse(dateString)
        } catch (e: ParseException) {
            return null
        }
        return formatDate
    }

    /**
     * Long类型数字转化为时分秒
     */
    fun long2HourMinutesSeconds(time: Long): String {
        var resultTime: String = ""
        val hour = time / 3600
        val mint = time % 3600 / 60
        val sed = time % 60
        var hourStr = hour.toString()
        hourStr = if (hour > 0) {
            if (hour < 10) {
                "0${hourStr}:"
            } else {
                "$hourStr:"
            }
        } else {
            ""
        }
        var mintStr = mint.toString()
        mintStr = if (mint < 10) {
            "0${mintStr}:"
        } else {
            "${mintStr}:"
        }
        var sedStr = sed.toString()
        if (sed < 10) {
            sedStr = "0$sedStr"
        }
        resultTime = "$hourStr$mintStr$sedStr"
        return resultTime
    }

    /**
     * 判断两个时间戳是否是同一天
     */
    fun isSameDay(millis1: Long, millis2: Long, timeZone: TimeZone = TimeZone.getDefault()): Boolean {
        val interval = millis1 - millis2
        return interval < 86400000 && interval > -86400000 && (millis2Days(millis1, timeZone) == millis2Days(millis2, timeZone))
    }

    private fun millis2Days(millis: Long, timeZone: TimeZone): Long {
        return (timeZone.getOffset(millis).toLong() + millis) / 86400000
    }

}