package com.example.pgphone.manager

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.example.pgphone.interfaces.OnDownloadProgressListener
import com.example.pgphone.service.DownloadService
import java.io.File

/**
 * 下载管理类
 */
class DownloadManager(val context: Context) {

    private var isForceUpdate: Boolean = true
    private var saveApkPath: String? = null
    private var isExit = false
    private var downloadProgressListener: OnDownloadProgressListener? = null

    fun startDownLoadApk(downloadLink: String) {
        val saveApkDir = context.getExternalFilesDir("")?.absolutePath + File.separator + "DownLoad"
        val saveApkName = downloadLink.substring(downloadLink.lastIndexOf(File.separator) + 1)
        saveApkPath = saveApkDir + File.separator + saveApkName
        saveApkPath?.run {
            if (File(this).exists()) {
                File(this).delete()
            }
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(DownloadService.KEY_DOWNLOAD_URL, downloadLink)
            intent.putExtra(DownloadService.KEY_SAVE_FILE_PATH, this)
            intent.putExtra(DownloadService.KEY_DIR_TYPE, "DownLoad")
            context.bindService(intent, UpdateServiceConnection(), Context.BIND_AUTO_CREATE)
        }
    }

    inner class UpdateServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: DownloadService.DownloadBinder = service as DownloadService.DownloadBinder
            val downloadService = binder.service
            //与DownloadService通过回调接口来通信，更新progressDialog
            downloadService.setOnProgressListener(object : DownloadService.OnProgressListener {
                override fun onProgress(mDownload_current: Long, mDownload_all: Long) {
//                    updateDialog?.setProgress(mDownload_current, mDownload_all)
                    downloadProgressListener?.onProgress(mDownload_current, mDownload_all)
                }
            })
            //与DownloadService通过回调接口来通信，调起包管理器安装
            downloadService.setOnInstallListener(object : DownloadService.OnInstallListener {
                override fun onInstall(path: String?) {
                    path?.run {
//                        updateDialog?.dismiss()
                        downloadProgressListener?.onInstall(path)
                        context.unbindService(this@UpdateServiceConnection)
                        installApk(this)
                    }
                }
            })
        }
    }

    private fun installApk(apkPath: String) {
        if (apkPath.isNotEmpty()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                installByFilePathUnderAndroidN(apkPath)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                installByFileProviderUpperAndroidN(apkPath)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                    installByFileProviderUpperAndroidN(apkPath)
                } else {
                    isHasInstallPackagesPermission(apkPath)
                }
            }
        } else {
//            context.showToast("安装包损坏或出错，请重新下载安装")
        }
    }

    //7.0以下系统，直接使用文件路径Uri即可调起安装
    private fun installByFilePathUnderAndroidN(localPath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(
            Uri.parse("file://$localPath"),
            "application/vnd.android.package-archive"
        ) //apk的MIMEType)
        context.startActivity(intent)
        if (isExit) {
            exitApp()
        }
    }

    //7.0以上系统，需使用FileProvider生成Uri来调起安装
    private fun installByFileProviderUpperAndroidN(localPath: String) {
        val intent = Intent(Intent.ACTION_VIEW)

        val uri: Uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            File(localPath)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
        if (isExit) {
            exitApp()
        }
    }

    //8.0安装未知来源应用权限
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isHasInstallPackagesPermission(apkPath: String) {
        val haveInstallPermission: Boolean = context.packageManager.canRequestPackageInstalls()
        if (haveInstallPermission) {
            installByFileProviderUpperAndroidN(apkPath)
        } else {
            requestInstallPackagesPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestInstallPackagesPermission() {
        val packageURI = Uri.parse("package:" + context.packageName)
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        (context as Activity).startActivityForResult(intent, INSTALL_PERMISSION_CODE)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == INSTALL_PERMISSION_CODE && resultCode == Activity.RESULT_OK) {
            saveApkPath?.run {
                installByFileProviderUpperAndroidN(this)
            }
        } else {
            if (isExit) {
                exitApp()
            }
        }
    }

    companion object {
        const val INSTALL_PERMISSION_CODE = 90
    }

    fun exitApp() {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appTaskList = activityManager.appTasks
        for (appTask in appTaskList) {
            appTask.finishAndRemoveTask()
        }
    }

    fun setOnDownloadProgressListener(downloadProgressListener: OnDownloadProgressListener?) {
        this.downloadProgressListener = downloadProgressListener
    }


    fun isExit(isExit: Boolean) {
        this.isExit = isExit
    }

    fun isExit(): Boolean {
        return isExit
    }


}