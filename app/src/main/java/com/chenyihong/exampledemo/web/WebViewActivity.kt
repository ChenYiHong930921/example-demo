package com.chenyihong.exampledemo.web

import android.app.Activity
import androidx.constraintlayout.widget.ConstraintLayout
import android.webkit.WebView
import androidx.core.widget.ContentLoadingProgressBar
import android.webkit.JavascriptInterface
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import com.chenyihong.exampledemo.R
import androidx.appcompat.widget.AppCompatButton
import android.webkit.WebSettings
import android.webkit.WebChromeClient
import android.webkit.ConsoleMessage
import android.webkit.WebViewClient
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView

const val TAG = "WebsiteTest"

class WebViewActivity : Activity() {

    private var rootView: ConstraintLayout? = null

    private var webView: WebView? = null

    private var progressBar: ContentLoadingProgressBar? = null

    /**
     * JavaScript interface
     */
    private val jsInteractive: JsInteractive = object : JsInteractive {

        @JavascriptInterface
        override fun jsCallAndroid() {
            Log.i(TAG, "WebViewActivity jsCallAndroid")
        }

        @JavascriptInterface
        override fun jsCallAndroidWithParams(params: String) {
            Log.i(TAG, "WebViewActivity jsCallAndroidWithParams params:$params")
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        rootView = layoutInflater.inflate(R.layout.layout_web_view_activity, null) as ConstraintLayout
        setContentView(rootView)

        progressBar = findViewById(R.id.pb_web_load_progress)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }

        webView = findViewById(R.id.web_view)
        initWebViewSetting(webView)

        findViewById<AppCompatButton>(R.id.btn_android_call_js).setOnClickListener { webView?.loadUrl("javascript:androidCallJsWithParams(true)") }

        webView?.loadUrl("file:///android_asset/index.html")
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

            addJavascriptInterface(jsInteractive, "JsInteractive")
            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    consoleMessage?.run {
                        Log.i("WebSiteConsole", message())
                    }
                    return super.onConsoleMessage(consoleMessage)
                }

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    Log.i(TAG, "web load progress:$newProgress")
                    progressBar?.run {
                        post { progress = newProgress }
                        if (newProgress >= 100 && visibility == View.VISIBLE) {
                            postDelayed({ visibility = View.GONE }, 500)
                        }
                    }
                }
            }
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.i(TAG, "WebViewActivity onPageStarted url:$url")
                    progressBar?.run {
                        post { visibility = View.VISIBLE }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        webView?.run {
            onResume()
            resumeTimers()
        }
    }

    override fun onPause() {
        super.onPause()
        webView?.run {
            onPause()
            pauseTimers()
        }
    }

    override fun onDestroy() {
        webView?.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        webView?.clearHistory()
        rootView?.removeView(webView)
        webView?.destroy()
        webView = null
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            super.onBackPressed()
        }
    }
}