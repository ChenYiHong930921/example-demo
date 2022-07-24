package com.chenyihong.exampledemo.web

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutWebViewActivityBinding

const val TAG = "WebsiteTest"

class WebViewActivity : Activity() {

    private lateinit var layoutWebViewActivityBinding: LayoutWebViewActivityBinding

    private var mainWebView: WebView? = null
    private var newWebView: WebView? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutWebViewActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_web_view_activity)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        layoutWebViewActivityBinding.ivBack.setOnClickListener { onBackPressed() }
        layoutWebViewActivityBinding.btnAndroidCallJs.setOnClickListener { mainWebView?.loadUrl("javascript:androidCallJsWithParams(true)") }
        mainWebView = WebView(this)
        mainWebView?.let {
            it.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            initWebViewSetting(it)
            layoutWebViewActivityBinding.webViewContainer.addView(it)
            Log.i(TAG, "--------------create main web-----------------")
        }
        mainWebView?.loadUrl("file:///android_asset/index_open_tab.html")
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
                    layoutWebViewActivityBinding.pbWebLoadProgress.run {
                        post { progress = newProgress }
                        if (newProgress >= 100 && visibility == View.VISIBLE) {
                            postDelayed({ visibility = View.GONE }, 500)
                        }
                    }
                }

                override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                    newWebView = WebView(this@WebViewActivity)
                    newWebView?.let { newWeb ->
                        newWeb.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                        initWebViewSetting(newWeb)
                        layoutWebViewActivityBinding.webViewContainer.addView(newWeb)
                        Log.i(TAG, "--------------create new web-----------------")
                        resultMsg?.run {
                            val webViewTransport = obj as? WebView.WebViewTransport
                            webViewTransport?.let { transport ->
                                transport.webView = newWeb
                            }
                            sendToTarget()
                        }
                    }
                    return true
                }

                override fun onCloseWindow(window: WebView?) {
                    destroyNewWebView()
                    super.onCloseWindow(window)
                }
            }
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.i(TAG, "WebViewActivity onPageStarted url:$url")
                    layoutWebViewActivityBinding.pbWebLoadProgress.run {
                        post { visibility = View.VISIBLE }
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.i(TAG, "WebViewActivity onPageFinished view:$view url:$url")
                    val message="mainWeb:$mainWebView||newWeb:$newWebView||currentWeb:$view"
                    view?.loadUrl("javascript:androidCallJsWithParams(\"$message\")")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainWebView?.run {
            onResume()
            resumeTimers()
        }
    }

    override fun onPause() {
        super.onPause()
        mainWebView?.run {
            onPause()
            pauseTimers()
        }
    }

    override fun onDestroy() {
        destroyMainWebView()
        destroyNewWebView()
        super.onDestroy()
    }

    override fun onBackPressed() {
        when {
            newWebView?.canGoBack() == true -> newWebView?.goBack()
            newWebView != null -> destroyNewWebView()
            mainWebView?.canGoBack() == true -> mainWebView?.goBack()
            else -> super.onBackPressed()
        }
    }

    private fun destroyMainWebView() {
        mainWebView?.run {
            Log.i(TAG, "destroy mainWeb webView:$this")
            clearHistory()
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            layoutWebViewActivityBinding.webViewContainer.removeView(this)
            destroy()
        }
        mainWebView = null
    }

    private fun destroyNewWebView() {
        newWebView?.run {
            Log.i(TAG, "destroy newWeb webView:$this")
            clearHistory()
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            layoutWebViewActivityBinding.webViewContainer.removeView(this)
            destroy()
        }
        newWebView = null
    }
}