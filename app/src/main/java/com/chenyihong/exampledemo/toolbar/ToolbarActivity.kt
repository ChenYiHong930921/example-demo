package com.chenyihong.exampledemo.toolbar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutToolbarActivityBinding

const val TAG = "ToolBalSimpleTag"

class ToolbarActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.example_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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

    private fun showToast(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
    }
}