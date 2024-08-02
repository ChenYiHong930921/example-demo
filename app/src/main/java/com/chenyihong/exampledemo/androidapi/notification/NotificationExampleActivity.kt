package com.chenyihong.exampledemo.androidapi.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutNotificationExampleActivityBinding
import com.chenyihong.exampledemo.utils.DensityUtil
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

    // 分组id
    private val systemNotificationGroupId = "system_notification_group"
    private val messageNotificationGroupId = "message_notification_group"

    // 渠道id
    private val enableBadgeSystemErrorChannelId = "enable_badge_system_error_notification_channel"
    private val disableBadgeSystemErrorChannelId = "disable_badge_system_error_notification_channel"
    private var currentSystemErrorChannelId = ""
    private val friendMessageChannelId = "friend_message_notification_channel"

    private val currentSystemErrorChannelIdKey = "currentSystemErrorChannelId"

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

            if (notificationManagerCompat.getNotificationChannelGroupCompat(systemNotificationGroupId) == null) {
                // 分组显示名称
                val systemNotificationGroupName = "${getText(applicationInfo.labelRes)} System Notification Group"
                // 分组描述
                val systemNotificationGroupDescription = "Receive all system-level notifications"
                createNotificationGroup(systemNotificationGroupId, systemNotificationGroupName, systemNotificationGroupDescription)
            }

            if (notificationManagerCompat.getNotificationChannelGroupCompat(messageNotificationGroupId) == null) {
                val messageNotificationGroupName = "${getText(applicationInfo.labelRes)} Message Notification Group"
                val messageNotificationGroupDescription = "Receive all message notifications"
                createNotificationGroup(messageNotificationGroupId, messageNotificationGroupName, messageNotificationGroupDescription)
            }

            // 获取当前保存的渠道id
            // 默认关闭圆点提示
            currentSystemErrorChannelId = SpUtils.getString(currentSystemErrorChannelIdKey, disableBadgeSystemErrorChannelId) ?: disableBadgeSystemErrorChannelId
            if (notificationManagerCompat.getNotificationChannelCompat(currentSystemErrorChannelId) == null) {
                // 渠道显示名称
                val systemErrorChannelDisplayName = "${getText(applicationInfo.labelRes)} System Error Notification Channel"
                // 渠道描述
                val systemErrorChannelDescription = "Receive system error notifications"
                // 渠道的重要性级别
                val systemErrorChannelImportance = NotificationManager.IMPORTANCE_HIGH
                createNotificationChannel(currentSystemErrorChannelId, systemErrorChannelDisplayName, systemErrorChannelDescription, systemErrorChannelImportance, currentSystemErrorChannelId == enableBadgeSystemErrorChannelId, systemNotificationGroupId)
            }

            if (notificationManagerCompat.getNotificationChannelCompat(friendMessageChannelId) == null) {
                val friendMessageChannelDisplayName = "${getText(applicationInfo.labelRes)} Friend Message Notification Channel"
                val friendMessageChannelDescription = "Receive friend message notifications"
                val friendMessageChannelImportance = NotificationManager.IMPORTANCE_DEFAULT
                createNotificationChannel(friendMessageChannelId, friendMessageChannelDisplayName, friendMessageChannelDescription, friendMessageChannelImportance, channelGroupId = messageNotificationGroupId)
            }

            binding.btnCheckChannelSetting.setOnClickListener {
                notificationManagerCompat.getNotificationChannelCompat(currentSystemErrorChannelId)?.apply {
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
                val notificationBuilder = NotificationCompat.Builder(this, currentSystemErrorChannelId)
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
        binding.btnCreatePictureNotification.setOnClickListener {
            if (notificationEnable()) {
                ContextCompat.getDrawable(this, R.drawable.big_picture_example)?.also { bigPictureExample ->
                    val emptyIcon: Icon? = null
                    val notificationBuilder = NotificationCompat.Builder(this, currentSystemErrorChannelId)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle("Big picture Notification")
                        .setContentText("This is a big picture notification example.")
                        // 折叠状态下的图标
                        .setLargeIcon(toBitmap(bigPictureExample, DensityUtil.dp2Px(24), DensityUtil.dp2Px(24)))
                        .setStyle(NotificationCompat.BigPictureStyle()
                            // 展开状态下的大图
                            .bigPicture(toBitmap(bigPictureExample))
                            // 传入null可以隐藏折叠状态下的图标（按需使用）
                            // 入参为Icon和入参为Bitmap的bigLargeIcon重载方法都允许传null，并且都只有一个参数
                            // 直接传null会提示匹配到了多个方法
                            .bigLargeIcon(emptyIcon))
                        .setAutoCancel(false)
                    val notificationId = 10001
                    notificationManagerCompat.notify(notificationId, notificationBuilder.build())
                }
            }
        }
        binding.btnCreateTextNotification.setOnClickListener {
            if (notificationEnable()) {
                ContextCompat.getDrawable(this, R.drawable.icon_juejin)?.also { bigPictureExample ->
                    val bigTextExample = ContextCompat.getString(this, R.string.label_notification_big_text_example)
                    val bigTextExampleSpannableString = SpannableStringBuilder(bigTextExample).apply {
                        // 前六个字符改为蓝色
                        setSpan(ForegroundColorSpan(ContextCompat.getColor(this@NotificationExampleActivity, R.color.color_blue_229CE9)), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    val notificationBuilder = NotificationCompat.Builder(this, currentSystemErrorChannelId)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle("Big Text Notification")
                        .setContentText("This is a big text notification example.")
                        .setLargeIcon(toBitmap(bigPictureExample, DensityUtil.dp2Px(24), DensityUtil.dp2Px(24)))
                        .setStyle(NotificationCompat.BigTextStyle()
                            // 设置一段ChatGPT生成的掘金简介
                            .bigText(bigTextExampleSpannableString))
                        .setAutoCancel(false)
                    val notificationId = 10002
                    notificationManagerCompat.notify(notificationId, notificationBuilder.build())
                }
            }
        }
        binding.btnCreateSegmentedTextNotification.setOnClickListener {
            if (notificationEnable()) {
                ContextCompat.getDrawable(this, R.drawable.icon_juejin)?.also { bigPictureExample ->
                    val notificationBuilder = NotificationCompat.Builder(this, currentSystemErrorChannelId)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle("Segmented Text Notification")
                        .setContentText("This is a segmented text notification example.")
                        .setLargeIcon(toBitmap(bigPictureExample, DensityUtil.dp2Px(24), DensityUtil.dp2Px(24)))
                        .setStyle(NotificationCompat.InboxStyle()
                            .addLine("line 1 message")
                            .addLine("line 2 message")
                            .addLine("line 3 message")
                            .addLine("line 4 message")
                            .addLine("line 5 message")
                            .addLine("line 6 message")
                            .addLine("line 7 message")
                        )
                        .setAutoCancel(false)
                    val notificationId = 10003
                    notificationManagerCompat.notify(notificationId, notificationBuilder.build())
                }
            }
        }
        binding.btnEnableShowBadge.setOnClickListener {
            notificationManagerCompat.getNotificationChannelCompat(currentSystemErrorChannelId)?.apply {
                // 获取当前的系统信息通知渠道，如果圆点提示是禁用的，创建一个新的启用圆点提示的系统通知渠道
                if (!canShowBadge()) {
                    val currentChannelName = name?.toString() ?: ""
                    val currentChannelDescription = description ?: ""
                    val currentImportance = importance
                    val currentGroupId = group ?: ""
                    notificationManagerCompat.deleteNotificationChannel(id)
                    currentSystemErrorChannelId = enableBadgeSystemErrorChannelId
                    createNotificationChannel(currentSystemErrorChannelId, currentChannelName, currentChannelDescription, currentImportance, channelGroupId = currentGroupId)
                    SpUtils.put(currentSystemErrorChannelIdKey, currentSystemErrorChannelId)
                }
            }
        }
        binding.btnDisableShowBadge.setOnClickListener {
            notificationManagerCompat.getNotificationChannelCompat(currentSystemErrorChannelId)?.apply {
                // 获取当前的系统信息通知渠道，如果圆点提示是启用的，创建一个新的启用圆点提示的系统通知渠道
                if (canShowBadge()) {
                    val currentChannelName = name?.toString() ?: ""
                    val currentChannelDescription = description ?: ""
                    val currentImportance = importance
                    val currentGroupId = group ?: ""
                    notificationManagerCompat.deleteNotificationChannel(id)
                    currentSystemErrorChannelId = disableBadgeSystemErrorChannelId
                    createNotificationChannel(currentSystemErrorChannelId, currentChannelName, currentChannelDescription, currentImportance, false, currentGroupId)
                    SpUtils.put(currentSystemErrorChannelIdKey, currentSystemErrorChannelId)
                }
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

    private fun toBitmap(drawable: Drawable, iconWidth: Int = 0, iconHeight: Int = 0): Bitmap {
        val width = if (iconWidth == 0) drawable.minimumWidth else iconWidth
        val height = if (iconHeight == 0) drawable.minimumHeight else iconHeight
        val oldBounds = Rect(drawable.bounds)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(Canvas(bitmap))
        drawable.bounds = oldBounds
        return bitmap
    }

    private fun createNotificationGroup(channelGroupId: String, displayName: String, displayDescription: String) {
        notificationManagerCompat.createNotificationChannelGroup(NotificationChannelGroupCompat.Builder(channelGroupId)
            .setName(displayName)
            .setDescription(displayDescription)
            .build())
    }

    private fun createNotificationChannel(channelId: String, displayName: String, displayDescription: String, importance: Int, showBadge: Boolean = true, channelGroupId: String? = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManagerCompat.createNotificationChannel(NotificationChannel(channelId, displayName, importance).apply {
                description = displayDescription
                setShowBadge(showBadge)
                group = channelGroupId
            })
        }
    }
}