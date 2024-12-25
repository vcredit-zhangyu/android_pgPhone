package com.example.pgphone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pgphone.R
import com.example.pgphone.entity.DownloadBranchEntity
import com.example.pgphone.interfaces.OnItemClickListener

/**
 * 下载站分支页列表适配器
 */
class DownloadBranchAdapter(private val dataList: MutableList<DownloadBranchEntity>) : RecyclerView.Adapter<DownloadBranchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_download_branch, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateEntity = dataList[position]
        holder.nameTextView.text = dateEntity.display_name
        if (position == dataList.size - 1) {
            holder.dividerView.visibility = View.INVISIBLE
        } else {
            holder.dividerView.visibility = View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(dateEntity)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val dividerView: View = view.findViewById(R.id.dividerView)

    }

    private var itemClickListener: OnItemClickListener<DownloadBranchEntity>? = null

    fun setOnClickListener(itemClickListener: OnItemClickListener<DownloadBranchEntity>) {
        this.itemClickListener = itemClickListener
    }

}