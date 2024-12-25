package com.example.pgphone.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.pgphone.base.globalApplication

open class SharePreferenceUtils (preferenceName: String = GLOBAL_SHARE_PREFERENCE_NAME) {
    private val sharePreference: SharedPreferences = globalApplication.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

    companion object {

        /**
         * 全局统一存储文件名称
         */
        private const val GLOBAL_SHARE_PREFERENCE_NAME = "pgphone_share_preference"

    }

    /**
     * 存储
     */
    fun putInt(key: String, value: Int) {
        sharePreference.edit().putInt(key, value).apply()
    }

    fun putString(key: String, value: String) {
        sharePreference.edit().putString(key, value).apply()
    }

    fun putFloat(key: String, value: Float) {
        sharePreference.edit().putFloat(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        sharePreference.edit().putLong(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        sharePreference.edit().putBoolean(key, value).apply()
    }

    /**
     * 取出
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharePreference.getInt(key,defaultValue)
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharePreference.getString(key,defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharePreference.getFloat(key,defaultValue)
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharePreference.getLong(key,defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharePreference.getBoolean(key,defaultValue)
    }

    /**
     * 查询键值是否存在
     */
    fun contains(key: String): Boolean {
        return sharePreference.contains(key)
    }

    /**
     * 移除指定存储的键值
     */
    fun removeKey(key: String) {
        sharePreference.edit().remove(key).apply()
    }

    /**
     * 清除所有存储的值
     */
    fun clear() {
        sharePreference.edit().clear().apply()
    }

}