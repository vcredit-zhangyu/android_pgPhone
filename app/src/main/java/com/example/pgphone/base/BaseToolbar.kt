package com.example.pgphone.base

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.pgphone.R
import com.example.pgphone.utils.StatusBarUtils

/**
 * 基础标题栏
 */
class BaseToolbar : FrameLayout {

    val NORMAL = 0
    val SHOWSERVICE = 1
    val WITHRIGHTTEXT = 2

    private var mActivity: BaseActivity? = null
    private var mComm_titlebar: LinearLayout? = null
    private var mTitlebar_icon_left: ImageView? = null
    private var mTitlebar_txt_title: TextView? = null
    private var mTitlebar_left_txt: TextView? = null
    private var mLeftIcon = 0
    private var mLeftText: String? = null
    private var mLeftTextSize = 0f
    private var mLeftTextColor = 0
    private var mMiddleTitleText: String? = null
    private var mMiddleTitleSize = 0f
    private var mMiddleTitleColor = 0
    private var mRightText: String? = null
    private var mRightTextSize = 0f
    private var mRightTextColor = 0
    private var mTitleBackgroundColor = 0

    private var lastClick: Long = 0

    private var onServiceClickListener: OnServiceClickListener? = null
    private var routeName: String? = null

    fun getRouteName(): String? {
        return if (TextUtils.isEmpty(routeName)) getTitle() else routeName
    }

    fun setRouteName(routeName: String?): BaseToolbar {
        this.routeName = routeName
        return this
    }

    interface OnServiceClickListener {
        fun onServiceClick()
    }

    fun setOnServiceClickListener(onServiceClickListener: OnServiceClickListener?) {
        this.onServiceClickListener = onServiceClickListener
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?): BaseToolbar {
        LayoutInflater.from(context).inflate(getLayout(), this)
        if (context is BaseActivity) {
            mActivity = context as BaseActivity
        }
        initTitleBarView(this)
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.BaseToolbar)
            mLeftIcon = ta.getResourceId(R.styleable.BaseToolbar_leftIcon, R.mipmap.toolbar_left_back)
            mLeftText = ta.getString(R.styleable.BaseToolbar_leftText)
            mLeftTextSize = ta.getDimension(R.styleable.BaseToolbar_leftTextSize, 14f)
            mLeftTextColor = ta.getColor(R.styleable.BaseToolbar_leftTextColor, context.resources.getColor(R.color.white))
            mMiddleTitleText = ta.getString(R.styleable.BaseToolbar_middleTitleText)
            mMiddleTitleSize = ta.getDimension(R.styleable.BaseToolbar_middleTitleSize, 17f)
            mMiddleTitleColor = ta.getColor(R.styleable.BaseToolbar_middleTitleColor, context.resources.getColor(R.color.white))
            mRightText = ta.getString(R.styleable.BaseToolbar_rightText)
            mRightTextSize = ta.getDimension(R.styleable.BaseToolbar_rightTextSize, 14f)
            mRightTextColor = ta.getColor(R.styleable.BaseToolbar_rightTextColor, context.resources.getColor(R.color.white))
            mTitleBackgroundColor = ta.getColor(R.styleable.BaseToolbar_titleBackgroundColor, context.resources.getColor(R.color.common_activity_bg))
            val titleType = ta.getInt(R.styleable.BaseToolbar_titleType, 0)

            if (mLeftText.isNullOrEmpty()) {
                mLeftText = "返回"
            }
//            setLeftTextColor(mLeftTextColor)
            setLeftText(mLeftText)
            setLeftIcon(mLeftIcon)
            setTitle(mMiddleTitleText)
//            setTitleColor(mMiddleTitleColor)
            when (titleType) {
                NORMAL -> setTitleBarTypeNormal()
                SHOWSERVICE -> setTitleBarTypeService()
                WITHRIGHTTEXT -> setTitleBarTypeWithRightText()
                else -> {}
            }
            ta.recycle()
        }
        return this
    }

    @LayoutRes
    private fun getLayout(): Int {
        return R.layout.layout_base_toolbar
    }

    private fun initTitleBarView(baseTitleBar: BaseToolbar) {
        mComm_titlebar = baseTitleBar.findViewById<LinearLayout>(R.id.toolbarConstraintLayout)
        mTitlebar_txt_title = baseTitleBar.findViewById<TextView>(R.id.titleTextView)
        mTitlebar_icon_left = baseTitleBar.findViewById(R.id.leftImageView)
        mTitlebar_left_txt = baseTitleBar.findViewById<TextView>(R.id.leftTextView)
        mComm_titlebar?.run {
            StatusBarUtils.setPadding(context, this)
        }
    }

    fun setTitleVisible(visible: Int): BaseToolbar {
        visibility = visible
        return this
    }

    fun setStatusBarVisible(visible: Int): BaseToolbar {
        return this
    }

    fun setLeftIcon(resId: Int): BaseToolbar {
        if (resId > 0) {
            mTitlebar_icon_left?.setImageResource(resId)
        }
        mTitlebar_icon_left?.visibility = if (resId > 0) VISIBLE else GONE
        return this
    }

    fun setLeftIconListener(listener: OnClickListener?): BaseToolbar {
        if (listener != null) {
            mTitlebar_icon_left?.setOnClickListener(listener)
        }
        return this
    }

    fun setLeftText(text: String?): BaseToolbar {
        if (!TextUtils.isEmpty(text)) {
            mTitlebar_left_txt?.text = text
        }
        mTitlebar_left_txt?.visibility = if (!TextUtils.isEmpty(text)) VISIBLE else GONE
        return this
    }

    fun setLeftTextColor(textColor: Int): BaseToolbar {
        mTitlebar_left_txt?.setTextColor(textColor)
        return this
    }

    fun setLeftIconVisibility(isShow: Boolean): BaseToolbar {
        mTitlebar_icon_left?.visibility = if (isShow) VISIBLE else GONE
        return this
    }

    fun setTitleColor(textColor: Int): BaseToolbar {
        //        if (textColor > 0) {
        mTitlebar_txt_title?.setTextColor(textColor)
        //        }
        return this
    }

    fun setTitle(text: String?): BaseToolbar {
        if (!TextUtils.isEmpty(text)) {
            mTitlebar_txt_title?.text = text
        }
        mTitlebar_txt_title?.visibility = if (!TextUtils.isEmpty(text)) VISIBLE else GONE
        return this
    }

    fun getTitle(): String? {
        return mTitlebar_txt_title?.text.toString().trim { it <= ' ' }
    }

    fun setLeftTextListener(listener: OnClickListener?): BaseToolbar {
        if (listener != null) {
            mTitlebar_left_txt?.setOnClickListener(listener)
        }
        return this
    }

    fun setTitleBarTypeNormal(): BaseToolbar {
        val isShow = true
        setLeftIconVisibility(isShow)
        setLeftTextVisibility(isShow)
        setLeftIconListener {
            if (mActivity != null) {
//                mActivity.onToolbarLeftBackPress()
                mActivity?.onBackPressed();
            }
        }
        setLeftTextListener {
            if (mActivity != null) {
//                mActivity.onToolbarLeftBackPress()
                mActivity?.onBackPressed();
            }
        }
        return this
    }

    fun setTitleBarTypeService(): BaseToolbar {
        val isShow = true
        setLeftIconVisibility(!isShow)
        setLeftTextVisibility(!isShow)
        return this
    }

    fun setTitleBarTypeWithRightText(): BaseToolbar {
        val isShow = true
        setLeftIconVisibility(isShow)
        setLeftTextVisibility(!isShow)
        setLeftIconListener(OnClickListener {
            if (isFastClick()) {
                return@OnClickListener
            }
            if (mActivity != null) {
//                mActivity?.onToolbarLeftBackPress()
                mActivity?.onBackPressed();
            }
        })
        return this
    }

    fun setTitleBarTypeWithRightCustomImage(imageUrl: String?): BaseToolbar {
        val isShow = true
        setLeftIconVisibility(isShow)
        setLeftTextVisibility(!isShow)
        setLeftIconListener(OnClickListener {
            if (isFastClick()) {
                return@OnClickListener
            }
            if (mActivity != null) {
//                mActivity.onToolbarLeftBackPress()
                mActivity?.onBackPressed();
            }
        })
        return this
    }


    fun setLeftTextVisibility(isShow: Boolean): BaseToolbar {
        mTitlebar_left_txt?.visibility = if (isShow) VISIBLE else GONE
        return this
    }

    fun setTitleBackgroundColor(color: Int): BaseToolbar {
        mComm_titlebar?.setBackgroundColor(color)
        mTitleBackgroundColor = color
        return this
    }

    fun setLeftIconColor(resId: Int): BaseToolbar {
        mActivity?.run {
            val up = ContextCompat.getDrawable(this, R.color.common_333)
            val drawableUp = DrawableCompat.wrap(up!!)
            DrawableCompat.setTint(drawableUp, resId)
            mTitlebar_icon_left?.setBackgroundResource(0)
            mTitlebar_icon_left?.setImageDrawable(drawableUp)
        }
        return this
    }

    /**
     * 判断是否快速点击
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isFastClick(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastClick >= 200) {
            lastClick = now
            return false
        }
        lastClick = now
        return true
    }

}