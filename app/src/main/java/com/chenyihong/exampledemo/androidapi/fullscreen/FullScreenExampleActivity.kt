package com.chenyihong.exampledemo.androidapi.fullscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.chenyihong.exampledemo.databinding.LayoutFullScreenExampleActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

class FullScreenExampleActivity : BaseGestureDetectorActivity<LayoutFullScreenExampleActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutFullScreenExampleActivityBinding {
        return LayoutFullScreenExampleActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeTitle.tvTitle.text = "FullScreen Api"
        binding.btnFullScreen.setOnClickListener {
            startActivity(Intent(this, FullScreenActivity::class.java))
        }

        binding.btnImmersion.setOnClickListener {
            startActivity(Intent(this, ImmersionActivity::class.java))
        }
    }
}