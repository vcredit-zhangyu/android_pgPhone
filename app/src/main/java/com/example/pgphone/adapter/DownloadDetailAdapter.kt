package com.example.pgphone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pgphone.R
import com.example.pgphone.entity.DownloadServerEntity
import com.example.pgphone.interfaces.OnItemDownloadDetailListener

/**
 * 下载站详情页列表适配器
 */
class DownloadDetailAdapter(private val dataList: MutableList<DownloadServerEntity>) : RecyclerView.Adapter<DownloadDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_download_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateEntity = dataList[position]
        holder.versionTextView.text = "${(dateEntity.display_name ?: "")}${getVersionCodeDisplay(dateEntity.version_code)}"
        holder.timeTextView.text = getCurStatus(dateEntity.status, dateEntity.write_date)
        if (position == dataList.size - 1) {
            holder.dividerView.visibility = View.INVISIBLE
        } else {
            holder.dividerView.visibility = View.VISIBLE
        }
        holder.generateButton.isEnabled = getGenerateStatus(dateEntity.status)
        val downloadStatus = getDownloadStatus(dateEntity.can_download, dateEntity.download_url, dateEntity.status)
        holder.downloadButton.isEnabled = downloadStatus
        holder.itemView.isEnabled = downloadStatus
        holder.generateButton.setOnClickListener {
            itemClickListener?.onGenerate(dateEntity)
        }
        holder.downloadButton.setOnClickListener {
            itemClickListener?.onDownload(dateEntity)
        }
        holder.itemView.setOnClickListener {
            itemClickListener?.onDownload(dateEntity)
        }
    }

    private fun getVersionCodeDisplay(versionCode: String?): String {
        return if (versionCode.isNullOrEmpty()) {
            ""
        } else {
            "  版本:${versionCode}"
        }
    }

    private fun getGenerateStatus(status: String?): Boolean {
        return !(status == "waiting" || status == "building")
    }

    private fun getDownloadStatus(canDownload: Boolean?, downloadUrl: String?, status: String?): Boolean {
        return canDownload == true && !downloadUrl.isNullOrEmpty() && status == "SUCCESS"
    }

    private fun getCurStatus(status: String?, time: String?): String {
        val resultStatus = when (status) {
            "SUCCESS" -> "编译成功"
            "waiting" -> "等待编译"
            "building" -> "编译中"
            "FAILURE" -> "编译失败"
            "ABORTED" -> "已取消"
            "Unknow" -> "未知"
            else -> ""
        }
        return if (resultStatus.isEmpty()) {
            ""
        } else {
            "$resultStatus  ${time ?: ""}"
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val versionTextView: TextView = view.findViewById(R.id.versionTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val generateButton: TextView = view.findViewById(R.id.generateButton)
        val downloadButton: TextView = view.findViewById(R.id.downloadButton)
        val dividerView: View = view.findViewById(R.id.dividerView)

    }

    private var itemClickListener: OnItemDownloadDetailListener<DownloadServerEntity>? = null

    fun setOnClickListener(itemClickListener: OnItemDownloadDetailListener<DownloadServerEntity>) {
        this.itemClickListener = itemClickListener
    }

}