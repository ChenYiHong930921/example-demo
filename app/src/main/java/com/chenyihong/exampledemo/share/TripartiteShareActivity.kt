package com.chenyihong.exampledemo.share

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutShareActivityBinding
import com.chenyihong.exampledemo.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.share.tripartiteshare.FacebookShareActivity

class TripartiteShareActivity : BaseGestureDetectorActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutShareActivityBinding>(this, R.layout.layout_share_activity)

        binding.includeTitle.tvTitle.text = "Tripartite Share"
        binding.btnFacebookShare.setOnClickListener {
            startActivity(Intent(this, FacebookShareActivity::class.java))
        }
    }
}