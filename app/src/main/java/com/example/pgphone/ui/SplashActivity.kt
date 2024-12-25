package com.example.pgphone.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.pgphone.BuildConfig
import com.example.pgphone.MainActivity
import com.example.pgphone.R
import com.example.pgphone.api.ApiManager
import com.example.pgphone.base.BaseActivity
import com.example.pgphone.extension.dismissLoading
import com.example.pgphone.extension.showLoading
import com.example.pgphone.help.SharePreferenceHelper
import com.example.pgphone.manager.UpdateManager
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * 启动页
 */
class SplashActivity : BaseActivity() {

    private var updateManager: UpdateManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initView()
        initListener()
        initLoad()
    }

    private fun initView() {
        val deviceId = SharePreferenceHelper.getDeviceId()
        if (deviceId.isNullOrEmpty()) {
            appEnterLinearLayout.visibility = View.VISIBLE
        } else {
            appEnterLinearLayout.visibility = View.INVISIBLE
        }
    }

    private fun initListener() {
        testDeviceButton.setOnClickListener {
            // 测试机
            SharePreferenceHelper.setClientType(1)
            startMainActivity()
        }
        personDeviceButton.setOnClickListener {
            // 个人手机
            SharePreferenceHelper.setClientType(2)
            startDownloadSiteActivity()
        }
    }

    /**
     * 进入主界面
     */
    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
     * 进入下载站页
     */
    private fun startDownloadSiteActivity() {
        val intent = Intent(this, DownloadProductActivity::class.java)
        intent.putExtra("from", 2)
        startActivity(intent)
        finish()
    }

    private fun initLoad() {
        loadVersionInfo()
    }

    private fun loadVersionInfo() {
        if (updateManager == null) {
            updateManager = UpdateManager(this)
        }
        showLoading()
        updateManager?.requestVersionUpdate {
            dismissLoading()
            if (!it) {
                nextStep()
            }
        }
    }

    private fun nextStep() {
        val deviceId = SharePreferenceHelper.getDeviceId()
        if (deviceId.isNullOrEmpty()) {
            val clientType = SharePreferenceHelper.getClientType()
            if (clientType == 1) {
//                startMainActivity()
            } else if (clientType == 2) {
                startDownloadSiteActivity()
            }
        } else {
            startMainActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateManager?.onActivityResult(requestCode, resultCode, data)
    }

}