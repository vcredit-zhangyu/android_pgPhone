package com.example.pgphone.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pgphone.R
import com.example.pgphone.adapter.DownloadProductAdapter
import com.example.pgphone.api.MainApi
import com.example.pgphone.base.BaseActivity
import com.example.pgphone.constant.IntentConstant
import com.example.pgphone.entity.DownloadProductEntity
import com.example.pgphone.extension.dismissLoading
import com.example.pgphone.extension.showLoading
import com.example.pgphone.extension.showToast
import com.example.pgphone.interfaces.OnItemClickListener
import com.example.pgphone.utils.LogUtil
import com.example.pgphone.utils.PhoneUtil
import kotlinx.android.synthetic.main.activity_download_product.*

/**
 * 下载站选择产品页
 */
class DownloadProductActivity : BaseActivity() {

    private val TAG: String = javaClass.simpleName
    private var pageFrom: Int? = null
    private var productAdapter: DownloadProductAdapter? = null
    private var productList: MutableList<DownloadProductEntity>? = null
    private var curDeviceId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_product)
        initView()
        initListener()
        initLoad()
    }

    private fun initView() {
        baseToolbar.setTitle("")
        pageFrom = intent.getIntExtra("from", -1)
        curDeviceId = intent.getStringExtra("deviceId")
        if (pageFrom == 1) {
            baseToolbar.visibility = View.VISIBLE
        } else {
            baseToolbar.visibility = View.INVISIBLE
        }
        productRecyclerView.layoutManager = GridLayoutManager(this, 3)
        if (productList == null) {
            productList = ArrayList()
        }
        val recyclerViewParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val realWidth = ((PhoneUtil.getPhoneWidth(this) - PhoneUtil.dp2Px(this, (16f * 2 + 22f * 2))) / 3) - PhoneUtil.dp2Px(this, (10f * 2))
        recyclerViewParams.width = realWidth
        recyclerViewParams.height = realWidth
        productList?.run {
            productAdapter = DownloadProductAdapter(this, recyclerViewParams)
        }
        productRecyclerView.adapter = productAdapter
    }

    private fun initListener() {
        productAdapter?.setOnClickListener(object : OnItemClickListener<DownloadProductEntity> {
            override fun onClick(entity: DownloadProductEntity) {
                LogUtil.v(TAG, "点击-->${entity.display_name}")
                val intent = Intent(this@DownloadProductActivity, DownloadBranchActivity::class.java)
                intent.putExtra("clickPosition", entity.name)
                intent.putExtra("deviceId", curDeviceId)
                startActivityForResult(intent, IntentConstant.activityResultCodeProduct)
            }
        })
    }

    private fun initLoad() {
        loadProductData()
    }

    private fun loadProductData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == IntentConstant.activityResultCodeProduct ) {
                loadProductData()
            }
        }
    }

}