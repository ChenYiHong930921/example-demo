package com.chenyihong.exampledemo.tripartite.admob.nativeadinlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutNativeAdInListExampleActivityBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NativeAdInListExampleActivity : AppCompatActivity() {

    private lateinit var layoutNativeAdInListExampleActivityBinding: LayoutNativeAdInListExampleActivityBinding

    private val exampleDataList = ArrayList<ExampleEntity>()

    private val nativeAdLoader by lazy {
        // 使用广告测试广告位id
        AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { nativeAd ->
                // 如果在页面销毁后触发此回调，需要销毁NativeAd避免内存泄漏
                if (isDestroyed || isFinishing || isChangingConfigurations) {
                    nativeAd.destroy()
                    return@forNativeAd
                }
                populateNativeAdView(nativeAd)
            }
            .withNativeAdOptions(NativeAdOptions.Builder()
                // 设置视频是否静音播放
                .setVideoOptions(VideoOptions.Builder().setStartMuted(false).build())
                .build())
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    // 广告加载成功
                    nativeAdIndex.getOrNull(nativeAdViewList.lastIndex)?.let {
                        lifecycleScope.launch(Dispatchers.Main) {
                            nativeAdExampleAdapter.notifyItemChanged(it)
                        }
                    }
                    // 需要的原生广告数量小于广告item数量，继续加载广告
                    if (nativeAdViewList.size < nativeAdIndex.size) {
                        loadNativeAd()
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    // 广告加载失败
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    // 广告页打开
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    // 广告被点击
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    // 广告页关闭
                }
            })
            .build()
    }

    private val nativeAdIndex = ArrayList<Int>()

    private val nativeAdList = ArrayList<NativeAd>()
    private val nativeAdViewList = ArrayList<NativeAdView>()

    private val nativeAdExampleAdapter = NativeAdExampleAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutNativeAdInListExampleActivityBinding = LayoutNativeAdInListExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            it.rvExampleDataContainer.adapter = nativeAdExampleAdapter
        }

        lifecycleScope.launch(Dispatchers.IO) {
            for (index in 1..30) {
                val layoutType: Int
                val content: String
                if (index % 4 == 0) {
                    layoutType = LAYOUT_TYPE_AD
                    content = ""
                    // 记录广告元素在整体集合中的位置
                    // 广告加载完成后根据此进行刷新，可以减少不必要的布局重绘。
                    nativeAdIndex.add(index - 1)
                } else {
                    layoutType = LAYOUT_TYPE_NORMAL
                    content = "example data $index"
                }
                exampleDataList.add(ExampleEntity(layoutType, content))
            }

            withContext(Dispatchers.Main) {
                nativeAdExampleAdapter.setNewData(exampleDataList)
            }

            // 初始化SDK并加载原生广告
            MobileAds.initialize(this@NativeAdInListExampleActivity) { loadNativeAd() }
        }
    }

    private fun loadNativeAd() {
        lifecycleScope.launch(Dispatchers.IO) { nativeAdLoader.loadAd(AdRequest.Builder().build()) }
    }

    @SuppressLint("InflateParams")
    private fun populateNativeAdView(nativeAd: NativeAd) {
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
                        }

                        override fun onVideoEnd() {
                            super.onVideoEnd()
                            // 视频结束，结束后可以刷新广告
                        }

                        override fun onVideoPlay() {
                            super.onVideoPlay()
                            // 视频播放
                        }

                        override fun onVideoPause() {
                            super.onVideoPause()
                            // 视频暂停
                        }

                        override fun onVideoMute(mute: Boolean) {
                            super.onVideoMute(mute)
                            // 视频是否静音
                            // mute true 静音 false 非静音
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
            nativeAdList.add(nativeAd)
            nativeAdViewList.add(this)
        }
    }

    fun getNativeAdView(layoutIndex: Int): NativeAdView? {
        return nativeAdViewList.getOrNull(nativeAdIndex.indexOf(layoutIndex))?.also { removeInParent(it) }
    }

    private fun removeInParent(view: View) {
        try {
            (view.parent as? ViewGroup)?.removeView(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseNativeAd() {
        nativeAdIndex.clear()
        for (nativeAd in nativeAdList) {
            nativeAd.destroy()
        }
        nativeAdList.clear()
        for (nativeAdView in nativeAdViewList) {
            removeInParent(nativeAdView)
            nativeAdView.destroy()
        }
        nativeAdViewList.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        exampleDataList.clear()
        nativeAdExampleAdapter.release()
        releaseNativeAd()
    }
}