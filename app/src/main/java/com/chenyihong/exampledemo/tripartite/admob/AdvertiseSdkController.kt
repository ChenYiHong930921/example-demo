package com.chenyihong.exampledemo.tripartite.admob

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.chenyihong.exampledemo.base.ExampleApplication
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

@SuppressLint("StaticFieldLeak", "UnsafeOptInUsageError")
object AdvertiseSdkController {

    private var tempActivity: Activity? = null

    var advertiseStatusListener: AdvertiseStatusListener? = null

    fun initAdMobSdk(activity: Activity) {
        tempActivity = activity
        MobileAds.initialize(activity.applicationContext) {
            loadInterstitialAd()
            loadRewardedVideoAd()
            createBannerAdView(activity)
        }
    }

    //<editor-folder desc = "interstitial ad">

    private var interstitialAd: InterstitialAd? = null

    // 插屏广告加载状态的回调
    private val interstitialAdLoadCallback = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            super.onAdLoaded(interstitialAd)
            Log.i(TAG, "interstitial onAdLoaded")
            // 加载成功
            this@AdvertiseSdkController.interstitialAd = interstitialAd
            // 设置广告事件回调
            this@AdvertiseSdkController.interstitialAd?.fullScreenContentCallback = interstitialAdCallback
            advertiseStatusListener?.availableChange(ADVERTISE_TYPE_INTERSTITIAL, true)
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            Log.e(TAG, "interstitial onAdFailedToLoad error:${loadAdError.message}")
            // 加载失败
            advertiseStatusListener?.availableChange(ADVERTISE_TYPE_INTERSTITIAL, false)
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
            loadInterstitialAd()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            super.onAdFailedToShowFullScreenContent(adError)
            // 展示失败时调用，此时销毁当前的插屏广告对象，重新加载插屏广告
            Log.e(TAG, "interstitial onAdFailedToShowFullScreenContent error:${adError.message}")
            interstitialAd = null
            loadInterstitialAd()
            advertiseStatusListener?.showFailure("Interstitial Ad show failure due to ${adError.message}")
        }
    }

    private fun loadInterstitialAd() {
        tempActivity?.let { InterstitialAd.load(it.applicationContext, "ca-app-pub-3940256099942544/1033173712", AdRequest.Builder().build(), interstitialAdLoadCallback) }
    }

    fun showInterstitialAd() {
        tempActivity?.let { interstitialAd?.show(it) }
    }
    //</editor-folder>

    //<editor-folder desc = "rewarded video ad">

    private var rewardedAd: RewardedAd? = null

    private val rewardedAdLoadCallback = object : RewardedAdLoadCallback() {
        override fun onAdLoaded(rewardedAd: RewardedAd) {
            super.onAdLoaded(rewardedAd)
            Log.i(TAG, "rewardedVideo onAdLoaded")
            // 加载成功
            this@AdvertiseSdkController.rewardedAd = rewardedAd
            // 设置广告事件回调
            this@AdvertiseSdkController.rewardedAd?.fullScreenContentCallback = rewardedVideoAdCallback
            advertiseStatusListener?.availableChange(ADVERTISE_TYPE_REWARDED, true)
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            // 加载失败
            Log.e(TAG, "rewardedVideo onAdFailedToLoad error:${loadAdError.message}")
            advertiseStatusListener?.availableChange(ADVERTISE_TYPE_REWARDED, false)
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
            loadRewardedVideoAd()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            super.onAdFailedToShowFullScreenContent(adError)
            // 展示失败时调用，此时销毁当前的激励视频广告对象，重新加载激励视频广告
            Log.e(TAG, "rewardedVideo onAdFailedToShowFullScreenContent error:${adError.message}")
            rewardedAd = null
            loadRewardedVideoAd()
            advertiseStatusListener?.showFailure("Rewarded Ad show failure due to ${adError.message}")
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
        tempActivity?.let { RewardedAd.load(it.applicationContext, "ca-app-pub-3940256099942544/5224354917", AdRequest.Builder().build(), rewardedAdLoadCallback) }
    }

    fun showRewardedVideo() {
        tempActivity?.let { rewardedAd?.show(it, rewardedVideoAdEarnedCallback) }
    }
    //</editor-folder>

    // <editor-folder desc = "banner ad">

    private var bannerAdView: AdView? = null

    private val bannerListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            // 广告加载成功
            Log.i(TAG, "banner onAdLoaded")
            advertiseStatusListener?.availableChange(ADVERTISE_TYPE_BANNER, true)
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            // 广告加载失败
            Log.e(TAG, "banner onAdFailedToLoad error:${loadAdError.message}")
            advertiseStatusListener?.availableChange(ADVERTISE_TYPE_BANNER, false)
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

    private fun createBannerAdView(activity: Activity) {
        // 获取页面的根布局
        val rootView = activity.findViewById<FrameLayout>(android.R.id.content)
        bannerAdView = AdView(activity)
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

    fun showBanner() {
        bannerAdView?.alpha = 1f
    }

    fun hideBanner() {
        bannerAdView?.alpha = 0f
    }

    private fun removeViewInParent(targetView: View) {
        try {
            (targetView.parent as? ViewGroup)?.removeView(targetView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseBanner() {
        bannerAdView?.let {
            removeViewInParent(it)
            it.destroy()
        }
        bannerAdView = null
    }
    //</editor-folder>

    fun changeActivity(currentActivity: Activity) {
        if (tempActivity != currentActivity) {
            tempActivity = currentActivity
            releaseBanner()
            createBannerAdView(currentActivity)
        }
    }
}