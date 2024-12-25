package com.example.pgphone.service

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.webkit.MimeTypeMap
import java.util.Locale

class DownloadService : Service() {

    private val binder = DownloadBinder()
    private var onProgressListener: OnProgressListener? = null
    private var onInstallListener: OnInstallListener? = null
    private var onCancelDownloadListener: OnCancelDownloadListener? = null
    private var downloadManagerCanUse = true
    private lateinit var downloadManager: DownloadManager
    private var downloadId: Long = 0
    private var completedReceiver: CompletedReceiver? = null
    private var notificationClickedReceiver: NotificationClickedReceiver? = null
    private var downloadObserver: DownloadObserver? = null
    override fun onCreate() {
        super.onCreate()
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManagerCanUse = canDownloadState(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    /** 下载  */
    @SuppressLint("NewApi")
    private fun download(url: String, dirType: String, fileNameTmp: String) {
        if (downloadManagerCanUse) {
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setAllowedOverRoaming(true)
            // 根据文件后缀设置mime
            val mimeTypeMap = MimeTypeMap.getSingleton()
            val startIndex = fileNameTmp.lastIndexOf(".")
            val tmpMimeString = fileNameTmp.substring(startIndex + 1).lowercase(Locale.getDefault())
            val mimeString = mimeTypeMap.getMimeTypeFromExtension(tmpMimeString)
            request.setMimeType(mimeString)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle(fileNameTmp)
            request.addRequestHeader(
                "Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8"
            )
            request.setDescription("掌御手机下载apk")
            request.setDestinationInExternalFilesDir(
                this,
                dirType,
                fileNameTmp
            )
            // 内存和外存都存储一下downloadId
            downloadId = downloadManager.enqueue(request)
            registerReceiversAndObservers()
        } else {
            // 系统DownloadManager被阉割，只能跳浏览器让他下载了
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            intent.data = Uri.parse(url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun registerReceiversAndObservers() {
        completedReceiver = CompletedReceiver()
        notificationClickedReceiver = NotificationClickedReceiver()

        //注册广播接收器（下载完成、通知栏被点击）
        registerReceiver(completedReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        registerReceiver(
            notificationClickedReceiver,
            IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        )
        downloadObserver = DownloadObserver(Handler(Looper.getMainLooper()), this)
        //注册内容观察者，观测下载进度并回调进度条增加
        downloadObserver?.run {
            contentResolver.registerContentObserver(
                Uri.parse("content://downloads/"),
                true,
                this
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != completedReceiver) unregisterReceiver(completedReceiver)
        if (null != notificationClickedReceiver) unregisterReceiver(notificationClickedReceiver)
        downloadObserver?.run {
            contentResolver.unregisterContentObserver(this)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        val downloadUrl = intent.getStringExtra(KEY_DOWNLOAD_URL)
        val fileAbsolutePath = intent.getStringExtra(KEY_SAVE_FILE_PATH)
        val dirType = intent.getStringExtra(KEY_DIR_TYPE)
        val fileName = fileAbsolutePath?.substring(fileAbsolutePath.lastIndexOf("/") + 1)
        if (!downloadUrl.isNullOrEmpty() && !fileName.isNullOrEmpty()) {
            download(downloadUrl, dirType ?: "", fileName)
        }
        return binder
    }

    interface OnProgressListener {
        fun onProgress(mDownload_current: Long, mDownload_all: Long)
    }

    interface OnInstallListener {
        fun onInstall(path: String?)
    }

    interface OnCancelDownloadListener {
        fun onCancel()
    }

    fun setOnProgressListener(onProgressListener: OnProgressListener?) {
        this.onProgressListener = onProgressListener
    }

    fun setOnInstallListener(onInstallListener: OnInstallListener?) {
        this.onInstallListener = onInstallListener
    }

    fun setOnCancelDownloadListener(onCancelDownloadListener: OnCancelDownloadListener?) {
        this.onCancelDownloadListener = onCancelDownloadListener
    }

    //取消下载
    fun cancelDownload(onCancelDownloadListener: OnCancelDownloadListener?) {
        onCancelDownloadListener?.onCancel()
        if (downloadId > 0) {
            downloadManager?.remove(downloadId)
            stopSelf()
        }
    }

    /** 跳转到系统下载界面  */
    private fun showDownloadManagerView() {
        val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private inner class CompletedReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent,
        ) {
            val c = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
            if (c.moveToFirst()) {
                val columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (c.getInt(columnIndex) == DownloadManager.STATUS_SUCCESSFUL) {
                    val localFilePath = Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).path
                    onInstallListener?.onInstall(localFilePath)
                }
            }
            c.close()
        }
    }

    private inner class NotificationClickedReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent,
        ) {
            showDownloadManagerView()
        }
    }

    private inner class DownloadObserver(
        handler: Handler?,
        private val context: Context,
    ) : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            //实例化查询类，downloadId
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
            while (cursor.moveToNext()) {
                val mDownload_so_far =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val mDownload_all =
                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                onProgressListener?.onProgress(mDownload_so_far, mDownload_all)
            }
            cursor.close()
        }

    }

    inner class DownloadBinder : Binder() {
        /**
         * 返回当前服务的实例
         *
         * @return
         */
        val service: DownloadService
            get() = this@DownloadService
    }

    companion object {
        const val KEY_DOWNLOAD_URL = "key_download_url"
        const val KEY_SAVE_FILE_PATH = "key_save_file_path"
        const val KEY_DIR_TYPE = "key_dir_type"

        /** DownloadManager 是否可用  */
        private fun canDownloadState(ctx: Context): Boolean {
            try {
                val state = ctx.packageManager
                    .getApplicationEnabledSetting("com.android.providers.downloads")
                if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
                ) {
                    return false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return true
        }

        /**
         * 是否正在下载
         *
         * @param url
         * @return
         */
        fun isDownloading(context: Context, url: String): Boolean {
            val c =
                (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
                    .query(
                        DownloadManager.Query().setFilterByStatus(DownloadManager.STATUS_RUNNING)
                    )
            if (c != null) {
                if (c.moveToFirst()) {
                    val tmpURI =
                        c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI))
                    if (tmpURI == url) {
                        if (!c.isClosed) {
                            c.close()
                        }
                        return true
                    }
                }
                if (!c.isClosed) {
                    c.close()
                }
            }
            return false
        }
    }
}