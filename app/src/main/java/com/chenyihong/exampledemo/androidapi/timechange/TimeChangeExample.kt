package com.chenyihong.exampledemo.androidapi.timechange

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import java.util.*

const val TAG = "TimeChangeExampleTag"

class TimeChangeExample : BaseGestureDetectorActivity() {

    private val timeChangeBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_TIME_TICK) {
                Log.i(TAG, "ACTION_TIME_TICK time:${Date().toLocaleString()}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(timeChangeBroadcastReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timeChangeBroadcastReceiver)
    }
}