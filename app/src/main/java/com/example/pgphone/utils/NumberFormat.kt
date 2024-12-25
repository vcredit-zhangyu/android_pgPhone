package com.example.pgphone.utils

import android.text.TextUtils
import java.lang.Exception
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * 数字格式化
 */
class NumberFormat {

    companion object {

        val commaSeparateTowType = "#,###.00"   // 格式化数字类型，整数部分用逗号分隔，保留两位有效数字
        val commaSeparateType = "#,###"         // 格式化数字类型，整数部分用逗号分隔，只保留整数部分

    }

    /**
     * 按type定制自己需要的类型 NumberFormat
     *
     * @param num
     * @param type 添加分隔符（例如"#,###"、"#,###.00"）其他类型（例如"#.0"、"#%"等）
     * @return
     */
    fun decimalFormat(num: Double, type: String): String {
        val decimalFormat = DecimalFormat(type)
        return decimalFormat.format(num)
    }

    fun stringFormat(number: String, type: String? = null): String {
        try {
            val decimalFormat = if (type.isNullOrEmpty()) DecimalFormat() else DecimalFormat(type)
            return decimalFormat.format(NumberFormat.getInstance().parse(number))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 对字符串数字取整
     * 用法 NumberFormat().bigDecimalFormat(“20000013", -2) // 百位取整
     * @param number 需要处理的数据
     * @param scale 返回结果的比例
     * @param roundingMode 取整类型 - 向上取整/向下取整
     */
    fun bigDecimalFormat(number: String, scale: Int = 0, roundingMode: Int = BigDecimal.ROUND_DOWN): String {
        try {
            var bigDecimal = BigDecimal(number)
            bigDecimal = bigDecimal.setScale(scale, roundingMode)
            return bigDecimal.toLong().toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 屏蔽手机号中间段
     *
     * @param replace
     * @return
     */
    fun hidePhoneNumberMiddle4(replace: String): String {
        if (TextUtils.isEmpty(replace)) {
            return ""
        } else if (replace.length < 11) {
            return replace
        }
        val sb = StringBuffer()
        sb.append(replace.substring(0, 3))
        for (i in 3 until replace.length - 4) {
            sb.append("*")
        }
        sb.append(replace.substring(replace.length - 4))
        return sb.toString()
    }

    /**
     * 获取手机号后几位 默认6位
     *
     * @param replace
     * @return
     */
    fun getPhoneNumberEnd(replace: String?, length: Int = 6): String {
        if (replace.isNullOrEmpty() || length <= 0) {
            return ""
        } else if (length > replace.length || replace.length < 11) {
            return replace
        }
        return replace.substring(replace.length - length)
    }

    // 字符串中转为int类型
    fun stringConvertInt(content: String?, defaultValue: Int = 0): Int {
        if (content.isNullOrEmpty()) {
            return defaultValue
        }
        try {
            return content.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defaultValue
    }

    // 字符串中转为double类型
    fun stringConvertDouble(content: String?, defaultValue: Double = 0.0): Double {
        if (content.isNullOrEmpty()) {
            return defaultValue
        }
        try {
            return content.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defaultValue
    }

    // 字符串中转为long类型
    fun stringConvertLong(content: String?, defaultValue: Long = 0L): Long {
        if (content.isNullOrEmpty()) {
            return defaultValue
        }
        try {
            return content.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defaultValue
    }

}