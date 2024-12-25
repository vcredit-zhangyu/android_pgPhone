package com.example.pgphone.ui

import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.example.pgphone.R
import com.example.pgphone.base.BaseActivity
import com.example.pgphone.constant.IntentConstant
import kotlinx.android.synthetic.main.activity_qrcode_generate.*


/**
 * 二维码生成页
 */
class QrcodeGenerateActivity : BaseActivity() {

    private var qrcodeContent: String? = null   // 需要生成的二维码内容

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_generate)
        initView()
        initListener()
    }

    private fun initView() {
        qrcodeContent = intent.getStringExtra(IntentConstant.activityIntentContent)

        if (qrcodeContent.isNullOrEmpty()) {
            tipsTextView.text = getString(R.string.qrcode_generate_error)
        } else {
            try {
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(qrcodeContent, BarcodeFormat.QR_CODE, 200, 200)
                generateImageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                tipsTextView.text = getString(R.string.qrcode_generate_error)
            }
        }
    }

    private fun initListener() {
        tipsTextView.setOnClickListener {
            finish()
        }
    }

}