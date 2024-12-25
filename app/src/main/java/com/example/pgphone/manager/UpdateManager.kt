package com.example.pgphone.manager

import android.content.Context
import android.content.Intent
import com.example.pgphone.api.MainApi
import com.example.pgphone.help.DeviceInfoHelper
import com.example.pgphone.interfaces.OnDownloadProgressListener
import com.example.pgphone.utils.LogUtil
import com.example.pgphone.utils.NumberFormat
import com.example.pgphone.view.dialog.DownloadDialog

class UpdateManager(private var context: Context) {

    private val TAG = javaClass.simpleName
    private var downloadManager: DownloadManager? = null

    private var isForceUpdate: Boolean = true
    private var saveApkPath: String? = null

    //    private var updateDialog: UpdateDialog? = null
    private var updateDialog: DownloadDialog? = null

    init {
        downloadManager = DownloadManager(context)
        downloadManager?.isExit(true)
    }

    fun requestVersionUpdate(successListener: (Boolean) -> Unit) {
        MainApi.checkVersionUpdate().setSuccessListener {
            if (it?.data != null) {
                it?.data?.run {
                    val versionCodeLong = NumberFormat().stringConvertLong(appVersion, -1)
                    val curVersionCode = DeviceInfoHelper.getAppVersionCode()
                    val isUpdate =
                        versionCodeLong > 0 && curVersionCode > 0 && curVersionCode < versionCodeLong
                    LogUtil.v(TAG, "--->isUpdate=${isUpdate}-versionCodeLong=${versionCodeLong}")
                    if (isUpdate && !downloadUrl.isNullOrEmpty()) {
                        successListener.invoke(true)
                        startDownLoadApk(downloadUrl)
                    } else {
                        successListener.invoke(false)
                    }
                }
            } else {
                successListener.invoke(false)
            }
        }.setErrorListener { errorType, errorCode, errorMsg ->
            successListener.invoke(false)
        }
    }

    fun startDownLoadApk(url: String) {
        showUpdateDialog()
        downloadManager?.startDownLoadApk(url)
        downloadManager?.setOnDownloadProgressListener(object : OnDownloadProgressListener {
            override fun onProgress(mDownload_current: Long, mDownload_all: Long) {
                if (updateDialog?.isShowing == true) {
                    updateDialog?.setProgress(mDownload_current, mDownload_all)
                }
            }

            override fun onInstall(path: String?) {
                updateDialog?.dismiss()
            }
        })
    }

    fun showUpdateDialog() {
        updateDialog = DownloadDialog(context)
        updateDialog?.show()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        downloadManager?.onActivityResult(requestCode, resultCode, intent)
    }

}