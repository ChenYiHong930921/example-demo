package com.chenyihong.exampledemo.tripartite.share

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.chenyihong.exampledemo.databinding.LayoutShareActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

class TripartiteShareActivity : BaseGestureDetectorActivity<LayoutShareActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutShareActivityBinding {
        return LayoutShareActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeTitle.tvTitle.text = "Tripartite Share"
        binding.btnFacebookShare.setOnClickListener {
            startActivity(Intent(this, FacebookShareActivity::class.java))
        }
    }
}