package com.chenyihong.exampledemo.tripartite.admob.multipleprocess

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.databinding.LayoutMultipleProcessAdvertiseActivityBinding

const val SUBPROCESS_ACTION = "SUBPROCESS_EVENT"
const val MAIN_ACTION = "SUBPROCESS_EVENT"

const val EVENT_NAME = "eventName"
const val EVENT_PARAMS = "eventParams"

const val EVENT_MOTION = "motion"
const val EVENT_SHOW_INTERSTITIAL = "showInterstitial"
const val EVENT_SHOW_REWARDED = "showRewarded"
const val EVENT_SHOW_BANNER = "showBanner"
const val EVENT_HIDE_BANNER = "hideBanner"
const val EVENT_BACK = "back"

class SubProcessActivity : AppCompatActivity() {

    private lateinit var layoutMultipleProcessAdvertiseActivityBinding: LayoutMultipleProcessAdvertiseActivityBinding

    private val mainProcessEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                if (action == MAIN_ACTION) {
                    when (intent.getStringExtra(EVENT_NAME)) {
                        EVENT_MOTION -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                getParcelableExtra(EVENT_PARAMS, MotionEvent::class.java)
                            } else {
                                getParcelableExtra(EVENT_PARAMS) as? MotionEvent
                            }?.let {
                                dispatchTouchEvent(it)
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutMultipleProcessAdvertiseActivityBinding = LayoutMultipleProcessAdvertiseActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            it.includeTitle.tvTitle.text = "SubprocessExampleActivity"
            it.btnBack.setOnClickListener {
                sendEventToMainProcess(EVENT_BACK)
                finish()
            }
            it.btnShowInterstitialAd.isEnabled = true
            it.btnShowInterstitialAd.setOnClickListener { sendEventToMainProcess(EVENT_SHOW_INTERSTITIAL) }
            it.btnShowRewardedAd.isEnabled = true
            it.btnShowRewardedAd.setOnClickListener { sendEventToMainProcess(EVENT_SHOW_REWARDED) }
            it.btnShowBannerAd.isEnabled = true
            it.btnShowBannerAd.setOnClickListener { sendEventToMainProcess(EVENT_SHOW_BANNER) }
            it.btnHideBannerAd.isEnabled = true
            it.btnHideBannerAd.setOnClickListener { sendEventToMainProcess(EVENT_HIDE_BANNER) }
        }
        ContextCompat.registerReceiver(this, mainProcessEventReceiver, IntentFilter(MAIN_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED)
        startActivity(Intent(this, TransparentAdActivity::class.java))
    }

    private fun sendEventToMainProcess(eventName: String) {
        sendBroadcast(Intent(SUBPROCESS_ACTION).apply {
            setPackage(packageName)
            putExtra(EVENT_NAME, eventName)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mainProcessEventReceiver)
    }
}