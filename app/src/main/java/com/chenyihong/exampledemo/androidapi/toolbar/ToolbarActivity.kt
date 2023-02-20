package com.chenyihong.exampledemo.androidapi.toolbar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutToolbarActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

const val TAG = "ToolBalSimpleTag"

class ToolbarActivity : BaseGestureDetectorActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.example_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // 如果需要在运行时对菜单进行调整(删除或增加)，在此处理
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 在此处理菜单项的点击事件
        when (item.itemId) {
            R.id.action_search -> {
                showToast("click search menu")
            }
            R.id.action_scan -> {
                showToast("click scan menu")
            }
            R.id.action_setting -> {
                showToast("click setting menu")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutToolbarActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_toolbar_activity)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setHomeAsUpIndicator(R.drawable.icon_back)
            setDisplayHomeAsUpEnabled(true)
        }
    }
}