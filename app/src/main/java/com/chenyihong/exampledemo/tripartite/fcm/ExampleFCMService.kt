package com.chenyihong.exampledemo.tripartite.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.chenyihong.exampledemo.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

internal class ExampleFCMService : FirebaseMessagingService() {

    private val requestCode = this.hashCode()
    private val exampleNotificationChannel = "example_notification_channel"
    private var notificationId = 0

    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
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

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // FCM生成的令牌，可以用于标识用户的身份
        Log.i("FCMExampleTag", "Firebase cloud message onNewToken token:$token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // 接收到推送消息时回调此方法
        message.notification?.let { postNotification(it) }
        Log.i("FCMExampleTag", "Firebase cloud message onMessageReceived message:${message.notification?.body}")
    }

    private fun postNotification(remoteNotification: RemoteMessage.Notification) {
        if (notificationManager.areNotificationsEnabled()) {
            val notification = NotificationCompat.Builder(this@ExampleFCMService, exampleNotificationChannel)
                //设置小图标
                .setSmallIcon(R.drawable.notification)
                // 设置通知标题
                .setContentTitle(remoteNotification.title)
                // 设置通知内容
                .setContentText(remoteNotification.body)
                // 设置是否自动取消
                .setAutoCancel(true)
                // 设置通知声音
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                // 设置点击的事件
                .setContentIntent(PendingIntent.getActivity(this@ExampleFCMService, requestCode, packageManager.getLaunchIntentForPackage(packageName)?.apply { putExtra("routes", "From notification") }, PendingIntent.FLAG_IMMUTABLE))
                .build()
            // notificationId可以记录下来
            // 可以通过notificationId对通知进行相应的操作
            notificationManager.notify(notificationId, notification)
            notificationId++
        }
    }
}