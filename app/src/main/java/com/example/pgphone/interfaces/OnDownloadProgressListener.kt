package com.example.pgphone.interfaces

interface OnDownloadProgressListener {

    fun onProgress(mDownload_current: Long, mDownload_all: Long)

    fun onInstall(path: String?)

}