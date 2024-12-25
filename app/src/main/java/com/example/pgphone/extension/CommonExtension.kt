package com.example.pgphone.extension

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.example.pgphone.R
import com.example.pgphone.view.dialog.LoadingDialog
import kotlinx.android.synthetic.main.layout_toast.view.*

fun Context.showToast(tipMsg: String?, @DrawableRes tipImage: Int? = null, showDuration: Int = Toast.LENGTH_SHORT) {
    if (tipMsg.isNullOrEmpty()) return
    val toastView = LayoutInflater.from(this).inflate(R.layout.layout_toast, null, false)
    toastView.toastContentTextView.text = tipMsg
    val toast = Toast(this)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.duration = showDuration
    toast.view = toastView
    toast.show()
}

fun Context.showToast(
    @StringRes tipMsgStrRes: Int,
    @DrawableRes tipImage: Int? = null,
    showDuration: Int = Toast.LENGTH_SHORT,
) {
    showToast(getString(tipMsgStrRes), tipImage, showDuration)
}

fun Fragment.showToast(tipMsg: String?, @DrawableRes tipImage: Int? = null, showDuration: Int = Toast.LENGTH_SHORT) {
    context?.showToast(tipMsg, tipImage, showDuration)
}

fun Fragment.showToast(
    @StringRes tipMsgStrRes: Int,
    @DrawableRes tipImage: Int? = null,
    showDuration: Int = Toast.LENGTH_SHORT,
) {
    context?.showToast(tipMsgStrRes, tipImage, showDuration)
}

fun View.showToast(tipMsg: String?, @DrawableRes tipImage: Int? = null, showDuration: Int = Toast.LENGTH_SHORT) {
    context?.showToast(tipMsg, tipImage, showDuration)
}

fun View.showToast(
    @StringRes tipMsgStrRes: Int,
    @DrawableRes tipImage: Int? = null,
    showDuration: Int = Toast.LENGTH_SHORT,
) {
    context?.showToast(tipMsgStrRes, tipImage, showDuration)
}

fun Context.showLoading(tipMsg: String? = null) {
    LoadingDialog.show(this, tipMsg)
}

fun Context.showLoading(@StringRes tipRes: Int) {
    showLoading(getString(tipRes))
}

fun Context.dismissLoading() {
    LoadingDialog.dismiss()
}

fun Fragment.showLoading(@StringRes tipRes: Int) {
    context?.showLoading(tipRes)
}

fun Fragment.showLoading(tipMsg: String? = null) {
    requireActivity().showLoading(tipMsg)
}

fun Fragment.dismissLoading() {
    requireActivity().dismissLoading()
}

fun View.showLoading(@StringRes tipRes: Int) {
    context?.showLoading(tipRes)
}

fun View.showLoading(tipMsg: String? = null) {
    context?.showLoading(tipMsg)
}

fun View.dismissLoading() {
    context?.dismissLoading()
}