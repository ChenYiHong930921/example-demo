package com.chenyihong.exampledemo.androidapi.timechange

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.view.LayoutInflater
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutTimeChangeExampleActivityBinding
import java.text.SimpleDateFormat
import java.util.*

const val TAG = "TimeChangeExampleTag"

class TimeChangeExample : BaseGestureDetectorActivity<LayoutTimeChangeExampleActivityBinding>() {

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutTimeChangeExampleActivityBinding {
        return LayoutTimeChangeExampleActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnCountdownByTimer.setOnClickListener {
            clearText()
            binding.tvCountdownText.text = "countdown by timer\n"
            startCountdownByTime()
        }
        binding.btnStopTimer.setOnClickListener {
            stopTimer()
        }

        binding.btnCountdownByBroadcast.setOnClickListener {
            clearText()
            binding.tvCountdownText.text = "countdown by broadcast\n"
            startCountdownByBroadcast()
        }
        binding.btnStopBroadcast.setOnClickListener {
            stopBroadcast()
        }

        binding.btnCountdownByHandler.setOnClickListener {
            clearText()
            binding.tvCountdownText.text = "countdown by handler\n"
            startCountdownByHandler()
        }
        binding.btnStopHandler.setOnClickListener {
            stopHandler()
        }
    }

    // <editor-folder desc = "Timer" >

    private var timerHandler = object : Handler(Looper.myLooper() ?: Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0) {
                setCountdownTimeText(msg.obj as Long)
            }
        }
    }
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    private fun startCountdownByTime() {
        stopTimer()
        timerTask = object : TimerTask() {
            override fun run() {
                timerHandler.sendMessage(timerHandler.obtainMessage(0, System.currentTimeMillis()))
            }
        }
        timer = Timer()
        timer?.schedule(timerTask, 0, 1000)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
        timerTask = null
    }
    // </editor-folder>

    // <editor-folder desc = "handler" >

    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
    private val countdownRunnable = object : Runnable {
        override fun run() {
            setCountdownTimeText(System.currentTimeMillis())
            val currentTime = SystemClock.uptimeMillis()
            val nextTime = currentTime + (1000 - currentTime % 1000)
            handler.postAtTime(this, nextTime)
        }
    }

    private fun startCountdownByHandler() {
        val currentTime = SystemClock.uptimeMillis()
        val nextTime = currentTime + (1000 - currentTime % 1000)
        handler.postAtTime(countdownRunnable, nextTime)
    }

    private fun stopHandler() {
        handler.removeCallbacks(countdownRunnable)
    }
    // </editor-folder>

    // <editor-folder desc = "BroadcastReceiver" >

    private val timeChangeBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_TIME_TICK) {
                setCountdownTimeText(System.currentTimeMillis())
            }
        }
    }

    private fun startCountdownByBroadcast() {
        registerReceiver(timeChangeBroadcastReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
        })
    }

    private fun stopBroadcast() {
        try {
            unregisterReceiver(timeChangeBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // </editor-folder>

    @SuppressLint("SetTextI18n")
    private fun setCountdownTimeText(time: Long) {
        binding.tvCountdownText.run {
            post {
                text = text.toString() + "${dateFormat.format(Date(time))}\n"
            }
        }
    }

    private fun clearText() {
        binding.tvCountdownText.text = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        stopHandler()
        stopBroadcast()
    }
}