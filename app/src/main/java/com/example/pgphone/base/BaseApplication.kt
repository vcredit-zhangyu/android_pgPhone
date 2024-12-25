package com.example.pgphone.base

import android.app.Application
import com.example.pgphone.BuildConfig
import com.example.pgphone.api.ApiManager
import com.example.pgphone.help.DeviceIdHelper
import com.example.pgphone.utils.TitleBarDisplayUtils

lateinit var globalApplication: BaseApplication

class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        globalApplication = this
        DeviceIdHelper.initOaid(this)
        TitleBarDisplayUtils.init(this)
        initEnvironment()
    }

    private fun initEnvironment() {
        ApiManager.baseEnvironment = if (BuildConfig.DEBUG) {
            ApiManager.BaseApiEnvironment.BASE_URL_TEST
        } else {
            ApiManager.BaseApiEnvironment.BASE_URL_PRODUCE
        }
    }

}