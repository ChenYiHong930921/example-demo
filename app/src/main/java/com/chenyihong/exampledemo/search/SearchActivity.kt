package com.chenyihong.exampledemo.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.adapter.TextDataAdapter
import com.chenyihong.exampledemo.databinding.LayoutSearchActivityBinding
import com.chenyihong.exampledemo.gesturedetector.BaseGestureDetectorActivity

const val TAG = "SearchExampleTag"

class SearchActivity : BaseGestureDetectorActivity() {

    private lateinit var binding: LayoutSearchActivityBinding

    private val textDataAdapter = TextDataAdapter()

    private val originData = ArrayList<String>()

    private var lastQueryValue = ""

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.example_seach_menu, menu)
        menu?.run {
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val searchView = findItem(R.id.action_search).actionView as SearchView
            searchView.setOnCloseListener {
                textDataAdapter.setNewData(originData)
                false
            }
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            if (lastQueryValue.isNotEmpty()) {
                searchView.setQuery(lastQueryValue, false)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clear_search_histor) {
            SearchRecentSuggestions(this, RecentSearchProvider.AUTHORITY, RecentSearchProvider.MODE)
                .clearHistory()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_search_activity)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            title = "SearchExample"
            setHomeAsUpIndicator(R.drawable.icon_back)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.rvContent.adapter = textDataAdapter
        originData.add("test data qwertyuiop")
        originData.add("test data asdfghjkl")
        originData.add("test data zxcvbnm")
        originData.add("test data 123456789")
        originData.add("test data /.,?-+")
        textDataAdapter.setNewData(originData)
        // 获取搜索内容
        getQueryKey(intent, false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // 更新Intent数据
        setIntent(intent)
        // 获取搜索内容
        getQueryKey(intent, true)
    }

    private fun getQueryKey(intent: Intent?, newIntent: Boolean) {
        intent?.run {
            if (Intent.ACTION_SEARCH == action) {
                val queryKey = getStringExtra(SearchManager.QUERY) ?: ""
                if (queryKey.isNotEmpty()) {
                    SearchRecentSuggestions(this@SearchActivity, RecentSearchProvider.AUTHORITY, RecentSearchProvider.MODE)
                        .saveRecentQuery(queryKey, "history $queryKey")
                    if (!newIntent) {
                        lastQueryValue = queryKey
                    }
                    val appData = getBundleExtra(SearchManager.APP_DATA)
                    doSearch(queryKey, appData)
                }
            }
        }
    }

    private fun doSearch(queryKey: String, appData: Bundle?) {
        Log.i(TAG, "doSearch queryKey:$queryKey")
        appData?.run {
            val gender = getString("gender") ?: ""
            val age = getInt("age")
            Log.i(TAG, "doSearch gender:$gender")
            Log.i(TAG, "doSearch age:$age")
        }
        val filterData = originData.filter { it.contains(queryKey) } as ArrayList<String>
        textDataAdapter.setNewData(filterData)
    }
}