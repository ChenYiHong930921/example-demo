package com.chenyihong.exampledemo.androidapi.search

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.chenyihong.exampledemo.databinding.LayoutSearchExampleActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

class SearchExampleActivity : BaseGestureDetectorActivity<LayoutSearchExampleActivityBinding>() {

    override fun onSearchRequested(): Boolean {
        val appData = Bundle()
        appData.putString("gender", "male")
        appData.putInt("age", 24)
        startSearch(null, false, appData, false)
        return true
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutSearchExampleActivityBinding {
        return LayoutSearchExampleActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeTitle.tvTitle.text = "Search Api"
        binding.btnSearchView.setOnClickListener { startActivity(Intent(this, SearchActivity::class.java)) }
        binding.btnSearchDialog.setOnClickListener { onSearchRequested() }
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchManager.setOnDismissListener {
            showToast("Search Dialog dismiss")
        }
    }
}