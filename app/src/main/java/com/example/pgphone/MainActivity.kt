package com.example.pgphone

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pgphone.adapter.MainDeviceInfoAdapter
import com.example.pgphone.api.MainApi
import com.example.pgphone.api.UploadUseInfo
import com.example.pgphone.base.BaseActivity
import com.example.pgphone.broadcast.RestartBootReceiver
import com.example.pgphone.broadcast.SMSReceiver
import com.example.pgphone.constant.DataCenter
import com.example.pgphone.constant.IntentConstant
import com.example.pgphone.entity.MainDeviceInfoEntity
import com.example.pgphone.extension.dismissLoading
import com.example.pgphone.extension.showLoading
import com.example.pgphone.extension.showToast
import com.example.pgphone.help.DeviceInfoHelper
import com.example.pgphone.help.SharePreferenceHelper
import com.example.pgphone.interfaces.DialogConfirmInterface
import com.example.pgphone.interfaces.InputDialogInterface
import com.example.pgphone.interfaces.OnItemClickListener
import com.example.pgphone.manager.UpdateManager
import com.example.pgphone.ui.MoreAppFunctionActivity
import com.example.pgphone.ui.QrcodeGenerateActivity
import com.example.pgphone.utils.DateUtils
import com.example.pgphone.utils.LogUtil
import com.example.pgphone.utils.StatusBarUtils
import com.example.pgphone.view.dialog.DeviceIdDialog
import com.example.pgphone.view.dialog.FeedbackDialog
import com.example.pgphone.view.dialog.RemindDialog
import com.example.pgphone.view.dialog.UseExpireDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Date
import java.util.Locale


/**
 * 主界面
 */
class MainActivity : BaseActivity() {

    private val TAG = javaClass.simpleName
    private var phoneInfo: String = ""
    private var mainDeviceInfoAdapter: MainDeviceInfoAdapter? = null
    private var mainDeviceInfoList: MutableList<MainDeviceInfoEntity>? = null
    private var deviceIdDialog: DeviceIdDialog? = null
    private var remindDialog: RemindDialog? = null
    private var curDeviceId: String? = ""
    private var expireDialog: UseExpireDialog? = null
    private var feedbackDialog: FeedbackDialog? = null
    private var updateManager: UpdateManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
        initLoad()
    }

    private fun initView() {
        statusBarImageView.layoutParams.height = StatusBarUtils.getStatusBarHeight(this)
        if (mainDeviceInfoList == null) {
            mainDeviceInfoList = ArrayList<MainDeviceInfoEntity>()
        }
        mainDeviceInfoList?.addAll(DataCenter.getMainDeviceInfoData(this))
        mainDeviceInfoList?.run {
            mainDeviceInfoAdapter = MainDeviceInfoAdapter(this)
        }
        deviceInfoRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        deviceInfoRecyclerView.adapter = mainDeviceInfoAdapter
        appVersionTextView.text =
            getString(R.string.app_version_format, DeviceInfoHelper.getAppVersionName(), DeviceInfoHelper.getAppVersionCode().toString())
        mainDeviceInfoAdapter?.setOnClickListener(object : OnItemClickListener<MainDeviceInfoEntity> {
            override fun onClick(entity: MainDeviceInfoEntity) {
                when (entity.index) {
                    10 -> {
                        startActivity(Intent(this@MainActivity, MoreAppFunctionActivity::class.java))
                    }
                }
            }
        })
        feedbackLinearLayout.isVisible = false
//        nestedScrollView.scrollTo(0, 0)
//        if (PhoneOperateUtils.copyClipboard(this, phoneInfo)) {
//            Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show()
//        }

        loadVersionInfo()
    }

    private fun initListener() {
        feedbackLinearLayout.setOnClickListener {
            showFeedbackDialog()
        }
    }

    private fun initLoad() {
        loadPhoneInfo()
    }

    private fun sendAlarmBroadcast() {
        val intent = Intent(this, RestartBootReceiver::class.java)
        intent.action = IntentConstant.restartBootReceiverAction
        sendBroadcast(intent)
    }

    private fun autoStart(context: Context) {
        val localComponentName = ComponentName(context, RestartBootReceiver::class.java)
        val i: Int = context.packageManager.getComponentEnabledSetting(localComponentName)
        LogUtil.e(TAG, "自启动 >>>>onCreate: $i")

        val newIntent: Intent = getAutostartSettingIntent(context)
        try {
            context.startActivity(newIntent)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 获取自启动管理页面的Intent
     *
     * @param context context
     * @return 返回自启动管理页面的Intent
     */
    private fun getAutostartSettingIntent(context: Context): Intent {
        var componentName: ComponentName? = null
        val brand = Build.MANUFACTURER
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        when (brand.lowercase(Locale.getDefault())) {
            "samsung" -> componentName = ComponentName("com.samsung.android.sm", "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity")
            "huawei" -> {
                Log.e("自启动管理 >>>>", "getAutostartSettingIntent: 华为")
                componentName = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")
            }

            "xiaomi" -> //                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                componentName = ComponentName("com.android.settings", "com.android.settings.BackgroundApplicationsManager")

            "vivo" -> //            componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.safaguard.PurviewTabActivity");
                componentName = ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")

            "oppo" -> //            componentName = new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                componentName = ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")

            "yulong", "360" -> componentName =
                ComponentName("com.yulong.android.coolsafe", "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity")

            "meizu" -> componentName = ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity")
            "oneplus" -> componentName = ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity")
            "letv" -> {
                intent.action = "com.letv.android.permissionautoboot"
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", context.packageName, null)
            }

            else -> {
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", context.packageName, null)
            }
        }
        intent.component = componentName
        return intent
    }

    // 收集收集信息
    private fun collectPhoneInfo(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("手机品牌=")
        stringBuilder.append(DeviceInfoHelper.getPhoneBrand())
        stringBuilder.append("\n")
        stringBuilder.append("手机型号=")
        stringBuilder.append(DeviceInfoHelper.getPhoneModel())
        stringBuilder.append("\n")
        stringBuilder.append("手机系统版本号=")
        stringBuilder.append(DeviceInfoHelper.getPhoneSystemVersionNumber())
        stringBuilder.append("\n")
        stringBuilder.append("手机系统版本名称=")
        stringBuilder.append(DeviceInfoHelper.getPhoneSystemVersionName())
        stringBuilder.append("\n")
        stringBuilder.append("手机分辨率=")
        stringBuilder.append(DeviceInfoHelper.getPhoneResolutionRatio(this))
        stringBuilder.append("\n")
        stringBuilder.append("手机尺寸=")
        stringBuilder.append(DeviceInfoHelper.getPhoneSize(this))
        stringBuilder.append("\n")
        stringBuilder.append("手机是否为安卓系统=")
        stringBuilder.append(DeviceInfoHelper.getPhoneClient())
        stringBuilder.append("\n")
        stringBuilder.append("设备id=")
        stringBuilder.append(DeviceInfoHelper.getDeviceId())
        stringBuilder.append("\n")

        stringBuilder.append("\n")
        stringBuilder.append("手机主板=")
        stringBuilder.append(DeviceInfoHelper.getPhoneBoard())
        stringBuilder.append("\n")
        stringBuilder.append("手机厂商=")
        stringBuilder.append(DeviceInfoHelper.getPhoneManufacturer())
        stringBuilder.append("\n")
        stringBuilder.append("手机工业设备名称=")
        stringBuilder.append(DeviceInfoHelper.getPhoneDevice())
        stringBuilder.append("\n")
        LogUtil.e(TAG, "手机信息->\n${stringBuilder.toString()}")
        return stringBuilder.toString()
    }

    private fun showDeviceIdDialog(content: String? = null) {
        dismissDeviceIdDialog()
        if (deviceIdDialog == null) {
            deviceIdDialog = DeviceIdDialog(this)
        }
        deviceIdDialog?.setInputImei(content)
        deviceIdDialog?.setOnInputDialogInterface(object : InputDialogInterface {
            override fun onInput(content: String) {
                doRequestGetPhoneInfo(content)
            }

            override fun onClose() {

            }
        })
        deviceIdDialog?.show()
    }

    private fun dismissDeviceIdDialog() {
        deviceIdDialog?.run {
            if (isShowing) dismiss()
            deviceIdDialog = null
        }
    }

    private fun showExpireDialog() {
        dismissExpireDialog()
        if (expireDialog == null) {
            expireDialog = UseExpireDialog(this)
        }
        expireDialog?.show()
    }

    private fun dismissExpireDialog() {
        expireDialog?.run {
            if (isShowing) dismiss()
            expireDialog = null
        }
    }

//    // Register the launcher and result handler QrcodeScanResultActivity
//    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> =
//        registerForActivityResult<ScanOptions, ScanIntentResult>(ScanContract(), ActivityResultCallback<ScanIntentResult> { result ->
//            if (result.contents == null) {
//                Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_LONG).show()
//            } else {
//                val resultInfo = result.contents
//                Log.v(TAG, "---Scanned=${resultInfo}")
////                Toast.makeText(this@MainActivity, "Scanned: $resultInfo", Toast.LENGTH_LONG).show()
//                if (resultInfo.endsWith(".apk")) {
//                    downLoadFile(resultInfo)
//                } else {
//                    startScanResult(resultInfo)
//                }
//            }
//        })
//
//    // 开始扫描二维码
//    private fun launchQrCode() {
//        barcodeLauncher.launch(ScanOptions())
//    }
//
//    /**
//     * 下载文件
//     */
//    private fun downLoadFile(url: String) {
//        val uri = Uri.parse(url)
//        val intent = Intent(Intent.ACTION_VIEW, uri)
//        startActivity(intent)
//    }
//
//    /**
//     * 打开浏览结果页
//     */
//    private fun startScanResult(content: String) {
//        val intent = Intent(this, QrcodeScanResultActivity::class.java)
//        intent.putExtra(IntentConstant.activityIntentContent, content)
//        startActivity(intent)
//    }

    /**
     * 生成二维码
     */
    private fun startQrcodeGenerate(content: String) {
        val intent = Intent(this, QrcodeGenerateActivity::class.java)
        intent.putExtra(IntentConstant.activityIntentContent, content)
        startActivity(intent)
    }

    private fun registerReceiverSms() {
        if (smsReceiver == null) {
            smsReceiver = SMSReceiver()
        }

        val smsFilter = IntentFilter()
        smsFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
        smsFilter.addAction("android.provider.Telephony.SMS_DELIVER")
        registerReceiver(smsReceiver, smsFilter)
    }

    /**
     * 权限的验证及处理，相关方法
     */
    private fun getReadPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val permissionReadSms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.RECEIVE_SMS
                    ) or ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ) { //是否请求过该权限
                    ActivityCompat.requestPermissions(
                        this, arrayOf<String>(
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_SMS,
                        ), 10001
                    )
                } else { //没有则请求获取权限，示例权限是：存储权限和短信权限，需要其他权限请更改或者替换
                    ActivityCompat.requestPermissions(
                        this, arrayOf<String>(
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_SMS,
                        ), 10001
                    )
                }
            } else { //如果已经获取到了权限则直接进行下一步操作
                registerReceiverSms()
                Log.e(TAG, "onRequestPermissionsResult")
            }
        }
    }

    /**
     * 一个或多个权限请求结果回调
     * 当点击了不在询问，但是想要实现某个功能，必须要用到权限，可以提示用户，引导用户去设置
     */
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            10001 -> {
                var i = 0
                while (i < grantResults.size) {

//                   如果拒绝获取权限
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        val flag = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]!!)
                        if (flag) {
                            getReadPermissions()
                            return  //用户权限是一个一个的请求的，只要有拒绝，剩下的请求就可以停止，再次请求打开权限了
                        } else { // 勾选不再询问，并拒绝
                            Toast.makeText(this, "请到设置中打开权限", Toast.LENGTH_LONG).show()
                            return
                        }
                    }
                    i++
                }
            }

            else -> {}
        }
    }

    private var smsReceiver: SMSReceiver? = null

    private fun unRegisterReceiverSms() {
        smsReceiver?.run {
            unregisterReceiver(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissDeviceIdDialog()
        unRegisterReceiverSms()
        dismissRemindDialog()
        dismissExpireDialog()
        dismissFeedbackDialog()
    }

    private fun commitPhoneInfo() {
        val bodyMap = HashMap<String, Any>()
        bodyMap["deviceName"] = ""  // 设备名称
        bodyMap["deviceNo"] = ""    // 设备号
        bodyMap["deviceType"] = ""  // 设备机型 苹果：ios 安卓：android

        bodyMap["deviceBrand"] = "" // 设备品牌
        bodyMap["deviceModel"] = "" // 设备型号
        bodyMap["deviceSystem"] = ""    // 设备系统
        bodyMap["deviceSystemVersion"] = "" // 设备系统版本
        bodyMap["deviceLoginUser"] = ""     // 设备用户名
        bodyMap["deviceLoginKey"] = ""      // 设备秘钥
        bodyMap["deviceImei"] = ""          // IMEI
        bodyMap["deviceSerialNo"] = ""      // 序列化
        bodyMap["deviceResolution"] = ""    // 分辨率
        bodyMap["deviceColor"] = ""         // 颜色
        bodyMap["deviceSize"] = ""          // 尺寸
        bodyMap["deviceAccessory"] = ""     // 配件
        bodyMap["deviceRemark"] = ""        // 备注
        bodyMap["commitPerson"] = ""        // 提交人
        MainApi.commitPhoneInfo(bodyMap).setSuccessListener {
            LogUtil.v(TAG, "---数据返回suc=${it?.data}")
        }.setErrorListener { errorType, errorCode, errorMsg ->
            LogUtil.v(TAG, "---数据返回fail=${errorMsg}")
        }
    }

    /**
     *
     * @param reQueryDeviceId 重新查询设备唯一号
     */
    private fun loadPhoneInfo(reQueryDeviceId: String = "") {
        if (reQueryDeviceId.isEmpty()) {
            val deviceId = SharePreferenceHelper.getDeviceId()
            if (deviceId.isNullOrEmpty()) {
                showDeviceIdDialog()
            } else {
                doRequestGetPhoneInfo(deviceId)
            }
        } else {
            doRequestGetPhoneInfo(reQueryDeviceId)
        }
    }

    private fun doRequestGetPhoneInfo(deviceId: String) {
        showLoading()
        MainApi.getPhoneInfo(deviceId).setSuccessListener {
            dismissLoading()
            LogUtil.v(TAG, "---数据返回suc=${it?.data}")
            it?.data?.run {
                deviceTitleTextView.text = deviceModel
                if (!deviceName.isNullOrEmpty()) {
                    mainDeviceInfoList?.get(0)?.content = deviceName
                }
                curDeviceId = deviceNo
                if (!deviceNo.isNullOrEmpty()) {
                    SharePreferenceHelper.setDeviceId(deviceNo)
                    mainDeviceInfoList?.get(1)?.content = deviceNo
                }
                if (!deviceStatus.isNullOrEmpty()) {
                    mainDeviceInfoList?.get(5)?.content = deviceStatus
                }
                if (!deviceCharge.isNullOrEmpty()) {
                    mainDeviceInfoList?.get(6)?.content = deviceCharge
                }
                if (!expireDate.isNullOrEmpty()) {
                    mainDeviceInfoList?.get(7)?.content = expireDate
                }
                mainDeviceInfoAdapter?.notifyDataSetChanged()

                val resultSystemVersion = deviceSystemVersion
                val localSystemVersion = DeviceInfoHelper.getUploadSystemVersionCode()
                if (!resultSystemVersion.isNullOrEmpty() && !localSystemVersion.isNullOrEmpty()) {
                    if (resultSystemVersion != localSystemVersion) {
                        // 更新版本信息
                        updatePhoneInfo(localSystemVersion)
                    }
                }

                deviceNo?.run {
                    deviceCharge?.let {  chargeIt ->
                        doCommitPhoneUseRecord(this, chargeIt)
                        updateActiveDate(this)
                    }
                }

                val curYearMonthDay = DateUtils().date2String(Date(), DateUtils.DATE_FORMAT_YYYY_MM_DD)
                val curDayMillis = DateUtils().string2date(curYearMonthDay, DateUtils.DATE_FORMAT_YYYY_MM_DD)?.time
                val resultDayMillis = DateUtils().string2date(expireDate, DateUtils.DATE_FORMAT_YYYY_MM_DD)?.time
//                LogUtil.v(TAG, "年月日-->curDayMillis=${curDayMillis}-resultDayMillis=${resultDayMillis}")
                if (curDayMillis != null && resultDayMillis != null && resultDayMillis < curDayMillis) {
                    feedbackLinearLayout.isVisible = false
                    showExpireDialog()
                } else {
                    feedbackLinearLayout.isVisible = true
                    dismissExpireDialog()
                }
            }
        }.setErrorListener { errorType, errorCode, errorMsg ->
            LogUtil.v(TAG, "---数据返回fail-errorCode=${errorCode}-errorMsg=${errorMsg}")
            dismissLoading()
            if (errorCode == 99) {
                showRemindDialog(deviceId)
            } else {
                showToast(errorMsg)
            }
        }
    }

    // 提交设备使用记录
    private fun doCommitPhoneUseRecord(deviceId: String, userName: String) {
        val packageList = DataCenter.getPackageList()
        val uploadUseList = ArrayList<UploadUseInfo>()
        packageList.forEach {
            val useTime = DeviceInfoHelper.getDeviceUseTime(this, it.packageName)
            if (useTime.isNotEmpty()) {
                uploadUseList.add(UploadUseInfo(deviceId, it.packageCode, useTime))
            }
        }
        if (uploadUseList.isNotEmpty()) {
            requestCommitPhoneUseRecord(uploadUseList)
        }
    }

    private fun requestCommitPhoneUseRecord(appContent: ArrayList<UploadUseInfo>) {
        MainApi.commitPhoneUseRecord(appContent).setSuccessListener {
            LogUtil.v(TAG, "---commitPhoneUseRecord-suc=${it?.data}")
        }.setErrorListener { errorType, errorCode, errorMsg ->
            LogUtil.v(TAG, "---commitPhoneUseRecord-fail=${errorMsg}")
        }
    }

    private fun showRemindDialog(deviceId: String) {
        dismissRemindDialog()
        if (remindDialog == null) {
            remindDialog = RemindDialog(this)
        }
        remindDialog?.setTitleText("提示")
        remindDialog?.setContentText("未查询到设备，请确认输入的设备唯一号")
        remindDialog?.setLeftButtonText("重新查询")
        remindDialog?.setRightButtonText("重新输入")
        remindDialog?.setOnDialogConfirmInterface(object: DialogConfirmInterface {
            override fun onConfirm() {  // 重新输入
                showDeviceIdDialog()
            }

            override fun onCancel() {   // 重新查询
                loadPhoneInfo(deviceId)
            }
        })
        remindDialog?.show()
    }

    private fun dismissRemindDialog() {
        remindDialog?.run {
            if (isShowing) {
                dismiss()
            }
            remindDialog = null
        }
    }

    override fun onResume() {
        super.onResume()
        if (!curDeviceId.isNullOrEmpty()) {
            curDeviceId?.run {
                doCommitPhoneUseRecord(this, "")
                updateActiveDate(this)
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    // 弹出意见反馈弹窗  FeedbackDialog
    private fun showFeedbackDialog() {
        dismissFeedbackDialog()
        if (feedbackDialog == null) {
            feedbackDialog = FeedbackDialog(this)
        }
        feedbackDialog?.setOnInputDialogInterface(object : InputDialogInterface {
            override fun onInput(content: String) {
                doRequestFeedback(content)
            }

            override fun onClose() {

            }
        })
        feedbackDialog?.show()
    }

    // 消失/回收意见反馈弹窗
    private fun dismissFeedbackDialog() {
        feedbackDialog?.run {
            if (isShowing) dismiss()
            feedbackDialog = null
        }
    }

    // 提交意见反馈
    private fun doRequestFeedback(content: String) {
        curDeviceId?.run {
            MainApi.commitFeedbackInfo(this, content).setSuccessListener {
                showToast("提交成功！")
                LogUtil.v(TAG, "---commitFeedbackInfo-suc=${it?.data}")
            }.setErrorListener { errorType, errorCode, errorMsg ->
                showToast(errorMsg)
                LogUtil.v(TAG, "---commitFeedbackInfo-fail=${errorMsg}")
            }
        }
    }

    // 更新手机信息
    private fun updatePhoneInfo(content: String) {
        curDeviceId?.run {
            MainApi.updatePhoneInfo(this, content).setSuccessListener {
                LogUtil.v(TAG, "---updatePhoneInfo-suc=${it?.data}")
            }.setErrorListener { errorType, errorCode, errorMsg ->
                LogUtil.v(TAG, "---updatePhoneInfo-fail=${errorMsg}")
            }
        }
    }

    // 更新掌御日活
    private fun updateActiveDate(deviceId: String) {
        val curTimeFormat = DeviceInfoHelper.timeFormat(System.currentTimeMillis())
        MainApi.updateActiveDate(deviceId, curTimeFormat).setSuccessListener {
            LogUtil.v(TAG, "---updateActiveDate-suc=${it?.data}")
        }.setErrorListener { errorType, errorCode, errorMsg ->
            LogUtil.v(TAG, "---updateActiveDate-fail=${errorMsg}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadVersionInfo() {
        if (updateManager == null) {
            updateManager = UpdateManager(this)
        }
        showLoading()
        updateManager?.requestVersionUpdate {
            dismissLoading()
            if (it) {
                dismissRemindDialog()
            }
        }
    }

}