package com.chenyihong.exampledemo.tripartite.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

class AppOpenAdManager {

    private val appOpenAdUnit = "ca-app-pub-3940256099942544/3419835294"
    private val appOpenAdValidTime = 4 * 60 * 60 * 1000

    private var currentAppOpenAd: AppOpenAd? = null
    private var currentAppOpenAdLoadedTime: Long = 0

    private var loadingAd: Boolean = false
    var showingAd: Boolean = false
        private set

    private var showAdWhenReady: Boolean = false
    private var tempActivity: Activity? = null
    private var tempAppOpenAdShowCallback: AppOpenAdShowCallback? = null

    private val appOpenAdLoadCallback = object : AppOpenAdLoadCallback() {
        override fun onAdLoaded(appOpenAd: AppOpenAd) {
            super.onAdLoaded(appOpenAd)
            // 开屏广告加载成功
            Log.i(TAG, "appOpenAd onAdLoaded openWhenReady:$showAdWhenReady")
            currentAppOpenAdLoadedTime = System.currentTimeMillis()
            currentAppOpenAd = appOpenAd
            loadingAd = false
            // 设置了当加载完成时打开广告
            if (showAdWhenReady) {
                showAdWhenReady = false
                tempActivity?.let {
                    showAppOpenAd(it, tempAppOpenAdShowCallback)
                }
            }
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            // 开屏广告加载失败
            // 官方不建议在此回调中重新加载广告
            // 如果确实需要，则必须限制最大重试次数，避免在网络受限的情况下连续多次请求
            Log.e(TAG, "appOpenAd onAdFailedToLoad error:$loadAdError")
            loadingAd = false
        }
    }

    fun loadAd(context: Context) {
        if (!loadingAd) {
            loadingAd = true
            AppOpenAd.load(context, appOpenAdUnit, AdRequest.Builder().build(), appOpenAdLoadCallback)
        }
    }

    fun showAppOpenAd(activity: Activity, appOpenAdShowCallback: AppOpenAdShowCallback? = null, showAdWhenReady: Boolean = false) {
        if (showingAd) {
            // 开屏广告正在展示
            Log.i(TAG, "appOpenAd showing now")
            return
        }
        if (!appOpenAdAvailable()) {
            Log.i(TAG, "appOpenAd unAvailable showAdWhenReady:$showAdWhenReady")
            // 开屏广告不可用，重新加载
            loadAd(activity)
            if (showAdWhenReady) {
                // 设置当加载完成时打开广告，缓存当前页面和回调方法
                this.showAdWhenReady = true
                tempActivity = activity
                tempAppOpenAdShowCallback = appOpenAdShowCallback
            } else {
                // 广告不可用，回调播放完成继续执行后续步骤
                appOpenAdShowCallback?.onAppOpenAdShowComplete()
            }
            return
        }
        currentAppOpenAd?.run {
            fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    // 广告展示
                    Log.i(TAG, "appOpenAd onAdShowedFullScreenContent")
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    // 广告展示失败，清空缓存数据，重新加载
                    Log.e(TAG, "appOpenAd onAdFailedToShowFullScreenContent error:$adError")
                    showingAd = false
                    currentAppOpenAd = null
                    appOpenAdShowCallback?.onAppOpenAdShowComplete()
                    tempActivity = null
                    tempAppOpenAdShowCallback = null
                    loadAd(activity)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    // 广告被点击
                    Log.i(TAG, "appOpenAd onAdClicked")
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    // 广告关闭，清空缓存数据，重新加载
                    Log.i(TAG, "appOpenAd onAdDismissedFullScreenContent")
                    showingAd = false
                    currentAppOpenAd = null
                    appOpenAdShowCallback?.onAppOpenAdShowComplete()
                    tempActivity = null
                    tempAppOpenAdShowCallback = null
                    loadAd(activity)
                }
            }
            showingAd = true
            show(activity)
            appOpenAdShowCallback?.onAppOpenAdShow()
        }
    }

    private fun appOpenAdAvailable(): Boolean {
        return currentAppOpenAd != null && (currentAppOpenAdLoadedTime != 0L && System.currentTimeMillis() - currentAppOpenAdLoadedTime <= appOpenAdValidTime)
    }

    interface AppOpenAdShowCallback {

        fun onAppOpenAdShow()

        fun onAppOpenAdShowComplete()
    }
}