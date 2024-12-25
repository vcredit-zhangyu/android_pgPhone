package com.example.pgphone.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import java.util.regex.Pattern

/**
 * 状态栏工具类
 */
object StatusBarUtils {

    /**
     * 设置状态栏字体颜色
     * @param isDark true-黑色；false-白色
     */
    fun configStatusBarAppearanceLight(activity: Activity, isDark: Boolean) {
        val window = activity.window
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = isDark
    }

    /**
     * 设置导航栏字体颜色
     * @param isDark true-黑色；false-白色
     */
    fun configNavigationBarAppearanceLight(activity: Activity, isDark: Boolean) {
        val window = activity.window
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = isDark
    }

    private const val MIN_API = 19

    fun immersive(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            var systemUiVisibility = activity.window.decorView.systemUiVisibility
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            activity.window.decorView.systemUiVisibility = systemUiVisibility
        }
    }

    fun darkMode(activity: Activity, dark: Boolean) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                darkModeForM(activity.window, dark)
            }

            isFlyme4Later -> {
                darkModeForFlyme4(activity.window, dark)
            }

            isMIUI6Later -> {
                darkModeForMIUI6(activity.window, dark)
            }
        }
    }

    /**
     * android 6.0设置字体颜色
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun darkModeForM(window: Window, dark: Boolean) {
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = if (dark) {
            systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    /**
     * 设置Flyme4+的darkMode,darkMode时候字体颜色及icon变黑
     * http://open-wiki.flyme.cn/index.php?title=Flyme%E7%B3%BB%E7%BB%9FAPI
     */
    private fun darkModeForFlyme4(window: Window?, dark: Boolean): Boolean {
        var result = false
        if (window != null) {
            try {
                val e = window.attributes
                val darkFlag =
                    WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags =
                    WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(e)
                value = if (dark) {
                    value or bit
                } else {
                    value and bit.inv()
                }
                meizuFlags.setInt(e, value)
                window.attributes = e
                result = true
            } catch (var8: Exception) {
                Log.e("StatusBar", "darkIcon: failed")
            }
        }
        return result
    }

    /**
     * 设置MIUI6+的状态栏是否为darkMode,darkMode时候字体颜色及icon变黑
     * http://dev.xiaomi.com/doc/p=4769/
     */
    private fun darkModeForMIUI6(window: Window, darkmode: Boolean): Boolean {
        val clazz: Class<out Window> = window.javaClass
        return try {
            var darkModeFlag = 0
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            extraFlagField.invoke(window, if (darkmode) darkModeFlag else 0, darkModeFlag)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 判断是否Flyme4以上
     */
    private val isFlyme4Later: Boolean
        get() = (Build.FINGERPRINT.contains("Flyme_OS_4")
                || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4")
                || Pattern.compile("Flyme OS [4|5]", Pattern.CASE_INSENSITIVE)
            .matcher(Build.DISPLAY).find())

    /**
     * 判断是否为MIUI6以上
     */
    private val isMIUI6Later: Boolean
        get() = try {
            val clz = Class.forName("android.os.SystemProperties")
            val mtd = clz.getMethod("get", String::class.java)
            var `val` = mtd.invoke(null, "ro.miui.ui.version.name") as String
            `val` = `val`.replace("[vV]".toRegex(), "")
            val version = `val`.toInt()
            version >= 6
        } catch (e: Exception) {
            false
        }
    //</editor-fold>
    /**
     * 增加View的paddingTop,增加的值为状态栏高度
     */
    fun setPadding(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            view.setPadding(
                view.paddingLeft, view.paddingTop + getStatusBarHeight(context),
                view.paddingRight, view.paddingBottom
            )
        }
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)
     */
    fun setPaddingSmart(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            val lp = view.layoutParams
            if (lp != null && lp.height >= 0) {
                lp.height += getStatusBarHeight(context) //增高
            }
            view.setPadding(
                view.paddingLeft, view.paddingTop + getStatusBarHeight(context),
                view.paddingRight, view.paddingBottom
            )
        }
    }

    /**
     * 增加View的高度以及paddingTop,增加的值为状态栏高度.一般是在沉浸式全屏给ToolBar用的
     */
    fun setHeightAndPadding(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            val lp = view.layoutParams
            lp.height += getStatusBarHeight(context) //增高
            view.setPadding(
                view.paddingLeft, view.paddingTop + getStatusBarHeight(context),
                view.paddingRight, view.paddingBottom
            )
        }
    }

    /**
     * 增加View上边距（MarginTop）一般是给高度为 WARP_CONTENT 的小控件用的
     */
    fun setMargin(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= MIN_API) {
            val lp = view.layoutParams
            if (lp is ViewGroup.MarginLayoutParams) {
                lp.topMargin += getStatusBarHeight(context) //增高
            }
            view.layoutParams = lp
        }
    }

    /**
     * 获取状态栏高度
     */
    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        var result = 24
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        result = if (resId > 0) {
            context.resources.getDimensionPixelSize(resId)
        } else {
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                result.toFloat(), Resources.getSystem().displayMetrics
            ).toInt()
        }
        return result
    }

    fun getStatusBarHeightDp(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = TitleBarDisplayUtils.px2dp(context.resources.getDimensionPixelSize(resourceId))
        }
        return result
    }

}