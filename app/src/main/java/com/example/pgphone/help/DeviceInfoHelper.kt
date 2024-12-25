package com.example.pgphone.help

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.pgphone.base.globalApplication
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * 设备信息帮助类
 */
object DeviceInfoHelper {

    // 获取机型 安卓/苹果/鸿蒙
    fun getPhoneClient(): String {
        return if (isHarmonyOs()) {
            "Harmony-${getHarmonyVersion()}"
        } else {
            "Android"
        }
    }

    /**
     * 获取上传所需的系统版本名称 安卓/鸿蒙
     */
    fun getUploadSystemVersionName() : String {
        return if (isHarmonyOs()) {
            "鸿蒙"
        } else {
            "安卓"
        }
    }

    /**
     * 获取上传所需的系统版本号
     */
    fun getUploadSystemVersionCode() : String {
        return if (isHarmonyOs()) {
            getHarmonyVersion().toString()
        } else {
            getPhoneSystemVersionNumber()
        }
    }

    /**
     * 是否为鸿蒙系统
     *
     * @return true为鸿蒙系统
     */
    fun isHarmonyOs(): Boolean {
        return try {
            val buildExClass = Class.forName("com.huawei.system.BuildEx")
            val osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass)
            "Harmony".equals(osBrand.toString(), ignoreCase = true)
        } catch (x: Throwable) {
            false
        }
    }

    /**
     * 获取鸿蒙系统版本号
     *
     * @return 版本号
     */
    fun getHarmonyVersion(): String? {
        return getProp("hw_sc.build.platform.version", "")
    }

    private fun getProp(property: String, defaultValue: String): String? {
        try {
            val spClz = Class.forName("android.os.SystemProperties")
            val method = spClz.getDeclaredMethod("get", String::class.java)
            val value = method.invoke(spClz, property) as String
            return if (TextUtils.isEmpty(value)) {
                defaultValue
            } else value
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return defaultValue
    }

    // 获取手机名称
    fun getPhoneName() {

    }

    // 获取手机品牌
    fun getPhoneBrand(): String {
        return Build.BRAND
    }

    // 获取手机型号
    fun getPhoneModel(): String {
        return Build.MODEL
    }

    // 获取手机系统类型
    fun getPhoneSystemType() {

    }

    // 获取手机系统版本号
    fun getPhoneSystemVersionNumber(): String {
        return Build.VERSION.RELEASE
    }

    // 获取手机系统版本名称
    fun getPhoneSystemVersionName(): String {
        return Build.DISPLAY
    }

    // 获取手机imei
    fun getImei() {

    }

    // 获取手机唯一版本号
    fun getDeviceId(): String? {
        return SharePreferenceHelper.getOaid()
    }

    // 获取手机分辨率
    fun getPhoneResolutionRatio(context: Activity): String {
        val dm = DisplayMetrics()
        val mWm: WindowManager? = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager?
        mWm?.defaultDisplay?.getRealMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        return "$width*$height"
//        return "${getScreenWidth(context)}*${getScreenHeight(context)}"
    }

    // 获取手机尺寸
    fun getPhoneSize(context: Context): String {
        return getPhysicsScreenSize(context).toString()
    }

    // 获取手机颜色
    fun getPhoneColor() {

    }

    /** 获取手机屏幕宽  */
    fun getScreenWidth(context: Activity): Int {
        return context.windowManager.defaultDisplay.width
    }

    /** 获取手机屏高宽  */
    fun getScreenHeight(context: Activity): Int {
        return context.windowManager.defaultDisplay.height
    }

    /**
     * 获取应用版本号
     *
     * @return
     */
    fun getAppVersionName(): String {
        val packageManager: PackageManager = globalApplication.packageManager
        var versionName = "1.0.0"
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                globalApplication.packageName, 0
            )
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace();
        }
        return versionName
    }

    /**
     * 获取应用版本号
     *
     * @return
     */
    fun getAppVersionCode(): Long {
        val packageManager: PackageManager = globalApplication.packageManager
        var versioncode = 0L
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                globalApplication.packageName, 0
            )
            versioncode = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                packageInfo.versionCode.toLong()
            } else {
                packageInfo.longVersionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versioncode
    }

    fun getDeviceUseTime(context: Context, packageName: String): String {
        val packageManager = context.packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val installTime = packageInfo.firstInstallTime
            val lastUpdateTime = packageInfo.lastUpdateTime
            return timeFormat(lastUpdateTime)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    // 格式化时间戳
    fun timeFormat(time: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        return dateFormat.format(time)
    }

    /**
     * 根据包名判断是否已安装
     */
    fun packageIsInstall(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        val installedPackages = packageManager.getInstalledPackages(0)
        if (installedPackages.isNotEmpty()) {
            val packageInfo = installedPackages.find { it.packageName == packageName }
            if (packageInfo != null) {
                return true
            }
        }
        return false
    }

    // ----  非需要
    // 获取手机主板
    fun getPhoneBoard(): String {
        return Build.BOARD
    }

    // 获取手机制造商
    fun getPhoneManufacturer(): String {
        return Build.MANUFACTURER
    }

    /**
     * 获取手机工业设备名称
     * 设备名
     */
    fun getPhoneDevice(): String {
        return Build.DEVICE
    }

    /**
     * 得到屏幕的物理尺寸，由于该尺寸是在出厂时，厂商写死的，所以仅供参考
     * 计算方法：获取到屏幕的分辨率:point.x和point.y，再取出屏幕的DPI（每英寸的像素数量），
     * 计算长和宽有多少英寸，即：point.x / dm.xdpi，point.y / dm.ydpi，屏幕的长和宽算出来了，
     * 再用勾股定理，计算出斜角边的长度，即屏幕尺寸。
     *
     * @param context
     * @return
     */
    fun getPhysicsScreenSize(context: Context): Double {
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        manager.defaultDisplay.getRealSize(point)
        val dm = context.resources.displayMetrics
        val densityDpi = dm.densityDpi      // 得到屏幕的密度值，但是该密度值只能作为参考，因为他是固定的几个密度值。
        val x = (point.x / dm.xdpi).toDouble().pow(2.0)     // dm.xdpi是屏幕x方向的真实密度值，比上面的densityDpi真实。
        val y = (point.y / dm.ydpi).toDouble().pow(2.0)     // dm.xdpi是屏幕y方向的真实密度值，比上面的densityDpi真实。
        return sqrt(x + y)
    }


}