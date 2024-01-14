package com.chenyihong.exampledemo.tripartite.admob

import android.app.Activity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.chenyihong.exampledemo.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.AdapterStatus

class AdMobController {

    private var bannerAdView: AdView? = null

    private val bannerListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            // 广告加载成功
            bannerAdLoadCallback?.invoke(true)
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            // 广告加载失败
            bannerAdLoadCallback?.invoke(false)
        }

        override fun onAdImpression() {
            super.onAdImpression()
            // 被记录为展示成功时调用
        }

        override fun onAdClicked() {
            super.onAdClicked()
            // 被点击时调用
        }

        override fun onAdOpened() {
            super.onAdOpened()
            // 广告落地页打开时调用
        }

        override fun onAdClosed() {
            super.onAdClosed()
            // 广告落地页关闭时调用
        }
    }

    private var bannerAdLoadCallback: ((succeed: Boolean) -> Unit)? = null

    var enableBannerResident = false

    private var newRootView: ConstraintLayout? = null

    fun initSdk(activity: Activity, bannerAdLoadCallback: ((succeed: Boolean) -> Unit)? = null) {
        this.bannerAdLoadCallback = bannerAdLoadCallback
        MobileAds.initialize(activity) { initializationStatus ->
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
                createBannerAdView(activity)
            }
        }
    }

    private fun createNewRootView(activity: Activity) {
        if (newRootView == null) {
            activity.findViewById<FrameLayout>(android.R.id.content).let { rootView ->
                // 获取主页面布局
                rootView.getChildAt(0).let { mainPageView ->
                    // 设置tag用于获取主页面布局
                    mainPageView.tag = R.id.layout_main_page_container
                    // 把主页布局从原来的根部局中移除
                    rootView.removeView(mainPageView)
                    // 使用ConstraintLayout创建一个新的根部局并添加到原来的根部局中
                    rootView.addView(ConstraintLayout(activity).apply {
                        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                        // 创建一个FrameLayout作为Banner容器
                        addView(FrameLayout(activity).apply {
                            id = R.id.layout_banner_container
                            setBackgroundResource(android.R.color.black)
                        }, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                            // 默认放在页面底部
                            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        })
                        // 把主页布局添加到新的根布局中
                        addView(mainPageView, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0).apply {
                            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                            bottomToTop = R.id.layout_banner_container
                        })
                        newRootView = this
                    }, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                }
            }
        }
    }

    private fun createBannerAdView(activity: Activity) {
        // 获取页面的根布局
        val rootView = activity.findViewById<FrameLayout>(android.R.id.content)
        bannerAdView = AdView(activity).apply {
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
            //加载广告
            loadAd(AdRequest.Builder().build())
        }
    }

    fun showBanner(onTop: Boolean) {
        bannerAdView?.let { bannerAdView ->
            bannerAdView.context?.let {
                if (it is Activity) {
                    it.runOnUiThread {
                        if (enableBannerResident) {
                            createNewRootView(it)
                        }
                        // 先从原有父布局中移除
                        removeViewParent(bannerAdView)
                        if (bannerAdView.alpha == 0f) {
                            // 设置为不透明
                            bannerAdView.alpha = 1f
                        }
                        // 添加到根布局中
                        if (enableBannerResident) {
                            it.findViewById<FrameLayout>(android.R.id.content).let { rootView ->
                                rootView.findViewWithTag<ViewGroup>(R.id.layout_main_page_container)?.run {
                                    updateLayoutParams<ConstraintLayout.LayoutParams> {
                                        if (onTop) {
                                            topToBottom = R.id.layout_banner_container
                                            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                                            topToTop = ConstraintLayout.LayoutParams.UNSET
                                            bottomToTop = ConstraintLayout.LayoutParams.UNSET
                                        } else {
                                            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                                            bottomToTop = R.id.layout_banner_container
                                            topToBottom = ConstraintLayout.LayoutParams.UNSET
                                            bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                                        }
                                    }
                                }
                                rootView.findViewById<ViewGroup>(R.id.layout_banner_container)?.run {
                                    updateLayoutParams<ConstraintLayout.LayoutParams> {
                                        if (onTop) {
                                            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                                            bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                                        } else {
                                            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                                            topToTop = ConstraintLayout.LayoutParams.UNSET
                                        }
                                    }
                                    addView(bannerAdView)
                                }
                            }
                        } else {
                            // 设置显示在顶部还是底部
                            bannerAdView.updateLayoutParams<FrameLayout.LayoutParams> {
                                gravity = (if (onTop) Gravity.TOP else Gravity.BOTTOM) or Gravity.CENTER_HORIZONTAL
                            }
                            it.findViewById<FrameLayout>(android.R.id.content).addView(bannerAdView)
                        }
                    }
                }
            }
        }
    }

    fun hideBanner() {
        bannerAdView?.run {
            // 设置为透明
            alpha = 0f
            // 从父布局中移除
            removeViewParent(this)
        }
    }

    private fun removeViewParent(view: View) {
        try {
            val parent = view.parent
            (parent as? ViewGroup)?.removeView(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}