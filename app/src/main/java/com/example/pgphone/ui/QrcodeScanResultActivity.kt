package com.example.pgphone.ui

import android.os.Bundle
import android.widget.Toast
import com.example.pgphone.R
import com.example.pgphone.base.BaseActivity
import com.example.pgphone.constant.IntentConstant
import com.example.pgphone.utils.PhoneOperateUtils
import kotlinx.android.synthetic.main.activity_qrcode_scan_result.*

/**
 * 二维码扫描结果页
 */
class QrcodeScanResultActivity : BaseActivity() {

    private var scanResult: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_scan_result)
        initView()
        initListener()
    }

    private fun initView() {
        scanResult = intent.getStringExtra(IntentConstant.activityIntentContent)
        scanResultTextView.text = scanResult
    }

    private fun initListener() {
        copyButton.setOnClickListener {
            PhoneOperateUtils.copyClipboard(this@QrcodeScanResultActivity, scanResult)
            Toast.makeText(this@QrcodeScanResultActivity, "复制成功！", Toast.LENGTH_LONG).show()
        }
    }

}