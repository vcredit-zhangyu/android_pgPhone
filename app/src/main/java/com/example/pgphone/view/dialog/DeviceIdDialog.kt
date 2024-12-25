package com.example.pgphone.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import com.example.pgphone.R
import com.example.pgphone.interfaces.InputDialogInterface
import kotlinx.android.synthetic.main.dialog_device_id.*

/**
 * 设备号弹窗
 */
class DeviceIdDialog(private val curContext: Context) : Dialog(curContext, R.style.BottomInDialog) {

    private var inputDialogInterface: InputDialogInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_device_id)
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
    }

    private fun initListener() {
        closeImageView.setOnClickListener {
            dismiss()
            inputDialogInterface?.onClose()
        }
        commitButton.setOnClickListener {
            doCommit()
        }
    }

    private fun doCommit() {
        val inputContent = imeiContentTextView.text.toString()
        if (inputContent.isEmpty()) {
            Toast.makeText(curContext, curContext.getString(R.string.dialog_device_id_title), Toast.LENGTH_LONG).show()
            return
        }
        inputDialogInterface?.onInput(inputContent)
        dismiss()
    }

    fun setOnInputDialogInterface(inputDialogInterface: InputDialogInterface?) {
        this.inputDialogInterface = inputDialogInterface
    }

    fun setInputImei(content: String?) {
        if (content.isNullOrEmpty()) {
            return
        }
        imeiContentTextView.setText(content)
        if (content.isNotEmpty()) {
            imeiContentTextView.setSelection(content.length)
        }
    }

}