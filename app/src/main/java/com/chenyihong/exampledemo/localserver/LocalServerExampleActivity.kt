package com.chenyihong.exampledemo.localserver

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import com.chenyihong.exampledemo.databinding.LayoutLocalServerExampleActivityBinding
import com.chenyihong.exampledemo.localserver.nanohttpd.NanoHttpdServer
import com.chenyihong.exampledemo.web.JsInteractive
import com.chenyihong.exampledemo.web.TAG
import com.google.android.material.snackbar.Snackbar
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

@UnstableApi
class LocalServerExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutLocalServerExampleActivityBinding

    private var mainWebView: WebView? = null

    private var nanoHttpdServer: NanoHttpdServer? = null

    private var andServer: Server? = null

    private val jsInteractive: JsInteractive = object : JsInteractive {

        @JavascriptInterface
        override fun jsCallAndroid() {
        }

        @JavascriptInterface
        override fun jsCallAndroidWithParams(params: String) {
            val message = "receive jsCallAndroidWithParams params:$params"
            showSnakeBar(message)
        }

        @JavascriptInterface
        override fun getPersonJsonArray() {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutLocalServerExampleActivityBinding.inflate(layoutInflater).also { setContentView(it.root) }
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())

        mainWebView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            initWebViewSetting(this)
            binding.webViewContainer.addView(this)
        }

        copyTestHtmlToStorage()

        binding.btnOpenNanohttpd.setOnClickListener {
            (nanoHttpdServer ?: NanoHttpdServer(this)).let {
                nanoHttpdServer = it
                if (!it.isAlive) {
                    it.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true)
                }
            }
        }
        binding.btnCloseNanohttpd.setOnClickListener {
            nanoHttpdServer?.run {
                if (isAlive) {
                    closeAllConnections()
                    mainWebView?.run {
                        clearHistory()
                        loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
                    }
                }
            }
        }
        binding.btnOpenAndserver.setOnClickListener {
            (andServer ?: AndServer.webServer(this).port(8080).timeout(10, TimeUnit.SECONDS).build()).let {
                andServer = it
                if (!it.isRunning) {
                    it.startup()
                }
            }
        }
        binding.btnCloseAndserver.setOnClickListener {
            andServer?.run {
                if (isRunning) {
                    shutdown()
                    mainWebView?.run {
                        clearHistory()
                        loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
                    }
                }
            }
        }
        binding.btnOpenAssetsWebsite.setOnClickListener {
            when {
                nanoHttpdServer?.isAlive == true -> {
                    nanoHttpdServer?.openFromStorage = false
                    mainWebView?.loadUrl("http://localhost:8080/assetsweb/local_server_example_index.html")
                }

                andServer?.isRunning == true -> mainWebView?.loadUrl("http://localhost:8080/local_server_example_index.html")
            }
        }
        binding.btnOpenStorageWebsite.setOnClickListener {
            when {
                nanoHttpdServer?.isAlive == true -> {
                    nanoHttpdServer?.openFromStorage = true
                    mainWebView?.loadUrl("http://localhost:8080/storageweb/local_server_example_index.html")
                }

                andServer?.isRunning == true -> mainWebView?.loadUrl("http://localhost:8080/local_server_example_index.html")
            }
        }
    }

    override fun onDestroy() {
        destroyWebView(mainWebView)
        nanoHttpdServer?.stop()
        nanoHttpdServer = null
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
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
                        Log.i("consolelog", "h5log —— ${message()}")
                    }
                    return super.onConsoleMessage(consoleMessage)
                }

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
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.pbWebLoadProgress.run {
                        post { visibility = View.VISIBLE }
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.loadUrl("javascript:androidCallJsWithParams(\"${"message from LocalServerExampleActivity"}\")")
                }

                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                    return super.shouldInterceptRequest(view, request)
                }
            }

            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    private fun showSnakeBar(message: String) {
        Log.i(TAG, message)
        runOnUiThread {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun destroyWebView(webView: WebView?) {
        webView?.run {
            clearHistory()
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            binding.webViewContainer.removeView(this)
            destroy()
        }
    }

    private fun copyTestHtmlToStorage() {
        val testHtmlParentDir = File(if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), packageName)
        } else {
            File(filesDir, packageName)
        }, "storageweb")
        if (!testHtmlParentDir.exists()) {
            testHtmlParentDir.mkdirs()
        }
        val testHtmlFile = File(testHtmlParentDir, "local_server_example_index.html")
        if (!testHtmlFile.exists()) {
            val inputStream = assets.open("assetsweb/local_server_example_index.html")
            val fileOutputStream = FileOutputStream(testHtmlFile)
            val buffer = ByteArray(1024)
            try {
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    fileOutputStream.write(buffer, 0, length)
                }
                inputStream.close()
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val testIconFile = File(testHtmlParentDir, "test_icon.jpg")
        if (!testIconFile.exists()) {
            val inputStream = assets.open("assetsweb/test_icon.jpg")
            val fileOutputStream = FileOutputStream(testIconFile)
            val buffer = ByteArray(1024)
            try {
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    fileOutputStream.write(buffer, 0, length)
                }
                inputStream.close()
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val testVideoFile = File(testHtmlParentDir, "test_video.mp4")
        if (!testVideoFile.exists()) {
            val inputStream = assets.open("assetsweb/test_video.mp4")
            val fileOutputStream = FileOutputStream(testVideoFile)
            val buffer = ByteArray(1024)
            try {
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    fileOutputStream.write(buffer, 0, length)
                }
                inputStream.close()
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}