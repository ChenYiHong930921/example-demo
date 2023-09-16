package com.chenyihong.exampledemo.androidapi.sensor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.chenyihong.exampledemo.androidapi.camerax.CameraActivity
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutSensorExampleActivityBinding
import java.util.concurrent.atomic.AtomicBoolean

const val TYPE_DETECTING_SHAKING = "shaking"
const val TYPE_DETECTING_SCREEN_ORIENTATION = "screen orientation"
const val TYPE_DETECTING_STEP_BY_STEP_COUNTER = "step counter"
const val TYPE_DETECTING_STEP_BY_STEP_DETECTOR = "step detector"
const val TYPE_DETECTING_LIGHT = "light detector"

class SensorExampleActivity : BaseGestureDetectorActivity<LayoutSensorExampleActivityBinding>() {

    private lateinit var sensorManager: SensorManager
    private var sensor = ArrayList<Sensor?>()

    private var type: String = TYPE_DETECTING_SCREEN_ORIENTATION

    private var isSensorListenerRegister = false

    //<editor-folder desc = "screen orientation">

    private val orientationAccelerometerData = FloatArray(3)
    private val orientationMagnetometerData = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private fun updateOrientationAngles() {
        // 根据加速度计传感器和磁力计传感器的读数更新旋转矩阵
        SensorManager.getRotationMatrix(rotationMatrix, null, orientationAccelerometerData, orientationMagnetometerData)
        // 根据旋转矩阵重新计算三个方向角
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        val degree = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        // 设置方位角旋转度数
        binding.ivScreenOrientation.run { post { rotation = degree } }
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

    //<editor-folder desc = "step detecting">

    private var accumulatedSteps = -1

    private var currentStep = 0

    private var requestPermissionName: String = ""

    private val requestSinglePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted: Boolean ->
        if (granted) {
            initStepSensor()
        } else {
            // 未同意授权
            if (!shouldShowRequestPermissionRationale(requestPermissionName)) {
                //用户拒绝权限并且系统不再弹出请求权限的弹窗
                //这时需要我们自己处理，比如自定义弹窗告知用户为何必须要申请这个权限
            }
        }
    }

    private fun checkActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissionName = Manifest.permission.ACTIVITY_RECOGNITION
            if (ActivityCompat.checkSelfPermission(this, requestPermissionName) == PackageManager.PERMISSION_GRANTED) {
                initStepSensor()
            } else {
                requestSinglePermissionLauncher.launch(requestPermissionName)
            }
        }
    }

    private fun initStepSensor() {
        when (type) {
            TYPE_DETECTING_STEP_BY_STEP_COUNTER -> sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER))
            TYPE_DETECTING_STEP_BY_STEP_DETECTOR -> sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR))
        }
        registerSensorListener()
    }
    //</editor-folder>

    private val sensorEventListener = object : SensorEventListener {
        @SuppressLint("SetTextI18n")
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

                Sensor.TYPE_ORIENTATION -> {
                    // 设置方位角旋转度数
                    binding.ivScreenOrientation.run { post { rotation = event.values[0] } }
                }

                Sensor.TYPE_STEP_COUNTER -> {
                    // 注意，计步器传感器返回的数据
                    // 是自计步器传感器上次重启以来用户行走的总的步数
                    currentStep = if (accumulatedSteps == -1) {
                        accumulatedSteps = event.values[0].toInt()
                        0
                    } else {
                        event.values[0].toInt() - accumulatedSteps
                    }
                    binding.tvTextContent.run { post { text = "传感器回调步数:${event.values[0].toInt()}\n首次回调步数:$accumulatedSteps\n本次行走步数:$currentStep" } }
                }

                Sensor.TYPE_STEP_DETECTOR -> {
                    currentStep += event.values[0].toInt()
                    binding.tvTextContent.run { post { text = "Step:$currentStep" } }
                }

                Sensor.TYPE_LIGHT -> {
                    // 改变窗口的亮度
                    window.attributes = window.attributes.apply {
                        // 以多云天气时的光照强度作为参考值计算屏幕亮度该设置多少
                        binding.tvTextContent.run { post { text = "照度:${event.values[0]}, 屏幕亮度:${event.values[0] / SensorManager.LIGHT_CLOUDY}" } }
                        screenBrightness = event.values[0] / SensorManager.LIGHT_CLOUDY
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
        type = intent.getStringExtra("type") ?: TYPE_DETECTING_SCREEN_ORIENTATION
        when (type) {
            TYPE_DETECTING_SHAKING -> {
                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
            }

            TYPE_DETECTING_STEP_BY_STEP_COUNTER, TYPE_DETECTING_STEP_BY_STEP_DETECTOR -> {
                checkActivityRecognitionPermission()
                binding.tvTextContent.visibility = View.VISIBLE
            }

            TYPE_DETECTING_LIGHT -> {
                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT))
                binding.tvTextContent.visibility = View.VISIBLE
            }

            else -> {
                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))
//                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION))
                binding.ivScreenOrientation.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerSensorListener()
    }

    override fun onPause() {
        super.onPause()
        // 移除传感器监听
        sensorManager.unregisterListener(sensorEventListener)
        isSensorListenerRegister = false
    }

    private fun registerSensorListener() {
        if (sensor.isNotEmpty() && !isSensorListenerRegister) {
            isSensorListenerRegister = true
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
    }
}