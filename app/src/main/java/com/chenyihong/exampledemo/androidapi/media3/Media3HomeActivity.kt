package com.chenyihong.exampledemo.androidapi.media3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutMedia3HomeActivityBinding

class Media3HomeActivity : BaseGestureDetectorActivity<LayoutMedia3HomeActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutMedia3HomeActivityBinding {
        return LayoutMedia3HomeActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            includeTitle.tvTitle.text = "Media3 Example"
            btnNormalPlayMedia.setOnClickListener {
                startActivity(Intent(this@Media3HomeActivity, Media3ExampleActivity::class.java).apply {
                    putExtra("backgroundPlay", false)
                })
            }
            btnBackgroundPlayMedia.setOnClickListener {
                startActivity(Intent(this@Media3HomeActivity, Media3ExampleActivity::class.java).apply {
                    putExtra("backgroundPlay", true)
                })
            }
        }
    }
}