package com.chenyihong.exampledemo.androidapi.targetsdk

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutTargetSdk14AdapterExampleActivityBinding

@SuppressLint("SetTextI18n")
class TargetSdk14AdapterExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutTargetSdk14AdapterExampleActivityBinding

    private lateinit var alarmManager: AlarmManager

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    // 仍然没有授权，考虑使用别的方法
                    binding.tvTextContent.text = "SCHEDULE_EXACT_ALARM permission still no granted"
                } else {
                    // 设置精准闹钟
                    binding.tvTextContent.text = "SCHEDULE_EXACT_ALARM permission granted"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutTargetSdk14AdapterExampleActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.includeTitle.tvTitle.text = "Adapt Android 14"
        alarmManager = getSystemService(AlarmManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // 没有权限，申请用户授权
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        } else {
            // 设置精准闹钟
            binding.tvTextContent.text = "SCHEDULE_EXACT_ALARM permission granted"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // 注册广播接收者，通过广播监听EXACT_ALARM_PERMISSION状态变化
            ContextCompat.registerReceiver(this, broadcastReceiver, IntentFilter().apply {
                addAction(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED)
            }, ContextCompat.RECEIVER_NOT_EXPORTED)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // 仍然没有授权，考虑使用别的方法
            binding.tvTextContent.text = "SCHEDULE_EXACT_ALARM permission still no granted"
        } else {
            // 设置精准闹钟
            binding.tvTextContent.text = "SCHEDULE_EXACT_ALARM permission granted"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消注册广播接收者
        unregisterReceiver(broadcastReceiver)
    }
}