package com.chenyihong.exampledemo.androidapi.media3

import android.os.Bundle
import android.view.LayoutInflater
import androidx.media3.common.util.UnstableApi
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutMeida3ListExampleAcitivityBinding

@UnstableApi
class Media3ListExampleActivity : BaseGestureDetectorActivity<LayoutMeida3ListExampleAcitivityBinding>() {

    private val media3ListExampleAdapter = Media3ListExampleAdapter()

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutMeida3ListExampleAcitivityBinding {
        return LayoutMeida3ListExampleAcitivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.rvMedia3ListContainer.adapter = media3ListExampleAdapter

    }
}