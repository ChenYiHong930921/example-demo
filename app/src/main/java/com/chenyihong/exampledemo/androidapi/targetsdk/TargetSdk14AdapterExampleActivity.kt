package com.chenyihong.exampledemo.androidapi.targetsdk

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.camerax.CameraActivity
import com.chenyihong.exampledemo.androidapi.media3.Media3HomeActivity
import com.chenyihong.exampledemo.databinding.LayoutTargetSdk14AdapterExampleActivityBinding

@SuppressLint("SetTextI18n")
class TargetSdk14AdapterExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutTargetSdk14AdapterExampleActivityBinding

    private lateinit var alarmManager: AlarmManager
    private var requestExactAlarm = false

    private lateinit var notificationManager: NotificationManagerCompat
    private val exampleNotificationChannel = "example_notification_channel"
    private var requestFullScreenIntent = false

    @SuppressLint("InlinedApi")
    private var requestPermissionNames = arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
//    private var requestPermissionNames = arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)

    private val requestMultiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
            val noGrantedPermissions = ArrayList<String>()
            permissions.entries.forEach {
                if (!it.value) {
                    noGrantedPermissions.add(it.key)
                }
            }
            if (noGrantedPermissions.isEmpty()) {
                // 申请权限通过，可以处理选择照片或视频资源
            } else {
                //未同意授权
                noGrantedPermissions.forEach {
                    if (!shouldShowRequestPermissionRationale(it)) {
                        //用户拒绝权限并且系统不再弹出请求权限的弹窗
                        //这时需要我们自己处理，比如自定义弹窗告知用户为何必须要申请这个权限
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

        notificationManager = NotificationManagerCompat.from(this)
        createNotificationChannel()

        binding.btnExactAlarms.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                requestExactAlarm = true
                // 没有权限，申请用户授权
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            } else {
                // 获得授权，设置精准闹钟
                openMediaActivityLater()
            }
        }
        binding.btnKillBackgroundProcess.setOnClickListener {
            // 需要在Manifest中配置android.permission.KILL_BACKGROUND_PROCESSES权限
            getSystemService(ActivityManager::class.java)?.killBackgroundProcesses("com.chenyihong.exampleadmobdemo")
        }
        binding.btnRequestMediaPermission.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestMultiplePermissionLauncher.launch(requestPermissionNames)
            }
        }
        binding.btnFullScreenNotification.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !notificationManager.canUseFullScreenIntent()) {
                requestFullScreenIntent = true
                // 没有权限，申请用户授权
                startActivity(Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT))
            } else {
                // 获得授权，发送全屏通知
                postFullScreenNotification()
            }
        }
        binding.btnOngoingNotification.setOnClickListener {
            postOngoingNotification()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requestExactAlarm) {
            requestExactAlarm = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // 仍然没有授权，考虑使用别的方法
                binding.tvTextContent.text = "SCHEDULE_EXACT_ALARM permission still no granted"
            } else {
                // 获得授权，设置精准闹钟
                openMediaActivityLater()
            }
        }
        if (requestFullScreenIntent) {
            requestFullScreenIntent = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !notificationManager.canUseFullScreenIntent()) {
                // 仍然没有授权，考虑使用别的方法
                binding.tvTextContent.text = "USE_FULL_SCREEN_INTENT permission still no granted"
            } else {
                // 获得授权，发送全屏通知
                postFullScreenNotification()
            }
        }
    }

    private fun openMediaActivityLater() {
        // 设置精准闹钟，打开指定页面
        val openMedia3ActivityPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, Media3HomeActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 1000, openMedia3ActivityPendingIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建通知渠道
            val applicationInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                packageManager.getApplicationInfo(packageName, 0)
            }
            val exampleChannel = NotificationChannel(exampleNotificationChannel, "${getText(applicationInfo.labelRes)} Notification Channel", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "The description of this notification channel"
            }
            notificationManager.createNotificationChannel(exampleChannel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun postFullScreenNotification() {
        val notification = NotificationCompat.Builder(this, "example_notification_channel")
            //设置小图标
            .setSmallIcon(R.drawable.notification)
            // 设置通知标题
            .setContentTitle("full screen notification")
            // 设置通知内容
            .setContentText("test full screen notification")
            .setFullScreenIntent(PendingIntent.getActivity(this, this.hashCode(), Intent(this, CameraActivity::class.java), PendingIntent.FLAG_IMMUTABLE), true)
            .build()
        notificationManager.notify(this.hashCode() + 1, notification)
    }

    @SuppressLint("MissingPermission")
    private fun postOngoingNotification() {
        val notification = NotificationCompat.Builder(this, "example_notification_channel")
            //设置小图标
            .setSmallIcon(R.drawable.notification)
            // 设置通知标题
            .setContentTitle("ongoing notification")
            // 设置通知内容
            .setContentText("test ongoing notification")
            .setContentIntent(PendingIntent.getActivity(this, this.hashCode(), Intent(this, CameraActivity::class.java), PendingIntent.FLAG_IMMUTABLE))
            .setOngoing(true)
            .build()
        notificationManager.notify(this.hashCode() + 2, notification)
    }
}