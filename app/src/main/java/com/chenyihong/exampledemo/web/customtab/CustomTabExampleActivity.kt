package com.chenyihong.exampledemo.web.customtab

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutCustomTabActivityBinding
import com.chenyihong.exampledemo.utils.DensityUtil
import com.chenyihong.exampledemo.web.PARAMS_LINK_URL
import com.chenyihong.exampledemo.web.WebViewActivity

const val TAG = "CustomTabExampleTag"

class CustomTabExampleActivity : BaseGestureDetectorActivity() {

    private val ACTION_ID = "actionId"
    private val ACTION_ID_SCAN = 1
    private val ACTION_ID_COPY_LINK = 2
    private val ACTION_ID_SEARCH = 3

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
        binding.btnOpenSimpleTab.setOnClickListener {
            checkCustomTabAvailable()
            CustomTabHelper.openSimpleCustomTab(this, url)
        }
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
            var backIcon: Bitmap? = null
            ContextCompat.getDrawable(this, R.drawable.icon_back)?.let { backIcon = toBitmap(it) }
            CustomTabHelper.openCustomTabWithCustomUI(this, url, ContextCompat.getColor(this, R.color.color_FF2600), showTitle = true, autoHide = true, CustomTabsIntent.CLOSE_BUTTON_POSITION_END, backIcon)
        }
        binding.btnCustomAnimations.setOnClickListener {
            checkCustomTabAvailable()
            CustomTabHelper.openCustomTabWithCustomAnimations(this, url)
        }
        binding.btnCustomActionButton.setOnClickListener {
            checkCustomTabAvailable()
            ContextCompat.getDrawable(this, R.drawable.icon_scan_32_black)?.let {
                CustomTabHelper.openCustomTabWithCustomActionButton(this, url, toBitmap(it), "Scan", createPendingIntent(ACTION_ID_SCAN))
            }
        }
        binding.btnCustomMenu.setOnClickListener {
            checkCustomTabAvailable()
            CustomTabHelper.openCustomTabWithCustomMenu(this, url, mapOf(
                Pair("Copy Link", createPendingIntent(ACTION_ID_COPY_LINK)),
                Pair("Search In App", createPendingIntent(ACTION_ID_SEARCH))
            ))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.run {
            when (getIntExtra(ACTION_ID, -1)) {
                ACTION_ID_SCAN -> showToast("click scan action button")
                ACTION_ID_COPY_LINK -> showToast("click copy link menu item")
                ACTION_ID_SEARCH -> showToast("click search menu item")
                else -> showToast("Unknown action id")
            }
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

    private fun toBitmap(drawable: Drawable): Bitmap {
        val width = DensityUtil.dp2Px(24)
        val height = DensityUtil.dp2Px(24)
        val oldBounds = Rect(drawable.bounds)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(Canvas(bitmap))
        drawable.bounds = oldBounds
        return bitmap
    }

    private fun createPendingIntent(actionId: Int): PendingIntent {
        val actionIntent = Intent(applicationContext, CustomTabExampleActivity::class.java).apply {
            putExtra(ACTION_ID, actionId)
        }
        return PendingIntent.getActivity(applicationContext, this.hashCode() + actionId, actionIntent, PendingIntent.FLAG_IMMUTABLE)
    }
}