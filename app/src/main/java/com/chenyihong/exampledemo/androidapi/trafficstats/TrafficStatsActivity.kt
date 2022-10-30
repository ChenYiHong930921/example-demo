package com.chenyihong.exampledemo.androidapi.trafficstats

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutTrafficStatsActivityBinding

const val TAG = "TrafficStatsTag"

class TrafficStatsActivity : BaseGestureDetectorActivity() {

    private lateinit var binding: LayoutTrafficStatsActivityBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_traffic_stats_activity)
        binding.includeTitle.tvTitle.text = "TrafficStatsExample"
        NetSpeedUtils.netSpeedCallback = object : NetSpeedUtils.NetSpeedCallback {
            override fun onNetSpeedChange(downloadSpeed: String, uploadSpeed: String) {
                binding.tvNetSpeed.run { post { text = "downloadSpeed:$downloadSpeed , uploadSpeed:$uploadSpeed" } }
            }
        }
        binding.btnStartMeasureNetSpeed.setOnClickListener {
            NetSpeedUtils.startMeasuringNetSpeed()
        }
        binding.btnStopMeasureNetSpeed.setOnClickListener {
            NetSpeedUtils.stopMeasuringNetSpeed()
        }
        initWebViewSetting(binding.webView)

        binding.webView.loadUrl("https://go.minigame.vip/")
    }

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    private fun initWebViewSetting(webView: WebView?) {
        webView?.run {
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.domStorageEnabled = true
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(true)

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.i(TAG, "WebViewActivity onPageStarted url:$url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.i(TAG, "WebViewActivity onPageFinished view:$view url:$url")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.clearHistory()
        binding.webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        binding.root.run {
            if (this is ViewGroup) {
                this.removeView(binding.webView)
            }
        }
        binding.webView.destroy()
    }

}