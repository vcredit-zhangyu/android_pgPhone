package com.example.pgphone.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.pgphone.utils.StatusBarUtils

/**
 * 基底Activity
 */
open class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.immersive(this)
        StatusBarUtils.darkMode(this, true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        finish()
    }

}