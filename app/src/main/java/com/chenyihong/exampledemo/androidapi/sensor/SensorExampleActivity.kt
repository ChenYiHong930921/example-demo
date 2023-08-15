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
import android.view.View
import com.chenyihong.exampledemo.androidapi.camerax.CameraActivity
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutSensorExampleActivityBinding
import java.util.concurrent.atomic.AtomicBoolean

const val TYPE_DETECTING_SHAKING = "shaking"
const val TYPE_DETECTING_SCREEN_ORIENTATION = "screen orientation"
const val TYPE_DETECTING_STEP_BY_STEP_COUNTER = "step counter"
const val TYPE_DETECTING_STEP_BY_STEP_DETECTOR = "step detector"

class SensorExampleActivity : BaseGestureDetectorActivity<LayoutSensorExampleActivityBinding>() {

    private lateinit var sensorManager: SensorManager
    private var sensor = ArrayList<Sensor?>()

    private var type: String = TYPE_DETECTING_SCREEN_ORIENTATION

    //<editor-folder desc = "screen orientation">

    private val orientationAccelerometerData = FloatArray(3)
    private val orientationMagnetometerData = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, orientationAccelerometerData, orientationMagnetometerData)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        val degree = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        binding.ivScreenOrientation.rotation = degree
    }
    //</editor-folder>

    //<editor-folder desc = "shaking">

    private val accelerometerData = FloatArray(3)
    private val thresholds = 2
    private var enterNextPage = AtomicBoolean(false)

    private fun checkShakingEvent(event: SensorEvent) {
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
    //</editor-folder>

    //<editor-folder desc = "step counter">

    //</editor-folder>

    //<editor-folder desc = "step detector">

    //</editor-folder>

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            // 传感器数据变化时回调此方法
            when (event?.sensor?.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    if (type == TYPE_DETECTING_SHAKING) {
                        checkShakingEvent(event)
                    } else {
                        System.arraycopy(event.values, 0, orientationAccelerometerData, 0, orientationAccelerometerData.size)
                        updateOrientationAngles()
                    }
                }

                Sensor.TYPE_MAGNETIC_FIELD -> {
                    System.arraycopy(event.values, 0, orientationMagnetometerData, 0, orientationMagnetometerData.size)
                    updateOrientationAngles()
                }

                Sensor.TYPE_STEP_COUNTER -> {
                    event.values.forEach {
                        Log.i("-,-,-", "TYPE_STEP_COUNTER value: $it")
                    }
                }

                Sensor.TYPE_STEP_DETECTOR -> {
                    event.values.forEach {
                        Log.i("-,-,-", "TYPE_STEP_DETECTOR value: $it")
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
        when (intent.getStringExtra("type")) {
            TYPE_DETECTING_SHAKING -> {
                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
            }

            TYPE_DETECTING_STEP_BY_STEP_COUNTER -> {
                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER))
            }

            TYPE_DETECTING_STEP_BY_STEP_DETECTOR -> {
                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR))
            }

            else -> {
                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))
                binding.ivScreenOrientation.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensor.forEach { item ->
            item?.let {
                // 注册传感器监听并且设置数据采样延迟
                // SensorManager.SENSOR_DELAY_FASTEST 延迟0微妙
                // SensorManager.SENSOR_DELAY_GAME 演示20000微妙
                // SensorManager.SENSOR_DELAY_UI 延迟60000微妙
                // SensorManager.SENSOR_DELAY_NORMAL 延迟200000微秒
                sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // 移除传感器监听
        sensorManager.unregisterListener(sensorEventListener)
    }
}