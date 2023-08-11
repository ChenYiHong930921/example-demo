package com.chenyihong.exampledemo.androidapi.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.chenyihong.exampledemo.androidapi.camerax.CameraActivity
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutSensorExampleActivityBinding
import java.util.concurrent.atomic.AtomicBoolean

class SensorExampleActivity : BaseGestureDetectorActivity<LayoutSensorExampleActivityBinding>() {

    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null

    private val accelerometerData = FloatArray(3)

    private val thresholds = 2

    private var enterNextPage = AtomicBoolean(false)

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            // 传感器数据变化时回调此方法
            when (event?.sensor?.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val oldX = accelerometerData[0]
                    val oldY = accelerometerData[1]
                    val oldZ = accelerometerData[2]

                    val currentX = event.values[0]
                    val currentY = event.values[1]
                    val currentZ = event.values[2]

                    // 判断3个轴的加速度变化是否超过阈值，超过则代表发生了移动
                    val xMove = oldX != 0f && currentX - oldX > thresholds
                    val yMove = oldY != 0f && currentY - oldY > thresholds
                    val zMove = oldZ != 0f && currentZ - oldZ > thresholds

                    accelerometerData[0] = currentX
                    accelerometerData[1] = currentY
                    accelerometerData[2] = currentZ

                    // 任意一个方向发生了位移，打开新页面
                    if (xMove || yMove || zMove) {
                        // 避免打开多个页面
                        if (!enterNextPage.get()) {
                            enterNextPage.set(true)
                            startActivity(Intent(this@SensorExampleActivity, CameraActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // 传感器的精度发生变化时回调此方法，通常无需做处理
            Log.i("-,-,-", "onAccuracyChanged accuracy:$accuracy")
        }
    }

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutSensorExampleActivityBinding {
        return LayoutSensorExampleActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeTitle.tvTitle.text = "Sensor Example"
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor?.let {
            // 注册传感器监听并且设置数据采样延迟
            // SensorManager.SENSOR_DELAY_FASTEST 延迟0微妙
            // SensorManager.SENSOR_DELAY_GAME 演示20000微妙
            // SensorManager.SENSOR_DELAY_UI 延迟60000微妙
            // SensorManager.SENSOR_DELAY_NORMAL 延迟200000微秒
            sensorManager.registerListener(sensorEventListener, it, 10 * 1000 * 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        // 移除传感器监听
        sensorManager.unregisterListener(sensorEventListener)
    }
}