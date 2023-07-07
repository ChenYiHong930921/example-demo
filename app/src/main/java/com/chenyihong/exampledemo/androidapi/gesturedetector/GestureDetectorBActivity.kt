package com.chenyihong.exampledemo.androidapi.gesturedetector

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.chenyihong.exampledemo.databinding.LayoutGestureDetectorActivityBinding

class GestureDetectorBActivity : BaseGestureDetectorActivity<LayoutGestureDetectorActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutGestureDetectorActivityBinding {
        return LayoutGestureDetectorActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvTitle.text = "Gesture Detector B"
        binding.btnEntrance.visibility = View.GONE
    }
}