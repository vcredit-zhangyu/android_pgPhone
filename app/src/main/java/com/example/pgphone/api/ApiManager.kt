package com.example.pgphone.api

import com.example.network.base.BaseApiManager
import com.example.network.config.AddTokenConfig
import com.example.network.config.DecryptConfig
import com.example.network.config.EncryptConfig
import com.example.network.config.SignConfig
import com.example.network.config.TimeOutConfig
import com.example.network.model.MediaTypeEnum
import okhttp3.Interceptor
import okhttp3.MediaType

object ApiManager : BaseApiManager() {

    var baseEnvironment: BaseApiEnvironment = BaseApiEnvironment.BASE_URL_PRODUCE
    val isProduceEnvironment: Boolean
        get() {
            return baseEnvironment == BaseApiEnvironment.BASE_URL_PRODUCE
        }

    override fun getRequestMediaType(): MediaType? {
        return MediaTypeEnum.TYPE_JSON
    }

    override fun getDecryptConfig(): DecryptConfig? {
        return null
    }

    override fun getTimeOutConfig(): TimeOutConfig {
        return TimeOutConfig()
    }

    override fun getEncryptConfig(): EncryptConfig {
        return EncryptConfig("newEncFlag")
    }

    override fun getNeedEncrypt(): Boolean {
        return false // 全部接口设置加密，可通过setNeedEncrypt(false)方法单独修改某一接口
    }

    override fun getAddTokenConfig(): AddTokenConfig? {
        return null
    }

    override fun getInterceptorList(): List<Interceptor>? {
        return null
    }

    override fun getSignConfig(): SignConfig? {
        return null
    }

    override fun getBaseUrlFuc(): (() -> String) {
        return { baseEnvironment.baseUrl }
    }

    override fun getCommonRequestHeadersFunc(): (() -> Map<String?, String?>) {
        return {
            mapOf<String?, String?>()
        }
    }

    override fun getHandleBusinessErrorCodeFunc(): ((errorCode: Int?) -> Boolean) {
        return {
            true
        }
    }

    class BaseApiEnvironment(val buildEnvironment: String, val baseUrl: String) {
        companion object {
            val BASE_URL_TEST = BaseApiEnvironment("sit", getTestEnvironment())  // 测试环境
            val BASE_URL_PRODUCE = BaseApiEnvironment("produce", getProduceEnvironment())   // 生产环境

            private fun getFat(): String {
                return ""
            }

            private fun getTestEnvironment(): String {
                return "http://pgphone-test.com/"
            }

            private fun getProduceEnvironment(): String {
                return "http://pgphone-produce.com/"
            }
        }

    }

}