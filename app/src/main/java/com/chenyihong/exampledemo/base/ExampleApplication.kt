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
}