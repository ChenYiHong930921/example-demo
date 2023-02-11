package com.chenyihong.exampledemo.flavor

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.BuildConfig
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutFlavorExampleActivityBinding

class FlavorExampleActivity : BaseGestureDetectorActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<LayoutFlavorExampleActivityBinding>(this, R.layout.layout_flavor_example_activity)
        binding.includeTitle.tvTitle.text = "Flavor ${BuildConfig.FLAVOR}"
        binding.tvFlavorBuildConfigValue.text = BuildConfig.example_value
        binding.tvFlavorResValue.text = getString(R.string.example_value)
        binding.tvVersionInfo.text = "VersionCode:${BuildConfig.VERSION_CODE} VersionName:${BuildConfig.VERSION_NAME}"
    }
}