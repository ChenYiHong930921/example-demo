package com.chenyihong.exampledemo.tripartite.admob

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.base.ExampleApplication
import com.chenyihong.exampledemo.home.HomeActivity

class AppOpenAdActivity : AppCompatActivity() {

    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
    private val enterRunnable = Runnable {
        // 停止自动显示，避免进入主页后自动展示广告打断用户行为
        (application as ExampleApplication).appOpenAdManager?.stopAutoShow()
        enterHomePage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_app_open_activity)
        (application as ExampleApplication).appOpenAdManager?.showAppOpenAd(this, object : AppOpenAdManager.AppOpenAdShowCallback {
            override fun onAppOpenAdShow() {
                // 开屏广告已显示，停止计时线程
                handler.removeCallbacks(enterRunnable)
            }

            override fun onAppOpenAdShowComplete() {
                // 开屏广告播放完毕（成功或失败），停止计时线程并进入主页
                handler.removeCallbacks(enterRunnable)
                enterHomePage()
            }
        }, true)
        // 三秒内没有显示出广告，自动进入主页
        handler.postDelayed(enterRunnable, 3000)
    }

    private fun enterHomePage() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}