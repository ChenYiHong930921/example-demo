package com.chenyihong.exampledemo.customview

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutCustomChartViewActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

class CustomChartViewActivity : BaseGestureDetectorActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutCustomChartViewActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_custom_chart_view_activity)
        binding.includeTitle.tvTitle.text = "Custom Chart View"
    }
}