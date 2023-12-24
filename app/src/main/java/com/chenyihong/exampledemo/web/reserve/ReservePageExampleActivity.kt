package com.chenyihong.exampledemo.web.reserve

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutReservePageExampleActivityBinding
import com.chenyihong.exampledemo.web.PARAMS_LINK_URL

@UnstableApi
class ReservePageExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutReservePageExampleActivityBinding

    private var currentWeb: WebView? = null

    private val webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            binding.pbWebLoadProgress.run {
                post { progress = newProgress }
                if (newProgress >= 100 && visibility == View.VISIBLE) {
                    postDelayed({ visibility = View.GONE }, 500)
                }
            }
        }
    }
    private val webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.pbWebLoadProgress.run { post { visibility = View.VISIBLE } }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutReservePageExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 处理系统返回事件
                handleBackPress()
            }
        })
        intent.getStringExtra(PARAMS_LINK_URL)?.let { websiteUrl ->
            // 切换Context
            WebVIewCacheController.webViewContextWrapperCache.baseContext = this
            // 获取缓存
            val cacheWebsiteUrl = WebVIewCacheController.webViewCache.entries.firstOrNull()?.key
            currentWeb = WebVIewCacheController.webViewCache.entries.firstOrNull()?.value
            if (websiteUrl == cacheWebsiteUrl) {
                // 加载同个网页，使用缓存的WebView
                currentWeb?.let {
                    // 确保控件没有父控件
                    removeViewParent(it)
                    // 添加到页面布局最底层。
                    binding.root.addView(it, 0)
                }
            } else {
                // 加载不同网页，释放旧的WebView并创建新的
                createWebView(websiteUrl)
            }
        }
    }

    private fun createWebView(webSiteUrl: String) {
        releaseWebView(currentWeb)
        WebVIewCacheController.webViewCache.clear()
        currentWeb = WebView(WebVIewCacheController.webViewContextWrapperCache).apply {
            initWebViewSetting(this)
            // 设置背景为黑色，根据自己需求可以忽略
            setBackgroundColor(ContextCompat.getColor(this@ReservePageExampleActivity, R.color.color_black_222))
            layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            // 确保控件没有父控件
            removeViewParent(this)
            // 添加到页面布局最底层。
            binding.root.addView(this, 0)
            loadUrl(webSiteUrl)
            // 缓存WebView
            WebVIewCacheController.webViewCache[webSiteUrl] = this
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSetting(webView: WebView) {
        val settings = webView.settings
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.domStorageEnabled = true
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true

        webView.webChromeClient = webChromeClient
        webView.webViewClient = webViewClient
    }

    private fun handleBackPress() {
        if (currentWeb?.canGoBack() == true) {
            currentWeb?.goBack()
        } else {
            minimize()
        }
    }

    private fun minimize() {
        // 切换Context
        WebVIewCacheController.webViewContextWrapperCache.baseContext = applicationContext
        // 暂时先把WebView移出布局
        currentWeb?.let { binding.root.removeView(it) }
        finish()
    }

    private fun releaseWebView(webView: WebView?) {
        webView?.run {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            clearHistory()
            clearCache(false)
            binding.root.removeView(this)
            destroy()
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