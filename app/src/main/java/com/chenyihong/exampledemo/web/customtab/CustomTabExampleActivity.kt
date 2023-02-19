package com.chenyihong.exampledemo.web.customtab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutCustomTabActivityBinding
import com.chenyihong.exampledemo.web.PARAMS_LINK_URL
import com.chenyihong.exampledemo.web.WebViewActivity

class CustomTabExampleActivity : BaseGestureDetectorActivity() {

    private var changeHeightByActivityResult = false

    private val url = "https://go.minigame.vip/"
    private var activityHeight = 0
    private var topRadius = 16
    private var changeHeightAdjustable = false

    private val customTabLauncher = registerForActivityResult(object : ActivityResultContract<String, Int>() {
        override fun createIntent(context: Context, input: String): Intent {
            val customTabsIntentBuilder = CustomTabsIntent.Builder()
                .setInitialActivityHeightPx(activityHeight, if (changeHeightAdjustable) CustomTabsIntent.ACTIVITY_HEIGHT_ADJUSTABLE else CustomTabsIntent.ACTIVITY_HEIGHT_FIXED)
                .setToolbarCornerRadiusDp(topRadius)
            return customTabsIntentBuilder.build().intent.apply {
                data = input.toUri()
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Int {
            return resultCode
        }
    }) {
        // 页面返回回调
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutCustomTabActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_custom_tab_activity)
        binding.includeTitle.tvTitle.text = "Chrome Custom Tab"
        activityHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()
        binding.btnChangeHeightFixed.setOnClickListener {
            checkCustomTabAvailable()
            changeHeightAdjustable = false
            if (changeHeightByActivityResult) {
                customTabLauncher.launch(url)
            } else {
                CustomTabHelper.openCustomTabWithInitialHeight(this, url, activityHeight, topRadius, changeHeightAdjustable)
            }
        }
        binding.btnChangeHeightAdjustable.setOnClickListener {
            checkCustomTabAvailable()
            changeHeightAdjustable = true
            if (changeHeightByActivityResult) {
                customTabLauncher.launch(url)
            } else {
                CustomTabHelper.openCustomTabWithInitialHeight(this, url, activityHeight, topRadius, changeHeightAdjustable)
            }
        }
        binding.btnCustomUi.setOnClickListener {
            checkCustomTabAvailable()
            CustomTabHelper.openCustomTabWithCustomUI(this, url, ContextCompat.getColor(this, R.color.color_FF2600), showTitle = true, autoHide = true, CustomTabsIntent.CLOSE_BUTTON_POSITION_END)
        }
        binding.btnCustomAnimations.setOnClickListener {
            checkCustomTabAvailable()
            CustomTabHelper.openCustomTabWithCustomAnimations(this, url)
        }
    }

    private fun checkCustomTabAvailable() {
        if (!CustomTabHelper.checkCustomTabAvailable(this)) {
            startActivity(Intent(this, WebViewActivity::class.java).apply { putExtra(PARAMS_LINK_URL, url) })
            return
        }
    }

    override fun onStart() {
        super.onStart()
        if (!changeHeightByActivityResult) {
            CustomTabHelper.bindCustomTabsService(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!changeHeightByActivityResult) {
            CustomTabHelper.unbindCustomTabsService(this)
        }
    }
}