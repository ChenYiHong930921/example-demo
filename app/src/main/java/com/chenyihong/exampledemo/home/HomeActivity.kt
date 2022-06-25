package com.chenyihong.exampledemo.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.camerax.CameraActivity
import com.chenyihong.exampledemo.customview.CustomViewActivity
import com.chenyihong.exampledemo.databinding.LayoutHomeActivityBinding
import com.chenyihong.exampledemo.entity.OptionEntity
import com.chenyihong.exampledemo.fragmentresultapi.FragmentResultApiActivity
import com.chenyihong.exampledemo.resultapi.ResultApiActivity
import com.chenyihong.exampledemo.share.ShareActivity
import com.chenyihong.exampledemo.share.systemshare.SystemShareActivity
import com.chenyihong.exampledemo.tripartitelogin.TripartiteLoginActivity
import com.chenyihong.exampledemo.web.WebViewActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<LayoutHomeActivityBinding>(this, R.layout.layout_home_activity)

        val optionList = arrayListOf(OptionEntity("Custom View", CustomViewActivity::class.java),
            OptionEntity("Activity Result Api", ResultApiActivity::class.java),
            OptionEntity("Camera", CameraActivity::class.java),
            OptionEntity("TripartiteLogin", TripartiteLoginActivity::class.java),
            OptionEntity("Share", ShareActivity::class.java),
            OptionEntity("Fragment Result Api", FragmentResultApiActivity::class.java),
            OptionEntity("JsAndroidInteraction", WebViewActivity::class.java))

        val optionAdapter = OptionAdapter()
        binding.rvOption.adapter = optionAdapter

        optionAdapter.setNewData(optionList)
    }
}