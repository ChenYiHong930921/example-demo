package com.chenyihong.exampledemo.gesturedetector

import android.annotation.SuppressLint
import android.content.Intent
import android.media.audiofx.LoudnessEnhancer
import android.os.Bundle
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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