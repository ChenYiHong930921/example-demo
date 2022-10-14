package com.chenyihong.exampledemo.downloadablefont

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutDownloadableFontActivityBinding
import com.chenyihong.exampledemo.gesturedetector.BaseGestureDetectorActivity

class DownloadableFontActivity : BaseGestureDetectorActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutDownloadableFontActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_downloadable_font_activity)
        binding.includeTitle.tvTitle.text = "DownloadAbleFont"
    }
}