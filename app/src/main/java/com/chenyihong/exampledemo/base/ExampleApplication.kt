package com.chenyihong.exampledemo.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class ExampleApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var exampleContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        exampleContext = this
    }

    override fun getPackageName(): String {
        try {
            val stackTrace = Thread.currentThread().stackTrace
            for (item in stackTrace) {
                if ("org.chromium.base.BuildInfo".equals(item.className, true)) {
                    if ("getAll".equals(item.methodName, true)) {
                        return ""
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return super.getPackageName()
    }
}