package com.chenyihong.exampledemo.androidapi.targetsdk

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.chenyihong.exampledemo.R

class ExampleMediaProjectionServices : Service() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationCompat.Builder(this, "example_notification_channel")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("media projection notification")
                .setContentText("test media projection notification")
                .build()
            ServiceCompat.startForeground(this,this.hashCode(),notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}