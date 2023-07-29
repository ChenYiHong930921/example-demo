package com.chenyihong.exampledemo.customview.itemdecoration

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutCustomItemDecorationExampleActivityBinding
import com.chenyihong.exampledemo.utils.DensityUtil

class CustomItemDecorationExampleActivity : BaseGestureDetectorActivity<LayoutCustomItemDecorationExampleActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutCustomItemDecorationExampleActivityBinding {
        return LayoutCustomItemDecorationExampleActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            includeTitle.tvTitle.setTextColor(ContextCompat.getColor(this@CustomItemDecorationExampleActivity, R.color.white))
            includeTitle.tvTitle.text = "Custom ItemDecoration Example"
            val adapter = ItemDecorationExampleAdapter()
            rvExampleDataContainer.addItemDecoration(CustomItemDecoration(DensityUtil.dp2Px(16), 6, DensityUtil.dp2Px(2), ContextCompat.getColor(this@CustomItemDecorationExampleActivity, R.color.color_black_363636)))
            rvExampleDataContainer.adapter = adapter
            adapter.setNewData(arrayListOf(
                ItemDecorationExampleDataEntity(0, "Home", R.mipmap.icon_tag_all, true),
                ItemDecorationExampleDataEntity(1, "Recently Played", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(2, "New Games", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(3, "Trending Now", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(4, "Updated", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(5, "The Game Blog", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(6, "Random", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(7, "2 Player", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(8, "Adventure", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(9, "Action", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(10, "Strategy", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(11, "Casual", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(12, ".io", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(13, "Horror", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(14, "3d", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(15, "Driving", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(16, "Shoting", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(17, "Puzzel", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(18, "Beauty", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(19, "Parkour", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
                ItemDecorationExampleDataEntity(20, "TestData", R.mipmap.icon_tag_all, false),
            ))
        }
    }
}