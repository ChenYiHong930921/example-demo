package com.chenyihong.exampledemo.androidapi.trafficstats

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutTrafficStatsActivityBinding

class TrafficStatsActivity : BaseGestureDetectorActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutTrafficStatsActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_traffic_stats_activity)
    }
}