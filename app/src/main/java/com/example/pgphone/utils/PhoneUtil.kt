package com.example.pgphone.utils

import android.content.Context

/**
 * 手机相关信息工具类
 */
object PhoneUtil {

    /**
     * 获取屏幕宽度
     */
    fun getPhoneWidth(context: Context?):Int {
        val widthPixels = context?.resources?.displayMetrics?.widthPixels
        return widthPixels ?: 0
    }

    /**
     * 获取屏幕高度
     */
    fun getPhoneHeight(context: Context?):Int {
        val heightPixels = context?.resources?.displayMetrics?.heightPixels
        return heightPixels ?: 0
    }

    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    fun dp2Px(context: Context, dp: Float): Int {
        return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    /**
     * px转dp
     *
     * @param px
     * @return
     */
    fun px2Dp(context: Context, px: Float): Int {
        return (px / context.resources.displayMetrics.density + 0.5f).toInt()
    }


    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    fun px2Sp(context: Context, pxValue: Int): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2Px(context: Context, spValue: Int): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

}