package com.chenyihong.exampledemo.share

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutShareActivityBinding
import com.chenyihong.exampledemo.share.systemshare.SystemShareActivity
import com.chenyihong.exampledemo.share.tripartiteshare.FacebookShareActivity

class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<LayoutShareActivityBinding>(this, R.layout.layout_share_activity)

        binding.btnSystemShare.setOnClickListener {
            startActivity(Intent(this, SystemShareActivity::class.java))
        }

        binding.btnFacebookShare.setOnClickListener {
            startActivity(Intent(this, FacebookShareActivity::class.java))
        }
    }
}