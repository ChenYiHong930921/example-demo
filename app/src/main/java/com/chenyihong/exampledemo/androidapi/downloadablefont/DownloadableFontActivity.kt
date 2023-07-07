package com.chenyihong.exampledemo.androidapi.downloadablefont

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import com.chenyihong.exampledemo.databinding.LayoutDownloadableFontActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

class DownloadableFontActivity : BaseGestureDetectorActivity<LayoutDownloadableFontActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutDownloadableFontActivityBinding {
        return LayoutDownloadableFontActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeTitle.tvTitle.text = "DownloadAbleFont"
    }
}