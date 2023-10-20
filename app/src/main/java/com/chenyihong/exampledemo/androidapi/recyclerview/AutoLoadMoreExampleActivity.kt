package com.chenyihong.exampledemo.androidapi.recyclerview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutAutoLoadMoreExampleActivityBinding

class AutoLoadMoreExampleActivity : BaseGestureDetectorActivity<LayoutAutoLoadMoreExampleActivityBinding>() {

    private val prePageCount = 20

    private var verticalRvVisibleItemCount = 0

    private val verticalRvAdapter = AutoLoadMoreExampleAdapter()

    private val verticalRvScrollListener = object : RecyclerView.OnScrollListener() {

        private var scrollToBottom = false

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            (recyclerView.layoutManager as? LinearLayoutManager)?.let { linearLayoutManager ->
                // 判断是拖动或者惯性滑动
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    if (verticalRvVisibleItemCount == 0) {
                        // 获取列表可视Item的数量
                        verticalRvVisibleItemCount = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition()
                    }
                    // 判断是向着列表尾部滚动，并且临界点已经显示，可以加载更多数据。
                    if (scrollToBottom && linearLayoutManager.findViewByPosition(linearLayoutManager.itemCount - 1 - verticalRvVisibleItemCount) != null) {
                        loadData()
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // 判断列表是向列表尾部滚动
            scrollToBottom = dy > 0
        }
    }

    private var horizontalRvVisibleItemCount = 0

    private val horizontalRvAdapter = AutoLoadMoreExampleAdapter(false)

    private val horizontalRvScrollListener = object : RecyclerView.OnScrollListener() {

        private var scrollToEnd = false

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            (recyclerView.layoutManager as? LinearLayoutManager)?.let { linearLayoutManager ->
                // 判断是拖动或者惯性滑动
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    if (horizontalRvVisibleItemCount == 0) {
                        // 获取列表可视Item的数量
                        horizontalRvVisibleItemCount = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition()
                    }
                    // 判断是向着列表尾部滚动，并且临界点已经显示，可以加载更多数据。
                    if (scrollToEnd && linearLayoutManager.findViewByPosition(linearLayoutManager.itemCount - 1 - horizontalRvVisibleItemCount) != null) {
                        loadData()
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // 判断列表是向列表尾部滚动
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
        val init = verticalRvAdapter.itemCount == 0
        val start = verticalRvAdapter.itemCount
        val end = verticalRvAdapter.itemCount + prePageCount

        val testData = ArrayList<String>()
        for (index in start until end) {
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