package com.chenyihong.exampledemo.tripartite.admob

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutAdmobExampleActivityBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.AdapterStatus
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

const val TAG = "AdmobExample"

class AdmobExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutAdmobExampleActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_admob_example_activity)
        binding.includeTitle.tvTitle.text = TAG

        MobileAds.initialize(this) { initializationStatus ->
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
                loadNativeAd()
            }
        }
        binding.btnShowInterstitialAd.setOnClickListener { showInterstitialAd() }
        binding.btnShowRewardedAd.setOnClickListener { showRewardedVideo() }
        binding.btnShowBannerAd.setOnClickListener { showBanner() }
        binding.btnHideBannerAd.setOnClickListener { hideBanner() }
        binding.btnStopNativeAd.setOnClickListener { showChoseMuteNativeAdDialog() }
    }

    //<editor-folder desc = "interstitial ad">

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

    //<editor-folder desc = "rewarded video ad">

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

    // <editor-folder desc = "banner ad">

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

    // <editor-folder desc = "native ad">

    private var nativeAdView: NativeAdView? = null
    private var currentNativeAd: NativeAd? = null
    private val muteThisAdReason = ArrayList<MuteThisAdReason>()

    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            // 广告加载成功
            Log.i(TAG, "nativeAd onAdLoaded")
            nativeAdView?.let { binding.flNativeAdContainer.addView(it) }
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            // 广告加载失败
            Log.e(TAG, "nativeAd onAdFailedToLoad error:${loadAdError}")
        }

        override fun onAdOpened() {
            super.onAdOpened()
            // 广告页打开
            Log.i(TAG, "nativeAd onAdOpened")
        }

        override fun onAdClicked() {
            super.onAdClicked()
            // 广告被点击
            Log.i(TAG, "nativeAd onAdClicked")
        }

        override fun onAdClosed() {
            super.onAdClosed()
            // 广告页关闭
            Log.i(TAG, "nativeAd onAdClosed")
        }
    }

    private val muteListener = MuteThisAdListener {
        // 广告关闭回调
        Log.i(TAG, "this native ad been muted")
    }

    @SuppressLint("InflateParams")
    private fun populateNativeAdView() {
        currentNativeAd?.let { nativeAd ->
            nativeAdView?.let { binding.flNativeAdContainer.removeView(it) }
            (LayoutInflater.from(this).inflate(R.layout.layout_admob_native_ad, null) as? NativeAdView)?.run {
                iconView = findViewById<AppCompatImageView>(R.id.iv_ad_app_icon).apply {
                    nativeAd.icon?.let { setImageDrawable(it.drawable) }
                    visibility = if (nativeAd.icon != null) View.VISIBLE else View.GONE
                }
                headlineView = findViewById<AppCompatTextView>(R.id.tv_ad_headline).apply {
                    text = nativeAd.headline
                }
                advertiserView = findViewById<AppCompatTextView>(R.id.tv_advertiser).apply {
                    text = nativeAd.advertiser
                    visibility = if (nativeAd.advertiser != null) View.VISIBLE else View.INVISIBLE
                }
                starRatingView = findViewById<AppCompatRatingBar>(R.id.rb_ad_stars).apply {
                    nativeAd.starRating?.let { rating = it.toFloat() }
                    visibility = if (nativeAd.starRating != null) View.VISIBLE else View.INVISIBLE
                }
                bodyView = findViewById<AppCompatTextView>(R.id.tv_ad_body).apply {
                    text = nativeAd.body
                    visibility = if (nativeAd.body != null) View.VISIBLE else View.INVISIBLE
                }
                mediaView = findViewById<MediaView>(R.id.mv_ad_media).apply {
                    nativeAd.mediaContent?.let {
                        mediaContent = it
                        it.videoController.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                            override fun onVideoStart() {
                                super.onVideoStart()
                                // 视频开始
                                Log.i(TAG, "onVideoStart")
                            }

                            override fun onVideoEnd() {
                                super.onVideoEnd()
                                // 视频结束，结束后可以刷新广告
                                Log.i(TAG, "onVideoEnd")
                            }

                            override fun onVideoPlay() {
                                super.onVideoPlay()
                                // 视频播放
                                Log.i(TAG, "onVideoPlay")
                            }

                            override fun onVideoPause() {
                                super.onVideoPause()
                                // 视频暂停
                                Log.i(TAG, "onVideoPause")
                            }

                            override fun onVideoMute(mute: Boolean) {
                                super.onVideoMute(mute)
                                // 视频是否静音
                                // mute true 静音 false 非静音
                                Log.i(TAG, "onVideoMute mute:$mute")
                            }
                        }
                    }
                }
                callToActionView = findViewById<AppCompatButton>(R.id.btn_ad_call_to_action).apply {
                    text = nativeAd.callToAction
                    visibility = if (nativeAd.callToAction != null) View.VISIBLE else View.INVISIBLE
                }
                priceView = findViewById<AppCompatTextView>(R.id.tv_ad_price).apply {
                    text = nativeAd.price
                    visibility = if (nativeAd.price != null) View.VISIBLE else View.INVISIBLE
                }
                storeView = findViewById<AppCompatTextView>(R.id.tv_ad_store).apply {
                    text = nativeAd.store
                    visibility = if (nativeAd.store != null) View.VISIBLE else View.INVISIBLE
                }
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.BOTTOM
                }
                setNativeAd(nativeAd)
                nativeAdView = this
            }
        }
    }

    private fun loadNativeAd() {
        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { nativeAd ->
                // 如果在页面销毁后触发此回调，需要销毁NativeAd避免内存泄漏
                if (isDestroyed || isFinishing || isChangingConfigurations) {
                    nativeAd.destroy()
                    return@forNativeAd
                }
                currentNativeAd?.destroy()
                nativeAd.setMuteThisAdListener(muteListener)
                currentNativeAd = nativeAd
                // 判断是否支持自定义不再显示广告
                if (nativeAd.isCustomMuteThisAdEnabled) {
                    // 获取不再显示广告的原因
                    muteThisAdReason.addAll(nativeAd.muteThisAdReasons)
                }
                binding.btnStopNativeAd.visibility = if (nativeAd.isCustomMuteThisAdEnabled) View.VISIBLE else View.GONE
                populateNativeAdView()
            }
            .withNativeAdOptions(NativeAdOptions.Builder()
                // 设置视频是否静音播放
                .setVideoOptions(VideoOptions.Builder().setStartMuted(false).build())
                // 设置自定义不再显示广告
                .setRequestCustomMuteThisAd(true)
                .build())
            .withAdListener(adListener)
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun showChoseMuteNativeAdDialog() {
        val muteThisAdReasonString = arrayOfNulls<CharSequence>(muteThisAdReason.size)
        for ((index, item) in muteThisAdReason.withIndex()) {
            muteThisAdReasonString[index] = item.description
        }
        AlertDialog.Builder(this)
            .setTitle("关闭此原生广告的原因是？")
            .setItems(muteThisAdReasonString) { _, which ->
                if (muteThisAdReason.size > which) {
                    muteNativeAd(muteThisAdReason[which])
                }
            }
            .create()
            .show()
    }

    private fun muteNativeAd(muteThisAdReason: MuteThisAdReason) {
        // 可以上报用户关闭广告的原因，便于优化广告
        currentNativeAd?.muteThisAd(muteThisAdReason)
        nativeAdView?.destroy()
        currentNativeAd?.destroy()
        binding.flNativeAdContainer.removeAllViews()
    }
    // </editor-folder>

    override fun onDestroy() {
        super.onDestroy()
        hideBanner()
        bannerAdView?.destroy()
        currentNativeAd?.destroy()
    }
}