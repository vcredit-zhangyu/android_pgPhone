package com.example.pgphone.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pgphone.R
import com.example.pgphone.adapter.DownloadDetailAdapter
import com.example.pgphone.api.MainApi
import com.example.pgphone.api.UploadUseInfo
import com.example.pgphone.base.BaseActivity
import com.example.pgphone.entity.DownloadServerEntity
import com.example.pgphone.extension.dismissLoading
import com.example.pgphone.extension.showLoading
import com.example.pgphone.extension.showToast
import com.example.pgphone.interfaces.OnDownloadProgressListener
import com.example.pgphone.interfaces.OnItemDownloadDetailListener
import com.example.pgphone.manager.DownloadManager
import com.example.pgphone.utils.LogUtil
import kotlinx.android.synthetic.main.activity_download_branch.*

/**
 * 下载站详情页面
 */
class DownloadDetailActivity : BaseActivity() {

    private val TAG: String = javaClass.simpleName
    private var productAdapter: DownloadDetailAdapter? = null
    private var productList: MutableList<DownloadServerEntity>? = null
    private var clickProduct: String? = ""
    private var clickBranch: String? = ""
    private var branch: String? = ""
    private var app: String? = ""
    private val refreshStatusHandlerCode = 1 // 刷新状态code
    private val downTimeUnit = 1000L    // 刷新时间单位 秒
    private var refreshTimeInterval = 5 // 刷新时间间隔
    private var downloadManager: DownloadManager? = null
    private var curDeviceId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_branch)
        initView()
        initListener()
        initLoad()
    }

    private fun initView() {
        baseToolbar.setTitle(getString(R.string.download_detail_title))
        productRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        if (productList == null) {
            productList = ArrayList()
        }
        productList?.run {
            productAdapter = DownloadDetailAdapter(this)
        }
        productRecyclerView.adapter = productAdapter
        clickProduct = intent.getStringExtra("clickProduct")
        clickBranch = intent.getStringExtra("clickBranch")
        curDeviceId = intent.getStringExtra("deviceId")
    }

    private fun initListener() {
        productAdapter?.setOnClickListener(object : OnItemDownloadDetailListener<DownloadServerEntity> {

            override fun onGenerate(entity: DownloadServerEntity) {
                LogUtil.v(TAG, "生成-->${entity.display_name}")
                doGenerate(entity)
            }

            override fun onDownload(entity: DownloadServerEntity) {
                LogUtil.v(TAG, "下载-->${entity.download_url}")
                doDownLoad(entity)
            }
        })
    }

    /**
     * 生成apk包
     */
    private fun doGenerate(entity: DownloadServerEntity) {
        var addUrl =
            "?branch=" + this.branch + "&os=" + "android" + "&server=" + entity.name + "&app=" + app + "&android_version=" + entity.android_version + "&port=" + entity.port

        var buildType = ""
        when (entity.name) {
            "dev" -> {
                buildType = "ForTest"
            }

            "release" -> {
                buildType = "Release"
            }

            "prod" -> {
                buildType = "Release"
            }
        }

        addUrl = "${addUrl}&buildType=$buildType"

    }

    private fun initLoad() {
        loadProductData()
    }

    private fun loadProductData() {

    }

    private fun setOnResult() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun refreshStatus(status: String?) {
        val message = Message()
        message.what = refreshStatusHandlerCode
        message.obj = status
        handler.sendMessageDelayed(message, refreshTimeInterval * downTimeUnit)
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                refreshStatusHandlerCode -> {
                    loadProductData()
                }
            }
        }
    }

    private fun doDownLoad(entity: DownloadServerEntity) {
//        showToast("下载中，请稍后...")
        if (downloadManager == null) {
            downloadManager = DownloadManager(this)
        }
        downloadManager?.isExit(false)
        val downloadUrl = entity.download_url ?: ""
        downloadManager?.startDownLoadApk(downloadUrl)
        if (!curDeviceId.isNullOrEmpty()) {
            curDeviceId?.run {
                doCommitPhoneUseRecord(this)
            }
        }
        showLoading(R.string.toast_loading)
        downloadManager?.setOnDownloadProgressListener(object : OnDownloadProgressListener {
            override fun onProgress(currentProgress: Long, maxProgress: Long) {
                if (currentProgress == maxProgress) {
                    dismissLoading()
                }
            }

            override fun onInstall(path: String?) {
                dismissLoading()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        downloadManager?.onActivityResult(requestCode, resultCode, data)
    }

    // 提交设备使用记录
    private fun doCommitPhoneUseRecord(deviceId: String) {
        val uploadUseList = ArrayList<UploadUseInfo>()
        uploadUseList.add(UploadUseInfo(deviceId, downloadAppCode(app), System.currentTimeMillis().toString()))
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

    // 生成app对应的code码
    private fun downloadAppCode(name: String?): String {
        if (name.isNullOrEmpty()) {
            return ""
        }
        return when(name) {
            "kkd" -> "ccl"
            "sd" -> "shandai"
            else -> name
        }
    }

}