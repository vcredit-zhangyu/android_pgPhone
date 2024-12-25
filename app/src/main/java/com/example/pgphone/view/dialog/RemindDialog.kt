package com.example.pgphone.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.example.pgphone.R
import com.example.pgphone.interfaces.DialogConfirmInterface
import kotlinx.android.synthetic.main.dialog_remind.*

/**
 * 提醒弹窗
 */
class RemindDialog(private val curContext: Context) : Dialog(curContext, R.style.NormalDialog) {

    private var dialogConfirmInterface: DialogConfirmInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_remind)
        initView()
        initListener()
    }

    override fun show() {
        super.show()

        val mParams = window?.attributes
        mParams?.alpha = 1f
        mParams?.gravity = Gravity.CENTER
        mParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        mParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.decorView?.setPadding(0, 0, 0, 0)
        window?.attributes = mParams
    }

    private var titleText: String = ""
    private var contentText: String = ""
    private var leftButtonText: String = ""
    private var rightButtonText: String = ""

    private fun initView() {
        setCanceledOnTouchOutside(false)

        titleTextView.text = titleText
        contentTextView.text = contentText
        leftButton.text = leftButtonText
        rightButton.text = rightButtonText
    }

    private fun initListener() {
        leftButton.setOnClickListener {
            dismiss()
            dialogConfirmInterface?.onCancel()
        }
        rightButton.setOnClickListener {
            dialogConfirmInterface?.onConfirm()
            dismiss()
        }
    }

    fun setOnDialogConfirmInterface(dialogConfirmInterface: DialogConfirmInterface?) {
        this.dialogConfirmInterface = dialogConfirmInterface
    }

    fun setTitleText(content: String) {
        this.titleText = content
    }

    fun setContentText(content: String) {
        this.contentText = content
    }

    fun setLeftButtonText(content: String) {
        this.leftButtonText = content
    }

    fun setRightButtonText(content: String) {
        this.rightButtonText = content
    }

}