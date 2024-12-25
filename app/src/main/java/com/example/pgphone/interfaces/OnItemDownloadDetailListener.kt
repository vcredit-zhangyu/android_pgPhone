package com.example.pgphone.interfaces

/**
 * 下载站下载详情页面生成/下载 点击接口
 */
interface OnItemDownloadDetailListener<T> {

    fun onGenerate(entity: T)

    fun onDownload(entity: T)

}