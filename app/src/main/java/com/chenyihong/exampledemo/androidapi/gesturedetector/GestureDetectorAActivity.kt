package com.chenyihong.exampledemo.androidapi.gesturedetector

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.chenyihong.exampledemo.databinding.LayoutGestureDetectorActivityBinding

class GestureDetectorAActivity : BaseGestureDetectorActivity<LayoutGestureDetectorActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutGestureDetectorActivityBinding {
        return LayoutGestureDetectorActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvTitle.text = "Gesture Detector Api"
        binding.btnEntrance.setOnClickListener {
            startActivity(Intent(this, GestureDetectorBActivity::class.java))
        }
    }
}