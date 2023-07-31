package com.chenyihong.exampledemo.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.chenyihong.exampledemo.databinding.LayoutWebViewActivityBinding
import com.chenyihong.exampledemo.entity.PersonEntity
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

const val TAG = "WebsiteTest"
const val PARAMS_LINK_URL = "linkUrl"

class WebViewActivity : BaseGestureDetectorActivity<LayoutWebViewActivityBinding>() {

    private val gson = Gson()

    private var mainWebView: WebView? = null
    private var newWebView: WebView? = null

    /**
     * JavaScript interface
     */
    private val jsInteractive: JsInteractive = object : JsInteractive {

        @JavascriptInterface
        override fun jsCallAndroid() {
            val message = "receive jsCallAndroid"
            showSnakeBar(message)
        }

        @JavascriptInterface
        override fun jsCallAndroidWithParams(params: String) {
            val message = "receive jsCallAndroidWithParams params:$params"
            showSnakeBar(message)
        }

        @JavascriptInterface
        override fun getPersonJsonArray() {
            val personList = ArrayList<PersonEntity>()
            for (index in 0 until 10) {
                personList.add(PersonEntity("测试$index", 20 + index, if (index % 2 == 0) 0 else 1, 160f, 175f))
            }
            invokeJsCallback("javascript:getPersonJsonArrayResult(\'${gson.toJson(personList)}\')")
        }
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutWebViewActivityBinding {
        return LayoutWebViewActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.i(TAG, "mainWebView canGoBack:${mainWebView?.canGoBack()}")
                when {
                    newWebView?.canGoBack() == true -> newWebView?.goBack()
                    newWebView != null -> destroyNewWebView()
                    mainWebView?.canGoBack() == true -> mainWebView?.goBack()
                    else -> finish()
                }
            }
        })
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnAndroidCallJs.setOnClickListener { mainWebView?.loadUrl("javascript:androidCallJsWithParams(true)") }
        val linkUrl = intent?.getStringExtra(PARAMS_LINK_URL) ?: ""
        mainWebView = WebView(this)
        mainWebView?.let {
            it.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            initWebViewSetting(it)
            binding.webViewContainer.addView(it)
            Log.i(TAG, "--------------create main web-----------------")
        }
        if (linkUrl.isNotEmpty() && linkUrl.isNotBlank()) {
            mainWebView?.loadUrl(linkUrl)
        }
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
                    binding.pbWebLoadProgress.run {
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
                        binding.webViewContainer.addView(newWeb)
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
                    binding.pbWebLoadProgress.run {
                        post { visibility = View.VISIBLE }
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.i(TAG, "WebViewActivity onPageFinished view:$view url:$url")
                    val message = "mainWeb:$mainWebView||newWeb:$newWebView||currentWeb:$view"
                    view?.loadUrl("javascript:androidCallJsWithParams(\"$message\")")
                }

                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                    request?.run {
                        val urlStr = url.toString()
                        Log.i(TAG, "WebViewActivity shouldInterceptRequest view:$view urlStr:$urlStr")
                        if (urlStr.contains("minigame") && urlStr.contains("assets")) {
                            val assetsNamespace = "assets/"
                            var localAssetsPath = ""
                            var mineType = ""
                            when {
                                urlStr.contains("test_icon.jpg") -> {
                                    localAssetsPath = urlStr.substring(urlStr.indexOf(assetsNamespace) + assetsNamespace.length)
                                    mineType = "image/jpeg"
                                }

                                urlStr.contains("test_video.mp4") -> {
                                    localAssetsPath = urlStr.substring(urlStr.indexOf(assetsNamespace) + assetsNamespace.length)
                                    mineType = "video/mp4"
                                }
                            }

                            if (localAssetsPath.isNotEmpty() && mineType.isNotEmpty()) {
                                val inputStream = assets.open(localAssetsPath)
                                return WebResourceResponse(mineType, Charsets.UTF_8.toString(), inputStream)
                            }
                        }
                    }
                    return super.shouldInterceptRequest(view, request)
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

    private fun showSnakeBar(message: String) {
        Log.i(TAG, message)
        runOnUiThread {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun invokeJsCallback(script: String) {
        mainWebView?.run { runOnUiThread { loadUrl(script) } }
    }

    private fun destroyMainWebView() {
        mainWebView?.run {
            Log.i(TAG, "destroy mainWeb webView:$this")
            clearHistory()
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            binding.webViewContainer.removeView(this)
            destroy()
        }
        mainWebView = null
    }

    private fun destroyNewWebView() {
        newWebView?.run {
            Log.i(TAG, "destroy newWeb webView:$this")
            clearHistory()
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            binding.webViewContainer.removeView(this)
            destroy()
        }
        newWebView = null
    }
}