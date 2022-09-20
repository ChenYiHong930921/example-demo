package com.chenyihong.exampledemo.fullscreen

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutFullScreenExampleActivityBinding
import com.chenyihong.exampledemo.gesturedetector.BaseGestureDetectorActivity

class FullScreenExampleActivity : BaseGestureDetectorActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<LayoutFullScreenExampleActivityBinding>(this, R.layout.layout_full_screen_example_activity)

        binding.btnFullScreen.setOnClickListener {
            startActivity(Intent(this, FullScreenActivity::class.java))
        }

        binding.btnImmersion.setOnClickListener {
            startActivity(Intent(this, ImmersionActivity::class.java))
        }
    }
}