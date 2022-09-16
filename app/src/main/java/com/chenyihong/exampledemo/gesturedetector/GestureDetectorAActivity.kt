package com.chenyihong.exampledemo.gesturedetector

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutGestureDetectorActivityBinding

class GestureDetectorAActivity : BaseGestureDetectorActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutGestureDetectorActivityBinding>(this, R.layout.layout_gesture_detector_activity)
        binding.tvTitle.text = "Gesture Detector A"
        binding.btnEntrance.setOnClickListener {
            startActivity(Intent(this, GestureDetectorBActivity::class.java))
        }
    }
}