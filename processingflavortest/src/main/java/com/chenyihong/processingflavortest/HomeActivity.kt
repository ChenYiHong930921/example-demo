package com.chenyihong.processingflavortest

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.chenyihong.processingflavortest.databinding.LayoutWebViewActivityBinding

class HomeActivity : AppCompatActivity() {

    private val openWebsite: String = BuildConfig.open_website

    private var webView: WebView? = null

    private val binding: LayoutWebViewActivityBinding by lazy {
        LayoutWebViewActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowCompat.getInsetsController(window, window.decorView).run {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.statusBars())
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView?.canGoBack() == true) {
                    webView?.goBack()
                } else {
                    moveTaskToBack(true)
                }
            }
        })
        WebView(this).also {
            it.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            initWebViewSetting(it)
            webView = it
            binding.flWebContainer.addView(it)
        }
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        webView?.loadUrl(openWebsite)
    }

    override fun onDestroy() {
        destroyWebView()
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSetting(webView: WebView) {
        webView.settings.run {
            //兼容HTTP与HTTPS
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            //设置是否开启JavaScript
            javaScriptEnabled = true
            //允许js弹出窗口
            javaScriptCanOpenWindowsAutomatically = true

            //设置是否启用DOM存储API
            domStorageEnabled = true

            //设置是否允许访问文件
            allowContentAccess = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true

            cacheMode = WebSettings.LOAD_DEFAULT
            //允许多窗口
            setSupportMultipleWindows(true)
            //设置是否使用ViewPort,设置为false时页面的宽度总是适应WebView的控件宽度
            useWideViewPort = true
            //设置是否使用概览模式加载页面(缩放内容以适应屏幕)
            loadWithOverviewMode = true
        }

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
    }

    private fun destroyWebView() {
        webView?.run {
            clearHistory()
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            binding.flWebContainer.removeView(this)
            destroy()
        }
        webView = null
    }
}