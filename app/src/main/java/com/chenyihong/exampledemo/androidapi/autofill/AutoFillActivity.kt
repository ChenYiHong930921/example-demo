package com.chenyihong.exampledemo.androidapi.autofill

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutAutoFillActivityBinding

class AutoFillActivity : BaseGestureDetectorActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutAutoFillActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_auto_fill_activity)
    }
}