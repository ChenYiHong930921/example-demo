package com.chenyihong.exampledemo.androidapi.autofill

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutAutoFillExampleActivityBinding

class AutoFillExampleActivity : BaseGestureDetectorActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutAutoFillExampleActivityBinding>(this, R.layout.layout_auto_fill_example_activity)
    }
}