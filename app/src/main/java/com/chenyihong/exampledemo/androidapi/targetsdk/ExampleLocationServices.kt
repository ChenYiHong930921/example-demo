package com.chenyihong.exampledemo.androidapi.targetsdk

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.chenyihong.exampledemo.R

class ExampleLocationServices : Service() {

    private val locationListener = LocationListener { location ->
        // 获取当前定位
        Log.i("-,-,-","location:$location")
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationCompat.Builder(this, "example_notification_channel")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("foreground services notification")
                .setContentText("test foreground services notification")
                .build()
            ServiceCompat.startForeground(this,this.hashCode(),notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        }
        getSystemService(LocationManager::class.java).run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // 结合多种数据源（传感器、定位）提供定位信息
                requestLocationUpdates(LocationManager.FUSED_PROVIDER, 2000, 0f, locationListener)
            } else {
                if (isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // 使用GPS提供定位信息
                    requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0f, locationListener)
                } else {
                    // 使用网络提供定位信息
                    requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0f, locationListener)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        getSystemService(LocationManager::class.java).removeUpdates(locationListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }else{
            stopForeground(true)
        }
        super.onDestroy()
    }
}