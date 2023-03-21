package com.chenyihong.exampledemo.androidapi.timechange

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
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

    private var countDownTime = 0
    private var onlineTime = 0

    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
    private val timeTikRunnable = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.uptimeMillis()
            val nextTime = currentTime + (1000 - currentTime % 1000)
            handler.postAtTime(this, nextTime)
            countDownTime += 1
            if (countDownTime % 60 == 0) {
                onlineTime += 1
            }
            Log.i(TAG, "countDownTime:$countDownTime onlineTime:$onlineTime")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(timeChangeBroadcastReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
        })
        val currentTime = SystemClock.uptimeMillis()
        val nextTime = currentTime + (1000 - currentTime % 1000)
        handler.postAtTime(timeTikRunnable, nextTime)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timeTikRunnable)
        unregisterReceiver(timeChangeBroadcastReceiver)
    }
}