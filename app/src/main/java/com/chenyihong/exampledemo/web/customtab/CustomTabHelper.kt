package com.chenyihong.exampledemo.web.customtab

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.net.toUri
import com.chenyihong.exampledemo.R

object CustomTabHelper {

    private var customTabAvailablePackageName: String = ""
    private var customTabsClient: CustomTabsClient? = null
    private var customTabsServiceConnection: CustomTabsServiceConnection? = null

    fun openCustomTabWithInitialHeight(context: Context, url: String, activityHeight: Int = 0, radius: Int = 0, adjustable: Boolean = false) {
        val customTabsIntentBuilder = CustomTabsIntent.Builder(customTabsClient?.newSession(null))
        if (activityHeight != 0) {
            // 第二个参数配置预期行为
            // ACTIVITY_HEIGHT_ADJUSTABLE 用户可以手动调整视图高度
            // ACTIVITY_HEIGHT_FIXED 用户无法手动调整视图高度
            customTabsIntentBuilder.setInitialActivityHeightPx(activityHeight, if (adjustable) CustomTabsIntent.ACTIVITY_HEIGHT_ADJUSTABLE else CustomTabsIntent.ACTIVITY_HEIGHT_FIXED)
            if (radius != 0) {
                customTabsIntentBuilder.setToolbarCornerRadiusDp(radius)
            }
        }
        customTabsIntentBuilder.build().launchUrl(context, url.toUri())
    }

    fun openCustomTabWithCustomUI(context: Context, url: String, @ColorInt color: Int = 0, showTitle: Boolean = false, autoHide: Boolean = false, backIconPosition: Int = CustomTabsIntent.CLOSE_BUTTON_POSITION_START, backIcon: Bitmap? = null) {
        val customTabsIntentBuilder = CustomTabsIntent.Builder(customTabsClient?.newSession(null))
        if (color != 0) {
            // 配置背景颜色
            customTabsIntentBuilder.setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
                .setToolbarColor(color)
                .build())
        }
        // 是否显示标题
        customTabsIntentBuilder.setShowTitle(showTitle)
        // 地址栏是否自动隐藏 ，此配置仅在Custom Tab全屏显示时生效
        customTabsIntentBuilder.setUrlBarHidingEnabled(autoHide)
        // 调整关闭按钮的位置
        // CustomTabsIntent.CLOSE_BUTTON_POSITION_START 在地址栏的左侧
        // CustomTabsIntent.CLOSE_BUTTON_POSITION_END 在地址栏的右侧
        customTabsIntentBuilder.setCloseButtonPosition(backIconPosition)
        // 调整返回按钮图标
        backIcon?.let { customTabsIntentBuilder.setCloseButtonIcon(it) }
        customTabsIntentBuilder.build().launchUrl(context, url.toUri())
    }

    fun openCustomTabWithCustomAnimations(context: Context, url: String) {
        val customTabsIntentBuilder = CustomTabsIntent.Builder(customTabsClient?.newSession(null))
        customTabsIntentBuilder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
        customTabsIntentBuilder.setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        customTabsIntentBuilder.build().launchUrl(context, url.toUri())
    }

    fun checkCustomTabAvailable(context: Context): Boolean {
        val packageManager = context.packageManager
        val browsableIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.fromParts("http", "", null)
        }
        // 获取所有浏览器
        val browsableResolverInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(browsableIntent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            packageManager.queryIntentActivities(browsableIntent, 0)
        }
        val supportingCustomTabResolveInfo = ArrayList<ResolveInfo>()
        browsableResolverInfo.forEach {
            val serviceIntent = Intent().apply {
                action = androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
                setPackage(it.activityInfo.packageName)
            }
            val customTabServiceResolverInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.resolveService(serviceIntent, PackageManager.ResolveInfoFlags.of(0))
            } else {
                packageManager.resolveService(serviceIntent, 0)
            }
            // 判断是否可以处理Custom Tabs service
            if (customTabServiceResolverInfo != null) {
                supportingCustomTabResolveInfo.add(it)
            }
        }
        if (supportingCustomTabResolveInfo.isNotEmpty()) {
            customTabAvailablePackageName = supportingCustomTabResolveInfo[0].activityInfo.packageName
        }
        return supportingCustomTabResolveInfo.isNotEmpty()
    }

    fun bindCustomTabsService(activity: Activity) {
        if (checkCustomTabAvailable(activity)) {
            if (customTabsClient == null) {
                customTabsServiceConnection = object : CustomTabsServiceConnection() {
                    override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                        customTabsClient = client
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {
                        customTabsClient = null
                    }
                }
                customTabsServiceConnection?.let {
                    CustomTabsClient.bindCustomTabsService(activity, customTabAvailablePackageName, it)
                }
            }
        }
    }

    fun unbindCustomTabsService(activity: Activity) {
        customTabsServiceConnection?.let { activity.unbindService(it) }
        customTabsClient = null
        customTabsServiceConnection = null
    }
}