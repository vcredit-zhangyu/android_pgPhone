package com.example.pgphone.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import com.example.pgphone.R
import com.example.pgphone.interfaces.InputDialogInterface
import kotlinx.android.synthetic.main.dialog_feedback.*

/**
 * 意见反馈弹窗
 */
class FeedbackDialog(private val curContext: Context) : Dialog(curContext, R.style.BottomInDialog) {

    private var inputDialogInterface: InputDialogInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_feedback)
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
        cancelButton.setOnClickListener {
            dismiss()
            inputDialogInterface?.onClose()
        }
        commitButton.setOnClickListener {
            doCommit()
        }
        inputContentTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(edit: Editable?) {
                val length = edit?.length ?: 0
                inputNumberTextView.text = "${length}/100"
            }
        })
    }

    private fun doCommit() {
        val inputContent = inputContentTextView.text.toString()
        if (inputContent.isEmpty()) {
            Toast.makeText(curContext, curContext.getString(R.string.dialog_feedback_input_remind), Toast.LENGTH_LONG).show()
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
        inputContentTextView.setText(content)
        if (content.isNotEmpty()) {
            inputContentTextView.setSelection(content.length)
        }
    }

}
