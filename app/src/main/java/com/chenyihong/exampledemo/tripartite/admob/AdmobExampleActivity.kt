package com.chenyihong.exampledemo.tripartite.admob

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutAdmobExampleActivityBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.AdapterStatus
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

const val TAG = "AdmobExample"

class AdmobExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutAdmobExampleActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_admob_example_activity)
        binding.includeTitle.tvTitle.text = "AdmobExample"

        MobileAds.initialize(this, object : OnInitializationCompleteListener {
            override fun onInitializationComplete(initializationStatus: InitializationStatus) {
                val readyAdapter = initializationStatus.adapterStatusMap.entries.find {
                    // 判断适配器初始化的状态
                    // 准备就绪 AdapterStatus.State.READY
                    // 没准备好 AdapterStatus.State.NOT_READY
                    it.value.initializationState == AdapterStatus.State.READY
                }
                // 有任意一种适配器初始化成功就可以开始加载广告
                if (readyAdapter != null) {
                    // 适配器的名称
                    val adapterName = readyAdapter.key
                    Log.i(TAG, "readyAdapter adapterName:$adapterName")
                    loadInterstitialAd()
                    loadRewardedVideoAd()
                    createBannerAdView()
                }
            }
        })
        binding.btnShowInterstitialAd.setOnClickListener { showInterstitialAd() }
        binding.btnShowRewardedAd.setOnClickListener { showRewardedVideo() }
        binding.btnShowBannerAd.setOnClickListener { showBanner() }
        binding.btnHideBannerAd.setOnClickListener { hideBanner() }
    }

    //<editor-folder desc = ”interstitial ad“>

    private var interstitialAd: InterstitialAd? = null

    // 插屏广告加载状态的回调
    private val interstitialAdLoadCallback = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            super.onAdLoaded(interstitialAd)
            Log.i(TAG, "interstitial onAdLoaded")
            // 加载成功
            this@AdmobExampleActivity.interstitialAd = interstitialAd
            // 设置广告事件回调
            this@AdmobExampleActivity.interstitialAd?.fullScreenContentCallback = interstitialAdCallback
            binding.btnShowInterstitialAd.isEnabled = true
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            Log.e(TAG, "interstitial onAdFailedToLoad error:${loadAdError.message}")
            // 加载失败
        }
    }

    // 插屏广告相关事件回调
    private val interstitialAdCallback = object : FullScreenContentCallback() {
        override fun onAdImpression() {
            super.onAdImpression()
            // 被记录为展示成功时调用
            Log.i(TAG, "interstitial onAdImpression")
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            // 显示时调用
            Log.i(TAG, "interstitial onAdShowedFullScreenContent")
        }

        override fun onAdClicked() {
            super.onAdClicked()
            // 被点击时调用
            Log.i(TAG, "interstitial onAdClicked")
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            // 隐藏时调用，此时销毁当前的插屏广告对象，重新加载插屏广告
            Log.i(TAG, "interstitial onAdDismissedFullScreenContent")
            interstitialAd = null
            binding.btnShowInterstitialAd.isEnabled = false
            loadInterstitialAd()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            super.onAdFailedToShowFullScreenContent(adError)
            // 展示失败时调用，此时销毁当前的插屏广告对象，重新加载插屏广告
            Log.e(TAG, "interstitial onAdFailedToShowFullScreenContent error:${adError.message}")
            interstitialAd = null
            binding.btnShowInterstitialAd.isEnabled = false
            loadInterstitialAd()
        }
    }

    private fun loadInterstitialAd() {
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", AdRequest.Builder().build(), interstitialAdLoadCallback)
    }

    private fun showInterstitialAd() {
        interstitialAd?.show(this)
    }
    //</editor-folder>

    //<editor-folder desc = ”rewarded video ad“>

    private var rewardedAd: RewardedAd? = null

    private val rewardedAdLoadCallback = object : RewardedAdLoadCallback() {
        override fun onAdLoaded(rewardedAd: RewardedAd) {
            super.onAdLoaded(rewardedAd)
            Log.i(TAG, "rewardedVideo onAdLoaded")
            // 加载成功
            this@AdmobExampleActivity.rewardedAd = rewardedAd
            // 设置广告事件回调
            this@AdmobExampleActivity.rewardedAd?.fullScreenContentCallback = rewardedVideoAdCallback
            binding.btnShowRewardedAd.isEnabled = true
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            // 加载失败
            Log.e(TAG, "rewardedVideo onAdFailedToLoad error:${loadAdError.message}")
        }
    }
    private val rewardedVideoAdCallback = object : FullScreenContentCallback() {
        override fun onAdImpression() {
            super.onAdImpression()
            // 被记录为展示成功时调用
            Log.i(TAG, "rewardedVideo onAdImpression")
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            // 显示时调用
            Log.i(TAG, "rewardedVideo onAdShowedFullScreenContent")
        }

        override fun onAdClicked() {
            super.onAdClicked()
            // 被点击时调用
            Log.i(TAG, "rewardedVideo onAdClicked")
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            // 隐藏时调用，此时销毁当前的激励视频广告对象，重新加载激励视频广告
            Log.i(TAG, "rewardedVideo onAdDismissedFullScreenContent")
            rewardedAd = null
            binding.btnShowRewardedAd.isEnabled = false
            loadRewardedVideoAd()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            super.onAdFailedToShowFullScreenContent(adError)
            // 展示失败时调用，此时销毁当前的激励视频广告对象，重新加载激励视频广告
            Log.e(TAG, "rewardedVideo onAdFailedToShowFullScreenContent error:${adError.message}")
            rewardedAd = null
            binding.btnShowRewardedAd.isEnabled = false
            loadRewardedVideoAd()
        }

    }
    private val rewardedVideoAdEarnedCallback = OnUserEarnedRewardListener {
        // 用户获得奖励
        // 奖励的类型
        val type = it.type
        // 奖励的金额
        val amount = it.amount
        Log.i(TAG, "rewardedVideo onUserEarnedReward type:$type, amount:$amount")
    }

    private fun loadRewardedVideoAd() {
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", AdRequest.Builder().build(), rewardedAdLoadCallback)
    }

    private fun showRewardedVideo() {
        rewardedAd?.show(this, rewardedVideoAdEarnedCallback)
    }
    //</editor-folder>

    //<editor-folder desc = ”banner ad“>

    private var bannerAdView: AdView? = null

    private val bannerListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            // 广告加载成功
            Log.i(TAG, "banner onAdLoaded")
            binding.btnShowBannerAd.isEnabled = true
            binding.btnHideBannerAd.isEnabled = true
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            // 广告加载失败
            Log.e(TAG, "banner onAdFailedToLoad error:${loadAdError.message}")
        }

        override fun onAdImpression() {
            super.onAdImpression()
            // 被记录为展示成功时调用
            Log.i(TAG, "banner onAdImpression")
        }

        override fun onAdClicked() {
            super.onAdClicked()
            // 被点击时调用
            Log.i(TAG, "banner onAdClicked")
        }

        override fun onAdOpened() {
            super.onAdOpened()
            // 广告落地页打开时调用
            Log.i(TAG, "banner onAdOpened")
        }

        override fun onAdClosed() {
            super.onAdClosed()
            // 广告落地页关闭时调用
            Log.i(TAG, "banner onAdClosed")
        }
    }

    private fun createBannerAdView() {
        // 获取页面的根布局
        val rootView = findViewById<FrameLayout>(android.R.id.content)
        bannerAdView = AdView(this)
        bannerAdView?.run {
            // 设置Banner的尺寸
            setAdSize(AdSize.BANNER)
            adUnitId = "ca-app-pub-3940256099942544/6300978111"
            // 设置广告事件回调
            adListener = bannerListener
            val bannerViewLayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            // 设置显示在页面的底部中间
            bannerViewLayoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            layoutParams = bannerViewLayoutParams
            alpha = 0f
            // 把 Banner Ad 添加到根布局
            rootView.addView(this)
            //加载广澳
            loadAd(AdRequest.Builder().build())
        }
    }

    private fun showBanner() {
        bannerAdView?.alpha = 1f
    }

    private fun hideBanner() {
        bannerAdView?.alpha = 0f
    }
    //</editor-folder>
}