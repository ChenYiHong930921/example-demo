package com.chenyihong.exampledemo.androidapi.desktopwidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.utils.SpUtils

const val ACTION_INCREASE = "ACTION_INCREASE"

const val ACTION_DECREASE = "ACTION_DECREASE"

const val ACTION_PIN_WIDGET = "ACTION_PIN_WIDGET"

class DesktopWidgetExampleProvider : AppWidgetProvider() {

    private val requestCode = this.hashCode()

    private var appWidgetManager: AppWidgetManager? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        // 接收到广播之后回调此方法。
        context?.also { usableContext ->
            if (!SpUtils.isInit()) {
                SpUtils.init(usableContext)
            }
            var changed = false
            // 实测全局变量在每次接受广播时都会重置。
            // 简单通过SharedPreference从本地存取，确保可以使用最新的数据。
            var currentTotalWaterCount = SpUtils.getInt("currentTotalWaterCount", 0)
            when (intent?.action) {
                ACTION_INCREASE -> {
                    changed = true
                    currentTotalWaterCount++
                }

                ACTION_DECREASE -> {
                    if (currentTotalWaterCount > 0) {
                        changed = true
                        currentTotalWaterCount--
                    }
                }

                ACTION_PIN_WIDGET -> {}
            }
            if (changed) {
                SpUtils.put("currentTotalWaterCount", currentTotalWaterCount)
                getWidgetManager(usableContext).run {
                    updateWidget(usableContext, this, getAppWidgetIds(ComponentName(usableContext, DesktopWidgetExampleProvider::class.java)))
                }
            }
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        // 首次创建小组件时回调此方法，适合在此执行初始化代码。
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        // 首次添加桌面组件以及调整组件大小时回调，可以根据用户调整的大小来控制显示的内容。
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // 以AppWidgetProviderInfo中设置的updatePeriodMillis为间隔，周期性的回调此方法。
        // 用户添加小组件时也会回调此方法。
        context?.let { usableContext ->
            appWidgetManager?.let { usableAppWidgetManager ->
                updateWidget(usableContext, usableAppWidgetManager, appWidgetIds)
            }
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        // 当小组件被移除时回调此方法。
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        // 当小组件的所有实例都被移除时回调此方法，适合执行释放缓存代码。
    }

    private fun getWidgetManager(context: Context?): AppWidgetManager {
        return appWidgetManager ?: AppWidgetManager.getInstance(context).also {
            appWidgetManager = it
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray?) {
        val currentTotalWaterCount = SpUtils.getInt("currentTotalWaterCount", 0)
        appWidgetManager.updateAppWidget(appWidgetIds, RemoteViews(context.packageName, R.layout.layout_desktop_widget_example).apply {
            setTextViewText(R.id.tv_example_content, "当前饮水量（杯）：$currentTotalWaterCount")
            setOnClickPendingIntent(R.id.btn_plus, PendingIntent.getBroadcast(context, requestCode, Intent(context, DesktopWidgetExampleProvider::class.java).apply { action = ACTION_INCREASE }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            setOnClickPendingIntent(R.id.btn_reduce, PendingIntent.getBroadcast(context, requestCode, Intent(context, DesktopWidgetExampleProvider::class.java).apply { action = ACTION_DECREASE }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        })
    }
}