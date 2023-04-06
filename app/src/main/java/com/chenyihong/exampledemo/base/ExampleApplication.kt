package com.chenyihong.exampledemo.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import com.chenyihong.exampledemo.tripartite.admob.AppOpenAdManager
import com.chenyihong.exampledemo.tripartite.admob.TAG
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.AdapterStatus

class ExampleApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var exampleContext: Context? = null
    }

    var appOpenAdManager: AppOpenAdManager? = null
    private var currentActivity: Activity? = null
    private var lastShowOpenAdTime: Long = 0
    private var interval = 4 * 60 * 60 * 1000

    override fun onCreate() {
        super.onCreate()
        exampleContext = this
        appOpenAdManager = AppOpenAdManager()
        MobileAds.initialize(this) { initializationStatus ->
            val readyAdapter = initializationStatus.adapterStatusMap.entries.find {
                it.value.initializationState == AdapterStatus.State.READY
            }
            if (readyAdapter != null) {
                appOpenAdManager?.loadAd(applicationContext)
            }
        }
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                Log.i(TAG, "Application onActivityStarted")
                // 开屏广告不展示的情况下，记录当前的Activity
                if (appOpenAdManager?.showingAd != true) {
                    currentActivity = activity
                }
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_START) {
                    // 应用进入前台，显示开屏广告
                    currentActivity?.let {
                        // 判断上次显示广告与本次现实广告的时间间隔，避免两次广告之间间隔太短
                        if (lastShowOpenAdTime == 0L || System.currentTimeMillis() - lastShowOpenAdTime > interval) {
                            appOpenAdManager?.showAppOpenAd(it, object : AppOpenAdManager.AppOpenAdShowCallback {
                                override fun onAppOpenAdShow() {
                                    lastShowOpenAdTime = System.currentTimeMillis()
                                }

                                override fun onAppOpenAdShowComplete() {}
                            })
                        }
                    }
                }
            }
        })
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