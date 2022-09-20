package com.chenyihong.exampledemo.fullscreen

import android.os.Bundle
import androidx.core.view.*
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutFullScreenActivityBinding
import com.chenyihong.exampledemo.gesturedetector.BaseGestureDetectorActivity

class FullScreenActivity : BaseGestureDetectorActivity() {

    lateinit var binding: LayoutFullScreenActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_full_screen_activity)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}