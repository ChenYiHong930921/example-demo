package com.chenyihong.exampledemo.tripartite.admob.multipleprocess

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.tripartite.admob.AdvertiseSdkController

class TransparentAdActivity : AppCompatActivity() {

    private val subprocessEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                if (action == SUBPROCESS_ACTION) {
                    when (intent.getStringExtra(EVENT_NAME)) {
                        EVENT_SHOW_INTERSTITIAL -> AdvertiseSdkController.showInterstitialAd()
                        EVENT_SHOW_REWARDED -> AdvertiseSdkController.showRewardedVideo()
                        EVENT_SHOW_BANNER -> AdvertiseSdkController.showBanner()
                        EVENT_HIDE_BANNER -> AdvertiseSdkController.hideBanner()
                        EVENT_BACK -> finish()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_transparent_ad_activity)
        ContextCompat.registerReceiver(this, subprocessEventReceiver, IntentFilter(SUBPROCESS_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED)
        AdvertiseSdkController.changeActivity(this)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            sendBroadcast(Intent(MAIN_ACTION).apply {
                setPackage(packageName)
                putExtra(EVENT_NAME, EVENT_MOTION)
                putExtra(EVENT_PARAMS, it)
            })
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(subprocessEventReceiver)
    }
}