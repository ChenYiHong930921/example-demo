package com.chenyihong.exampledemo.customview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import com.chenyihong.exampledemo.databinding.LayoutCustomChartViewActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

class CustomChartViewActivity : BaseGestureDetectorActivity<LayoutCustomChartViewActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutCustomChartViewActivityBinding {
        return LayoutCustomChartViewActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeTitle.tvTitle.text = "Custom Chart View"
    }
}