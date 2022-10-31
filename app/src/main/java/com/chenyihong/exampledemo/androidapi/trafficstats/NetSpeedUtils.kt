package com.chenyihong.exampledemo.androidapi.trafficstats

import android.net.TrafficStats
import com.chenyihong.exampledemo.base.ExampleApplication
import java.util.Timer
import java.util.TimerTask

object NetSpeedUtils {

    var netSpeedCallback: NetSpeedCallback? = null

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    private var lastTotalReceiveBytes: Long = 0
    private var lastTotalTransferBytes: Long = 0

    /**
     * 根据应用uid获取设备启动以来，该应用接收到的总字节数
     */
    fun getTotalReceiveBytes(): Long {
        var receiveBytes: Long = TrafficStats.UNSUPPORTED.toLong()
        ExampleApplication.exampleContext?.run {
            receiveBytes = TrafficStats.getUidRxBytes(applicationInfo.uid)
        }
        return if (receiveBytes == TrafficStats.UNSUPPORTED.toLong()) 0 else receiveBytes / 1024
    }

    /**
     * 根据应用uid获取设备启动以来，该应用传输的总字节数
     */
    fun getTotalTransferBytes(): Long {
        var transferBytes: Long = TrafficStats.UNSUPPORTED.toLong()
        ExampleApplication.exampleContext?.run {
            transferBytes = TrafficStats.getUidTxBytes(applicationInfo.uid)
        }
        return if (transferBytes == TrafficStats.UNSUPPORTED.toLong()) 0 else transferBytes / 1024
    }

    private fun calculateNetSpeed() {
        val nowTotalReceiveBytes = getTotalReceiveBytes()
        val nowTotalTransferBytes = getTotalTransferBytes()

        val downloadSpeed = nowTotalReceiveBytes - lastTotalReceiveBytes
        val uploadSpeed = nowTotalTransferBytes - lastTotalTransferBytes

        lastTotalReceiveBytes = nowTotalReceiveBytes
        lastTotalTransferBytes = nowTotalTransferBytes

        netSpeedCallback?.onNetSpeedChange("$downloadSpeed kb/s", "$uploadSpeed kb/s")
    }

    fun startMeasuringNetSpeed() {
        if (timer == null && timerTask == null) {
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    calculateNetSpeed()
                }
            }
            timer?.run { timerTask?.let { schedule(it, 0L, 1000L) } }
        }
    }

    fun stopMeasuringNetSpeed() {
        timerTask?.cancel()
        timerTask = null
        timer?.cancel()
        timer = null
    }

    interface NetSpeedCallback {
        fun onNetSpeedChange(downloadSpeed: String, uploadSpeed: String)
    }
}