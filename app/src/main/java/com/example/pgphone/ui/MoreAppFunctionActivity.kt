package com.example.pgphone.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.example.pgphone.R
import com.example.pgphone.base.BaseActivity
import com.example.pgphone.constant.DataCenter
import com.example.pgphone.constant.IntentConstant
import com.example.pgphone.utils.PhoneOperateUtils
import com.example.pgphone.utils.StatusBarUtils
import kotlinx.android.synthetic.main.activity_more_app_function.*

/**
 * 多个app扫描前选择功能
 */
class MoreAppFunctionActivity : BaseActivity() {

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_app_function)
        initView()
        initListener()
    }

    private fun initView() {
        StatusBarUtils.setPadding(this, rootView)
//        function1Button.layoutParams.height = StatusBarUtils.getStatusBarHeight(this)
//        function1Button.top += StatusBarUtils.getStatusBarHeight(this)
    }

    private fun initListener() {
        function1Button.setOnClickListener {
            // 扫码下载
            launchQrCode(0)
        }
        function2Button.setOnClickListener {
            // 豆豆钱
            launchQrCode(1)
        }
        function3Button.setOnClickListener {
            // 卡卡贷
            launchQrCode(2)
        }
    }

    // Register the launcher and result handler QrcodeScanResultActivity
    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult<ScanOptions, ScanIntentResult>(ScanContract(), ActivityResultCallback<ScanIntentResult> { result ->
            if (result.contents == null) {
                Toast.makeText(this@MoreAppFunctionActivity, "取消扫描", Toast.LENGTH_LONG).show()
            } else {
                val resultInfo = result.contents
                val originalIntent = result.originalIntent
                var intentType: Int = -1
                originalIntent?.run {
                    intentType = this.getIntExtra(IntentConstant.activityIntentType, -1)
                }
                Log.v(TAG, "---Scanned=${resultInfo}---intentType=${intentType}")
//                Toast.makeText(this@MainActivity, "Scanned: $resultInfo", Toast.LENGTH_LONG).show()
                if (resultInfo.endsWith(".apk")) {
                    downLoadFile(resultInfo)
                } else {
                    when(intentType) {
                        else -> {
                            startScanResult(resultInfo)
                        }
                    }
                }
            }
        })

    // 开始扫描二维码
    private fun launchQrCode(type: Int) {
        val scanOptions = ScanOptions()
        scanOptions.addExtra(IntentConstant.activityIntentType, type)
//        val createScanIntent = scanOptions.createScanIntent(this)
//        createScanIntent.putExtra(IntentConstant.activityIntentType, type)
        barcodeLauncher.launch(scanOptions)
    }

    /**
     * 下载文件
     */
    private fun downLoadFile(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    /**
     * 打开浏览结果页
     */
    private fun startScanResult(content: String) {
        val intent = Intent(this, QrcodeScanResultActivity::class.java)
        intent.putExtra(IntentConstant.activityIntentContent, content)
        startActivity(intent)
    }

}