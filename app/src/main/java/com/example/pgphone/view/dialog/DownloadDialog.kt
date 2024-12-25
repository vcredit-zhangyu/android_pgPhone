package com.example.pgphone.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import com.example.pgphone.R
import com.example.pgphone.utils.PhoneUtil
import kotlinx.android.synthetic.main.dialog_download.*
import java.text.DecimalFormat

/**
 * 下载弹窗
 */
class DownloadDialog(val curContext: Context) : Dialog(curContext, R.style.NormalDialog) {

    private var titleText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.dialog_download)
        initView()
    }

    private fun initView() {
        if (!titleText.isNullOrEmpty()) {
            titleTextView.text = titleText
        }
    }

    fun setTitleText(titleText: String?): DownloadDialog {
        this.titleText = titleText
        return this
    }

    fun setProgress(currentProgress: Long, maxProgress: Long) {
        updateProgressBar.progress = if (currentProgress.toInt() <= 0) 0 else currentProgress.toInt()
        updateProgressBar.max = maxProgress.toInt()

        (progressPercentTextView.layoutParams as LinearLayout.LayoutParams).setMargins(
            PhoneUtil.dp2Px(curContext, 19f) +
                    (PhoneUtil.dp2Px(curContext, 220f) * (currentProgress.toFloat() / maxProgress)).toInt(),
            (progressPercentTextView.layoutParams as LinearLayout.LayoutParams).topMargin, 0, 0
        )
        progressPercentTextView.text = "${DecimalFormat("#.#").format(currentProgress.toDouble() / maxProgress.toDouble() * 100)}%"
        progressTextView.text = "${
            DecimalFormat("#.##").format(currentProgress.toDouble() / (1024 * 1024)).toDouble()
        }M/${DecimalFormat("#.##").format(maxProgress.toDouble() / (1024 * 1024)).toDouble()}M"
    }

    override fun show() {
        super.show()
        val mParams = window?.attributes
        mParams?.alpha = 1f
        mParams?.gravity = Gravity.CENTER
        mParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        mParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        val leftRightPadding = PhoneUtil.dp2Px(curContext, 30f)
        window?.decorView?.setPadding(leftRightPadding, 0, leftRightPadding, 0)
        window?.attributes = mParams
    }

}