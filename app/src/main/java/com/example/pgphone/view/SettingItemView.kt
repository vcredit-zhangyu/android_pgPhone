package com.example.pgphone.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.pgphone.R
import kotlinx.android.synthetic.main.layout_setting_item_view.view.*

/**
 * 自定义设置条目View
 */
class SettingItemView : LinearLayout {

    /**
     * 是否展示分隔线
     */
    private var showDivider = true

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.layout_setting_item_view, this@SettingItemView, true)
        attrs?.run {
            val viewTypeArray = context.obtainStyledAttributes(this, R.styleable.SettingItemView)
            showDivider = viewTypeArray.getBoolean(R.styleable.SettingItemView_showDivider, true)
            val itemTitle = viewTypeArray.getString(R.styleable.SettingItemView_itemTitle)
            val itemContent = viewTypeArray.getString(R.styleable.SettingItemView_itemContent)
            if (!itemTitle.isNullOrEmpty()) {
                setTitle(itemTitle)
            }
            if (!itemContent.isNullOrEmpty()) {
                setContent(itemContent)
            }
            setShowDivider(showDivider)

            viewTypeArray.recycle()
        }
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setContent(content: String) {
        contentTextView.text = content
    }

    fun setShowDivider(showDivider: Boolean) {
        this.showDivider = showDivider
        if (showDivider) {
            dividerView.visibility = VISIBLE
        } else {
            dividerView.visibility = INVISIBLE
        }
    }

}