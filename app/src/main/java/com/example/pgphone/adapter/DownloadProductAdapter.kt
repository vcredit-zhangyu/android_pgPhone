package com.example.pgphone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pgphone.R
import com.example.pgphone.entity.DownloadProductEntity
import com.example.pgphone.interfaces.OnItemClickListener

/**
 * 下载站产品页列表适配器
 */
class DownloadProductAdapter(private val dataList: MutableList<DownloadProductEntity>, val recyclerViewParams: ViewGroup.LayoutParams) :
    RecyclerView.Adapter<DownloadProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_download_product, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateEntity = dataList[position]
        holder.nameTextView.text = dateEntity.display_name
        when (dateEntity.name) {
            "pgp" -> {
                holder.coverImageView.setImageResource(R.mipmap.app_launcher)
            }

            else -> {
                holder.coverImageView.setImageResource(R.mipmap.ic_launcher)
            }
        }
        holder.coverImageView.layoutParams = recyclerViewParams
        holder.itemView.setOnClickListener {
            itemClickListener?.onClick(dateEntity)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val coverImageView: ImageView = view.findViewById(R.id.coverImageView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)

    }

    private var itemClickListener: OnItemClickListener<DownloadProductEntity>? = null

    fun setOnClickListener(itemClickListener: OnItemClickListener<DownloadProductEntity>) {
        this.itemClickListener = itemClickListener
    }

}