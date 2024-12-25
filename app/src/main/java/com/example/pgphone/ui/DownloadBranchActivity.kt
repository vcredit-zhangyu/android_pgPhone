package com.example.pgphone.ui

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pgphone.R
import com.example.pgphone.adapter.DownloadBranchAdapter
import com.example.pgphone.api.MainApi
import com.example.pgphone.base.BaseActivity
import com.example.pgphone.constant.IntentConstant
import com.example.pgphone.entity.DownloadBranchEntity
import com.example.pgphone.extension.dismissLoading
import com.example.pgphone.extension.showLoading
import com.example.pgphone.extension.showToast
import com.example.pgphone.interfaces.OnItemClickListener
import com.example.pgphone.utils.LogUtil
import kotlinx.android.synthetic.main.activity_download_branch.*

/**
 * 下载站分支列表页面
 */
class DownloadBranchActivity : BaseActivity() {

    private val TAG: String = javaClass.simpleName
    private var productAdapter: DownloadBranchAdapter? = null
    private var productList: MutableList<DownloadBranchEntity>? = null
    private var clickPosition: String? = ""
    private var curDeviceId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_branch)
        initView()
        initListener()
        initLoad()
    }

    private fun initView() {
        baseToolbar.setTitle(getString(R.string.download_branch_title))
        productRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        if (productList == null) {
            productList = ArrayList()
        }
        productList?.run {
            productAdapter = DownloadBranchAdapter(this)
        }
        productRecyclerView.adapter = productAdapter
        clickPosition = intent.getStringExtra("clickPosition")
        curDeviceId = intent.getStringExtra("deviceId")
    }

    private fun initListener() {
        productAdapter?.setOnClickListener(object : OnItemClickListener<DownloadBranchEntity> {
            override fun onClick(entity: DownloadBranchEntity) {
                LogUtil.v(TAG, "点击-->${entity.display_name}")
                val intent = Intent(this@DownloadBranchActivity, DownloadDetailActivity::class.java)
                intent.putExtra("clickProduct", clickPosition)
                intent.putExtra("clickBranch", entity.name)
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

    private fun setOnResult() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
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