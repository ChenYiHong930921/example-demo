package com.chenyihong.exampledemo.androidapi.nfc

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutNfcShareExampleActivityBinding

class NFCShareExampleActivity : BaseGestureDetectorActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutNfcShareExampleActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_nfc_share_example_activity)
        binding.includeTitle.tvTitle.text = "NFC Share Example"
    }
}