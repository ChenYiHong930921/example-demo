package com.chenyihong.exampledemo.fullscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutFullScreenExampleActivityBinding

class FullScreenExampleActivity :AppCompatActivity() {

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