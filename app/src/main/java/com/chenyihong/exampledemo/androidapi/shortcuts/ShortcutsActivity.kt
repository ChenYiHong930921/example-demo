package com.chenyihong.exampledemo.androidapi.shortcuts

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.androidapi.gps.GpsSignalActivity
import com.chenyihong.exampledemo.databinding.LayoutShortcutsActivityBinding
import com.chenyihong.exampledemo.home.HomeActivity

class ShortcutsActivity : BaseGestureDetectorActivity() {

    private val locationShortcutId = "location"

    private var english = true

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutShortcutsActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_shortcuts_activity)
        binding.includeTitle.tvTitle.text = "Shortcuts Api"
        binding.btnCreateShortcut.setOnClickListener {
            // 创建动态快捷方式
            createDynamicShortcuts()
        }
        binding.btnRemoveShortcut.setOnClickListener {
            // 根据ID移除指定的动态快捷方式
            ShortcutManagerCompat.removeDynamicShortcuts(this, arrayListOf(locationShortcutId))
            // 根据ID移除指定的动态快捷方式
            // ShortcutManagerCompat.removeAllDynamicShortcuts(this)
        }
        binding.btnCreatePinShortcut.setOnClickListener {
            // 创建桌面快捷方式
            createPinShortcuts()
        }
        binding.btnCreateMultipleIntentsShortcut.setOnClickListener {
            createMultipleIntentsDynamicShortcuts()
        }
        binding.btnEnableShortcut.setOnClickListener {
            // 启用快捷方式
            ShortcutManagerCompat.enableShortcuts(this, arrayListOf(ShortcutInfoCompat.Builder(this, locationShortcutId)
                .setShortLabel(getString(R.string.location_shortcuts_short_label))
                .setLongLabel(getString(R.string.location_shortcuts_long_label))
                .setDisabledMessage(getString(R.string.shortcuts_disable_message))
                .setIcon(IconCompat.createWithResource(this, R.drawable.icon_location))
                .setIntent(Intent(Intent.ACTION_VIEW).apply {
                    component = ComponentName(packageName, GpsSignalActivity::class.java.name)
                })
                .build()
            ))
        }
        binding.btnDisableShortcut.setOnClickListener {
            // 禁用快捷方式，需要传入禁用原因，当用户点击时会显示
            ShortcutManagerCompat.disableShortcuts(this, arrayListOf(locationShortcutId), "Location function is currently unavailable")
        }
        binding.btnUpdateShortcut.setOnClickListener {
            // 更新快捷方式
            updateDynamicShortcuts()
        }
    }

    private fun createDynamicShortcuts() {
        val dynamicShortcuts = ShortcutManagerCompat.getDynamicShortcuts(this)
        val locationShortcut = ShortcutInfoCompat.Builder(this, locationShortcutId)
            .setShortLabel(getString(R.string.location_shortcuts_short_label))
            .setLongLabel(getString(R.string.location_shortcuts_long_label))
            .setDisabledMessage(getString(R.string.shortcuts_disable_message))
            .setIcon(IconCompat.createWithResource(this, R.drawable.icon_location))
            .setIntent(Intent(Intent.ACTION_VIEW).apply {
                component = ComponentName(packageName, GpsSignalActivity::class.java.name)
            })
            .build()
        if (dynamicShortcuts.isEmpty() || !dynamicShortcuts.contains(locationShortcut)) {
            ShortcutManagerCompat.pushDynamicShortcut(this, locationShortcut)
        }
    }

    private fun createMultipleIntentsDynamicShortcuts() {
        val dynamicShortcuts = ShortcutManagerCompat.getDynamicShortcuts(this)
        val locationShortcut = ShortcutInfoCompat.Builder(this, locationShortcutId)
            .setShortLabel(getString(R.string.location_shortcuts_short_label))
            .setLongLabel(getString(R.string.location_shortcuts_long_label))
            .setDisabledMessage(getString(R.string.shortcuts_disable_message))
            .setIcon(IconCompat.createWithResource(this, R.drawable.icon_location))
            .setIntents(arrayOf(
                Intent(Intent.ACTION_VIEW).apply {
                    component = ComponentName(packageName, HomeActivity::class.java.name)
                },
                Intent(Intent.ACTION_VIEW).apply {
                    component = ComponentName(packageName, GpsSignalActivity::class.java.name)
                }))
            .build()
        if (dynamicShortcuts.isEmpty() || !dynamicShortcuts.contains(locationShortcut)) {
            ShortcutManagerCompat.pushDynamicShortcut(this, locationShortcut)
        }
    }

    private fun updateDynamicShortcuts() {
        english = !english
        ShortcutManagerCompat.updateShortcuts(this, arrayListOf(
            ShortcutInfoCompat.Builder(this, locationShortcutId)
                .setShortLabel(if (english) getString(R.string.location_shortcuts_short_label) else "使用定位")
                .setLongLabel(if (english) getString(R.string.location_shortcuts_long_label) else "通过定位获取位置信息")
                .setDisabledMessage(if (english) getString(R.string.shortcuts_disable_message) else "此快捷方式不可用")
                .setIcon(IconCompat.createWithResource(this, R.drawable.icon_location))
                .setIntent(Intent(Intent.ACTION_VIEW).apply {
                    component = ComponentName(packageName, GpsSignalActivity::class.java.name)
                })
                .build()
        ))
    }

    private fun createPinShortcuts() {
        // 先判断是否支持添加桌面快捷方式
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
            val pinShortcutInfo = ShortcutInfoCompat.Builder(this, locationShortcutId)
                .setShortLabel(getString(R.string.location_shortcuts_short_label))
                .setLongLabel(getString(R.string.location_shortcuts_long_label))
                .setDisabledMessage(getString(R.string.shortcuts_disable_message))
                .setIcon(IconCompat.createWithResource(this, R.drawable.icon_location))
                .setIntent(Intent(Intent.ACTION_VIEW).apply {
                    component = ComponentName(packageName, GpsSignalActivity::class.java.name)
                })
                .build()
            val pinnedShortcutCallbackIntent = ShortcutManagerCompat.createShortcutResultIntent(this, pinShortcutInfo)
            val successCallback = PendingIntent.getBroadcast(this, 0, pinnedShortcutCallbackIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0)
            ShortcutManagerCompat.requestPinShortcut(this, pinShortcutInfo, successCallback.intentSender)
        }
    }
}