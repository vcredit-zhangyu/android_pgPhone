package com.example.pgphone.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.StringRes
import com.example.pgphone.R
import kotlinx.android.synthetic.main.dialog_loading.*

class LoadingDialog : Dialog {

    private var mContext: Context? = null
    private var message: String? = null

    constructor(context: Context) : this(context, "")

    constructor(context: Context, message: String?)
            : super(context, R.style.WidgetViewLoadingDialog) {
        this.mContext = context
        this.message = message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.dialog_loading)
        if (!message.isNullOrEmpty()) {
            messageTextView.text = message
        }
    }

    override fun show() {
        if (mContext != null && !(mContext as Activity).isFinishing) {
            //show dialog
            super.show()
            val mParams = window?.attributes
            mParams?.alpha = 1f
            mParams?.gravity = Gravity.CENTER
            mParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
            mParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.decorView?.setPadding(0, 0, 0, 0)
            window?.attributes = mParams
        } else {
            dialog = null
        }
    }

    companion object {

        private var dialog: LoadingDialog? = null

        @Synchronized
        fun show(context: Context, @StringRes tipsRes: Int) {
            show(context, context.getString(tipsRes))
        }

        @Synchronized
        fun show(context: Context, message: String? = null) {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
                dialog = null
            }
            if (dialog == null) {
                dialog = LoadingDialog(context, message)
            }
            if (context is Activity) {
                if (context.isDestroyed || context.isFinishing) {
                    return
                }
            }
            dialog?.show()
        }

        @Synchronized
        fun dismiss() {
            dialog?.run {
                if (this.isShowing) {
                    this.dismiss()
                    dialog = null
                }
            }
        }

    }

}