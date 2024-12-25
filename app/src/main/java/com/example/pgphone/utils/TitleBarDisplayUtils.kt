package com.example.pgphone.utils

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics

object TitleBarDisplayUtils {

    private var sDisplayMetrics: DisplayMetrics? = null
    private var sConfiguration: Configuration? = null
    private const val ROUND_DIFFERENCE = 0.5f

    /**
     * 初始化操作
     */
    fun init(context: Context) {
        sDisplayMetrics = context.resources.displayMetrics
        sConfiguration = context.resources.configuration
    }

    /**
     * dp 转 px
     * 注意正负数的四舍五入规则
     *
     * @param dp dp值
     * @return 转换后的像素值
     */
    fun dp2px(dp: Int): Int {
        return dp2px(dp.toFloat()).toInt()
    }

    fun dp2px(dp: Float): Float {
        return dp * (sDisplayMetrics?.density ?: DisplayMetrics.DENSITY_DEFAULT).toFloat() + if (dp > 0) ROUND_DIFFERENCE else -ROUND_DIFFERENCE
    }

    fun sp2px(sp: Int): Int {
        return sp2px(sp.toFloat()).toInt()
    }

    fun sp2px(sp: Float): Float {
        return sp * (sDisplayMetrics?.scaledDensity ?: 0.0f) + if (sp > 0) ROUND_DIFFERENCE else -ROUND_DIFFERENCE
    }

    /**
     * px 转 dp
     * 注意正负数的四舍五入规则
     *
     * @param px px值
     * @return 转换后的dp值
     */
    fun px2dp(px: Int): Int {
        return ((px / (sDisplayMetrics?.density ?: DisplayMetrics.DENSITY_DEFAULT).toInt())
                + if (px > 0) ROUND_DIFFERENCE else -ROUND_DIFFERENCE).toInt()
    }

}