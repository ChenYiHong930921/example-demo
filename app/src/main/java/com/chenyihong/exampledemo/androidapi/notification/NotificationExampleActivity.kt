package com.chenyihong.exampledemo.androidapi.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutNotificationExampleActivityBinding
import com.chenyihong.exampledemo.utils.SpUtils

class NotificationExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutNotificationExampleActivityBinding

    private var checkByNotificationAPI = false

    private val notRequestAgainKey = "notRequestAgain"

    private val notificationManagerCompat: NotificationManagerCompat by lazy { NotificationManagerCompat.from(this) }

    @SuppressLint("InlinedApi")
    private val postNotificationPermission = Manifest.permission.POST_NOTIFICATIONS

    private val singlePermissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        // 申请权限回调
        // granted = true 表示已授权，granted = false 表示未授权
        if (!granted) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, postNotificationPermission)) {
                //用户拒绝权限并且系统不再弹出请求权限的弹窗
                //保存结果，用于判断下次请求权限时是否使用自定义弹窗
                SpUtils.put(notRequestAgainKey, true)
            }
        }
    }

    private val intentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // 页面关闭回调
        // 可以在此对通知是否可用再进行一次判断，但如果不可用最好不要直接再次申请，避免用户厌烦
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutNotificationExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        if (!SpUtils.isInit()) {
            SpUtils.init(this)
        }
        val notRequestAgain = SpUtils.getBoolean(notRequestAgainKey, false)
        if (!notificationEnable()) {
            if (notRequestAgain) {
                // 显示自定义弹窗
                showPermissionStatementDialog()
            } else {
                // 显示系统通知权限弹窗
                singlePermissionRequestLauncher.launch(postNotificationPermission)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val applicationInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                packageManager.getApplicationInfo(packageName, 0)
            }

            // 分组id
            val systemNotificationGroupId = "system_notification_group"
            // 分组显示名称
            val systemNotificationGroupName = "${getText(applicationInfo.labelRes)} System Notification Group"
            // 分组描述
            val systemNotificationGroupDescription = "Receive all system-level notifications"
            notificationManagerCompat.createNotificationChannelGroup(NotificationChannelGroupCompat.Builder(systemNotificationGroupId)
                .setName(systemNotificationGroupName)
                .setDescription(systemNotificationGroupDescription)
                .build())

            val messageNotificationGroupId = "message_notification_group"
            val messageNotificationGroupName = "${getText(applicationInfo.labelRes)} Message Notification Group"
            val messageNotificationGroupDescription = "Receive all message notifications"
            notificationManagerCompat.createNotificationChannelGroup(NotificationChannelGroupCompat.Builder(messageNotificationGroupId)
                .setName(messageNotificationGroupName)
                .setDescription(messageNotificationGroupDescription)
                .build())

            // 渠道id
            val systemErrorChannelId = "system_error_notification_channel"
            // 渠道显示名称
            val systemErrorChannelDisplayName = "${getText(applicationInfo.labelRes)} System Error Notification Channel"
            // 渠道的重要性级别
            val systemErrorChannelImportance = NotificationManager.IMPORTANCE_HIGH
            val systemErrorChannel = NotificationChannel(systemErrorChannelId, systemErrorChannelDisplayName, systemErrorChannelImportance).apply {
                // 渠道描述
                description = "Receive system error notifications"
                group = systemNotificationGroupId
            }
            notificationManagerCompat.createNotificationChannel(systemErrorChannel)

            val friendMessageChannelId = "friend_message_notification_channel"
            val friendMessageChannelDisplayName = "${getText(applicationInfo.labelRes)} Friend Message Notification Channel"
            val friendMessageChannelImportance = NotificationManager.IMPORTANCE_DEFAULT
            val friendMessageChannel = NotificationChannel(friendMessageChannelId, friendMessageChannelDisplayName, friendMessageChannelImportance).apply {
                // 渠道描述
                description = "Receive friend message notifications"
                group = messageNotificationGroupId
            }
            notificationManagerCompat.createNotificationChannel(friendMessageChannel)

            binding.btnCheckChannelSetting.setOnClickListener {
                notificationManagerCompat.getNotificationChannelCompat(systemErrorChannelId)?.apply {
                    if (importance == NotificationManager.IMPORTANCE_MIN) {
                        intentLauncher.launch(Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                            putExtra(Settings.EXTRA_CHANNEL_ID, id)
                        })
                    }
                }
            }

            binding.btnDeleteChannel.setOnClickListener {
                notificationManagerCompat.deleteNotificationChannel(friendMessageChannelId)
                // 也可以通过id删除渠道分组
//                notificationManagerCompat.deleteNotificationChannelGroup(messageNotificationGroupId)
            }
        }

        binding.btnCreateBasicNotification.setOnClickListener {
            if (notificationEnable()) {
                val notificationBuilder = NotificationCompat.Builder(this, "system_error_notification_channel")
                    // 设置小图标（必须设置，否则会引起崩溃）
                    .setSmallIcon(R.drawable.notification)
                    // 设置通知标题
                    .setContentTitle("Example title")
                    // 设置通知内容
                    .setContentText("This is a basic notification example.")
                    // 设置是否自动取消
                    .setAutoCancel(false)

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                    // Android 7.1以下通知渠道配置的优先级无效，需通过setPriority()设置通知优先级
                    notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH)
                }

                // 通知id，可以记录下来，后续可以通过通知id对通知进行操作
                val notificationId = 0
                notificationManagerCompat.notify(notificationId, notificationBuilder.build())
            }
        }
    }

    private fun notificationEnable(): Boolean {
        return if (checkByNotificationAPI) {
            // 通过通知API判断通知是否可用.
            notificationManagerCompat.areNotificationsEnabled()
        } else {
            // 通过POST_NOTIFICATIONS权限判断通知是否可用.
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ActivityCompat.checkSelfPermission(this, postNotificationPermission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showPermissionStatementDialog() {
        val permissionTipsDialog = AlertDialog.Builder(this)
            .setTitle("Statement of Notification Permission")
            .setMessage("Receive notifications to improve user experience!")
            .setCancelable(true)
            .setPositiveButton("grant") { dialog, _ ->
                intentLauncher.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = Uri.parse("package:$packageName") })
                dialog.dismiss()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        permissionTipsDialog.show()
    }
}