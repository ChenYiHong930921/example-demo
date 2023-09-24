package com.chenyihong.exampledemo.androidapi.media3

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutTransparentActivityBinding

class TransparentActivity : BaseGestureDetectorActivity<LayoutTransparentActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutTransparentActivityBinding {
        return LayoutTransparentActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeTitle.tvTitle.text = "TransparentActivity"
    }
}