package com.example.pgphone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pgphone.R
import com.example.pgphone.entity.MainDeviceInfoEntity
import com.example.pgphone.interfaces.OnItemClickListener
import com.example.pgphone.view.SettingItemView

/**
 * 首页设备信息列表适配器
 */
class MainDeviceInfoAdapter(private val dataList: MutableList<MainDeviceInfoEntity>) :
    RecyclerView.Adapter<MainDeviceInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_device_info, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateEntity = dataList[position]
        holder.settingItemView.setTitle(dateEntity.title)
        holder.settingItemView.setContent(dateEntity.content)
        holder.settingItemView.setShowDivider(position != dataList.size - 1)
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(dateEntity)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val settingItemView: SettingItemView = view.findViewById(R.id.settingItemView)

    }

    private var itemClickListener: OnItemClickListener<MainDeviceInfoEntity>? = null

    fun setOnClickListener(itemClickListener: OnItemClickListener<MainDeviceInfoEntity>) {
        this.itemClickListener = itemClickListener
    }

}