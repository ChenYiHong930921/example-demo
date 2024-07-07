package com.chenyihong.exampledemo.androidapi.desktopwidgets

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutDesktopWidgetConfigExampleActivityBinding
import com.chenyihong.exampledemo.utils.SpUtils

class DesktopWidgetConfigExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LayoutDesktopWidgetConfigExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        if (!SpUtils.isInit()) {
            SpUtils.init(this)
        }

        val appWidgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // 先将结果设置为Activity.RESULT_CANCELED，如果没有顺利执行完整个流程，系统会取消添加widget。
        setResult(Activity.RESULT_CANCELED, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))

        binding.btnSave.setOnClickListener {
            val targetWaterCount = binding.etInputTargetWaterCount.text.toString()
            updateWidget(this, AppWidgetManager.getInstance(this), appWidgetId, targetWaterCount)
            setResult(Activity.RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
            finish()
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, targetWaterCount: String) {
        // 可能不是初次创建时打开配置，所以需要使用保存的数据。
        val currentTotalWaterCount = SpUtils.getInt("currentTotalWaterCount", 0)
        appWidgetManager.updateAppWidget(appWidgetId, RemoteViews(context.packageName, R.layout.layout_desktop_widget_example).apply {
            setTextViewText(R.id.tv_target_water_count, "目标饮水量（杯）：$targetWaterCount")
            setTextViewText(R.id.tv_example_content, "当前饮水量（杯）：$currentTotalWaterCount")
            setOnClickPendingIntent(R.id.btn_plus, PendingIntent.getBroadcast(context, 0, Intent(context, DesktopWidgetExampleProvider::class.java).apply { action = ACTION_INCREASE }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            setOnClickPendingIntent(R.id.btn_reduce, PendingIntent.getBroadcast(context, 0, Intent(context, DesktopWidgetExampleProvider::class.java).apply { action = ACTION_DECREASE }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        })
    }
}