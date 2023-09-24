package com.chenyihong.exampledemo.androidapi.media3

import android.content.Intent
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
        media3ListExampleAdapter.itemClickCallback = object : Media3ListExampleAdapter.ItemClickCallback {
            override fun onItemClick(data: ExampleListEntity) {
                // 打开一个半透明的Activity
                startActivity(Intent(this@Media3ListExampleActivity, TransparentActivity::class.java))
            }
        }
        media3ListExampleAdapter.setNewData(arrayListOf(ExampleListEntity(1, "https://minigame.vip/Uploads/images/2021/09/18/1631951892_page_img.mp4", "Video item"), ExampleListEntity(2, "", "Normal item"), ExampleListEntity(2, "", "Normal item"), ExampleListEntity(2, "", "Normal item"), ExampleListEntity(2, "", "Normal item"), ExampleListEntity(2, "", "Normal item"), ExampleListEntity(2, "", "Normal item"), ExampleListEntity(2, "", "Normal item"), ExampleListEntity(1, "https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4", "Video item"), ExampleListEntity(2, "", "Normal item"), ExampleListEntity(2, "", "Normal item")))
    }

    override fun onPause() {
        super.onPause()
        media3ListExampleAdapter.notifyVideoItemStatus(true)
    }

    override fun onResume() {
        super.onResume()
        media3ListExampleAdapter.notifyVideoItemStatus(false)
    }
}