package com.chenyihong.exampledemo.androidapi.desktopwidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chenyihong.exampledemo.databinding.LayoutDesktopWidgetExampleActivityBinding

class DesktopWidgetExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LayoutDesktopWidgetExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.btnAddDesktopWidget.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppWidgetManager.getInstance(this).run {
                    // 先判断是否支持添加小组件
                    if (isRequestPinAppWidgetSupported) {
                        // 小组件信息
                        val widgetProvider = ComponentName(this@DesktopWidgetExampleActivity, DesktopWidgetExampleProvider::class.java)
                        // 添加成功的广播（如果不需要，可以用null）
                        val successCallback = PendingIntent.getBroadcast(this@DesktopWidgetExampleActivity, 0, Intent(this@DesktopWidgetExampleActivity, DesktopWidgetExampleProvider::class.java).apply { action = ACTION_PIN_WIDGET }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                        // 请求添加小组件
                        requestPinAppWidget(widgetProvider, null, successCallback)
                    }
                }
            }
        }
    }
}