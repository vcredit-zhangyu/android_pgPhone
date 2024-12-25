package com.example.pgphone.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.example.pgphone.R
import kotlinx.android.synthetic.main.dialog_use_expire.*

/**
 * 使用过期提醒弹窗
 */
class UseExpireDialog(private val curContext: Context) : Dialog(curContext, R.style.BottomInDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_use_expire)
        initView()
        initListener()
    }

    override fun show() {
        super.show()

        val mParams = window?.attributes
        mParams?.alpha = 1f
        mParams?.gravity = Gravity.BOTTOM
        mParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        mParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.decorView?.setPadding(0, 0, 0, 0)
        window?.attributes = mParams
    }

    private fun initView() {
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    private fun initListener() {
    }

}