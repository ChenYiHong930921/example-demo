package com.chenyihong.exampledemo.tripartite.admob.multipleprocess

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.databinding.LayoutMultipleProcessAdvertiseActivityBinding
import com.chenyihong.exampledemo.tripartite.admob.ADVERTISE_TYPE_BANNER
import com.chenyihong.exampledemo.tripartite.admob.ADVERTISE_TYPE_INTERSTITIAL
import com.chenyihong.exampledemo.tripartite.admob.ADVERTISE_TYPE_REWARDED
import com.chenyihong.exampledemo.tripartite.admob.AdvertiseSdkController
import com.chenyihong.exampledemo.tripartite.admob.AdvertiseStatusListener
import com.google.android.material.snackbar.Snackbar

class MainProcessActivity : AppCompatActivity() {

    private lateinit var layoutMultipleProcessAdvertiseActivityBinding: LayoutMultipleProcessAdvertiseActivityBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutMultipleProcessAdvertiseActivityBinding = LayoutMultipleProcessAdvertiseActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            it.includeTitle.tvTitle.text = "MainProcessExampleActivity"
            it.btnOpenSubprocess.run {
                visibility = View.VISIBLE
                setOnClickListener { startActivity(Intent(this@MainProcessActivity, SubProcessActivity::class.java)) }
            }
            it.btnShowInterstitialAd.setOnClickListener { AdvertiseSdkController.showInterstitialAd() }
            it.btnShowRewardedAd.setOnClickListener { AdvertiseSdkController.showRewardedVideo() }
            it.btnShowBannerAd.setOnClickListener { AdvertiseSdkController.showBanner() }
            it.btnHideBannerAd.setOnClickListener { AdvertiseSdkController.hideBanner() }
        }
        AdvertiseSdkController.advertiseStatusListener = object : AdvertiseStatusListener {
            override fun availableChange(adType: Int, enable: Boolean) {
                when (adType) {
                    ADVERTISE_TYPE_INTERSTITIAL -> layoutMultipleProcessAdvertiseActivityBinding.btnShowInterstitialAd.isEnabled = enable
                    ADVERTISE_TYPE_REWARDED -> layoutMultipleProcessAdvertiseActivityBinding.btnShowRewardedAd.isEnabled = enable
                    ADVERTISE_TYPE_BANNER -> {
                        layoutMultipleProcessAdvertiseActivityBinding.btnShowBannerAd.isEnabled = enable
                        layoutMultipleProcessAdvertiseActivityBinding.btnHideBannerAd.isEnabled = enable
                    }
                }
            }

            override fun showFailure(message: String) {
                showSnakeBar(message)
            }
        }
        AdvertiseSdkController.initAdMobSdk(this)
    }

    override fun onResume() {
        super.onResume()
        AdvertiseSdkController.changeActivity(this)
    }

    private fun showSnakeBar(message: String) {
        runOnUiThread {
            Snackbar.make(layoutMultipleProcessAdvertiseActivityBinding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}