package com.chenyihong.exampledemo.androidapi.recyclerview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutAutoLoadMoreExampleActivityBinding

class AutoLoadMoreExampleActivity : BaseGestureDetectorActivity<LayoutAutoLoadMoreExampleActivityBinding>() {

    private val testData = ArrayList<String>()

    private val prePageCount = 20

    private val verticalRvAdapter = AutoLoadMoreExampleAdapter()

    private val verticalRvScrollListener = object : RecyclerView.OnScrollListener() {

        private var scrollToBottom = false

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            (recyclerView.layoutManager as? LinearLayoutManager)?.let { linearLayoutManager ->
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    if (scrollToBottom && linearLayoutManager.findViewByPosition(linearLayoutManager.itemCount - (linearLayoutManager.findLastCompletelyVisibleItemPosition() - linearLayoutManager.findFirstCompletelyVisibleItemPosition()) - 1) != null) {
                        loadData()
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            scrollToBottom = dy > 0
        }
    }

    private val horizontalRvAdapter = AutoLoadMoreExampleAdapter(false)

    private val horizontalRvScrollListener = object : RecyclerView.OnScrollListener() {

        private var scrollToEnd = false

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            (recyclerView.layoutManager as? LinearLayoutManager)?.let { linearLayoutManager ->
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    if (scrollToEnd && linearLayoutManager.findViewByPosition(linearLayoutManager.itemCount - (linearLayoutManager.findLastCompletelyVisibleItemPosition() - linearLayoutManager.findFirstCompletelyVisibleItemPosition()) - 1) != null) {
                        loadData()
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            scrollToEnd = dx > 0
        }
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutAutoLoadMoreExampleActivityBinding {
        return LayoutAutoLoadMoreExampleActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeTitle.tvTitle.text = "AutoLoadMoreExample"

        binding.rvExampleDataContainerVertical.adapter = verticalRvAdapter
        binding.rvExampleDataContainerVertical.addOnScrollListener(verticalRvScrollListener)

        binding.rvExampleDataContainerHorizontal.adapter = horizontalRvAdapter
        binding.rvExampleDataContainerHorizontal.addOnScrollListener(horizontalRvScrollListener)

        loadData()
    }

    fun loadData() {
        val init = testData.isEmpty()
        for (index in testData.size until testData.size + prePageCount) {
            testData.add("item$index")
        }
        if (init) {
            verticalRvAdapter.setNewData(testData)
            horizontalRvAdapter.setNewData(testData)
        } else {
            verticalRvAdapter.addData(testData)
            horizontalRvAdapter.addData(testData)
        }
    }
}