package com.chenyihong.exampledemo.androidapi.fullscreen

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.*
import com.chenyihong.exampledemo.databinding.LayoutFullScreenActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

class FullScreenActivity : BaseGestureDetectorActivity<LayoutFullScreenActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutFullScreenActivityBinding {
        return LayoutFullScreenActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}