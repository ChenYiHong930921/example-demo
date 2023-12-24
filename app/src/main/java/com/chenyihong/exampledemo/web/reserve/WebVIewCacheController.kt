package com.chenyihong.exampledemo.web.reserve

import android.annotation.SuppressLint
import android.content.MutableContextWrapper
import android.util.ArrayMap
import android.webkit.WebView
import androidx.media3.common.util.UnstableApi
import com.chenyihong.exampledemo.base.ExampleApplication

@UnstableApi
@SuppressLint("StaticFieldLeak")
object WebVIewCacheController {

    // 经过实际测试需要如此实现
    val webViewContextWrapperCache = MutableContextWrapper(ExampleApplication.exampleContext)

    // Key为网页链接，Value为WebView
    val webViewCache = ArrayMap<String, WebView?>()
}