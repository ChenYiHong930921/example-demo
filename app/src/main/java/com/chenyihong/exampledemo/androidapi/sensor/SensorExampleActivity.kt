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
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.camerax.CameraActivity
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutSensorExampleActivityBinding
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import java.util.concurrent.atomic.AtomicBoolean

const val TYPE_DETECTING_SHAKING = "shaking"
const val TYPE_DETECTING_SCREEN_ORIENTATION_BY_SENSOR_MANAGER = "sensor manager"
const val TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR = "orientation sensor"
const val TYPE_DETECTING_STEP_BY_STEP_COUNTER = "step counter"
const val TYPE_DETECTING_STEP_BY_STEP_DETECTOR = "step detector"

class SensorExampleActivity : BaseGestureDetectorActivity<LayoutSensorExampleActivityBinding>() {

    private lateinit var sensorManager: SensorManager
    private var sensor = ArrayList<Sensor?>()

    private var type: String = TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR

    private var isSensorListenerRegister = false

    //<editor-folder desc = "screen orientation">

    private val orientationAccelerometerData = FloatArray(3)
    private val orientationMagnetometerData = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val mapViewBundleKey = "MapViewBundleKey"
    private var googleMap: GoogleMap? = null
    private var locationManager: LocationManager? = null
    private var currentLatLong: LatLng? = null

    @SuppressLint("NewApi")
    private val locationListener = LocationListener { location ->
        if (currentLatLong?.latitude != location.latitude && currentLatLong?.longitude != location.longitude) {
            googleMap?.run {
                currentLatLong = LatLng(location.latitude, location.longitude)
                animateCamera(CameraUpdateFactory.newLatLng(currentLatLong))
            }
        }
    }

    private val requestMultiplePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
        val noGrantedPermissions = ArrayList<String>()
        permissions.entries.forEach {
            if (!it.value) {
                noGrantedPermissions.add(it.key)
            }
        }
        if (noGrantedPermissions.isEmpty()) {
            // 申请权限通过，可以使用地图
            initMapView()
        } else {
            //未同意授权
            noGrantedPermissions.forEach {
                if (!shouldShowRequestPermissionRationale(it)) {
                    //用户拒绝权限并且系统不再弹出请求权限的弹窗
                    //这时需要我们自己处理，比如自定义弹窗告知用户为何必须要申请这个权限
                }
            }
        }
    }

    private fun updateOrientationAngles() {
        // 根据加速度计传感器和磁力计传感器的读数更新旋转矩阵
        SensorManager.getRotationMatrix(rotationMatrix, null, orientationAccelerometerData, orientationMagnetometerData)
        // 根据旋转矩阵重新计算三个方向角
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        val degree = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        // 设置方位角旋转度数
        addMarkToMap(degree)
    }

    @SuppressLint("MissingPermission")
    private fun initMapView() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestLocationUpdates(LocationManager.FUSED_PROVIDER, 2000, 0f, locationListener)
            } else {
                if (isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0f, locationListener)
                } else {
                    requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0f, locationListener)
                }
            }
        }
        binding.mapView.getMapAsync { googleMap ->
            this.googleMap = googleMap.apply {
                isMyLocationEnabled = false
                moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), maxZoomLevel - 5))
            }
        }
        binding.mapView.visibility = View.VISIBLE
    }

    private fun addMarkToMap(rotationDegree: Float) {
        googleMap?.run {
            clear()
            currentLatLong?.let {
                addMarker(MarkerOptions()
                    .position(it)
                    .rotation(rotationDegree)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_device_orientation)))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR ||
            type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_SENSOR_MANAGER
        ) {
            binding.mapView.onSaveInstanceState(outState.getBundle(mapViewBundleKey) ?: Bundle().apply {
                putBundle(mapViewBundleKey, this)
            })
        }
    }

    override fun onStart() {
        super.onStart()
        if (type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR ||
            type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_SENSOR_MANAGER
        ) {
            binding.mapView.onStart()
        }
    }

    override fun onStop() {
        if (type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR ||
            type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_SENSOR_MANAGER
        ) {
            binding.mapView.onStop()
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR ||
            type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_SENSOR_MANAGER
        ) {
            binding.mapView.onDestroy()
            locationManager?.removeUpdates(locationListener)
        }
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR ||
            type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_SENSOR_MANAGER
        ) {
            binding.mapView.onLowMemory()
        }
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
                    addMarkToMap(event.values[0])
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
                    binding.tvStepCount.run { post { text = "传感器回调步数:${ event.values[0].toInt()}\n首次回调步数:$accumulatedSteps\n本次行走步数:$currentStep" } }
                }

                Sensor.TYPE_STEP_DETECTOR -> {
                    currentStep += event.values[0].toInt()
                    binding.tvStepCount.run { post { text = "Step:$currentStep" } }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // 传感器的精度发生变化时回调此方法，通常无需做处理
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
        type = intent.getStringExtra("type") ?: TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR
        when (type) {
            TYPE_DETECTING_SHAKING -> {
                sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
            }

            TYPE_DETECTING_STEP_BY_STEP_COUNTER, TYPE_DETECTING_STEP_BY_STEP_DETECTOR -> {
                checkActivityRecognitionPermission()
                binding.tvStepCount.visibility = View.VISIBLE
            }

            else -> {
                if (type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR) {
                    sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION))
                }
                if (type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_SENSOR_MANAGER) {
                    sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
                    sensor.add(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))
                }
                binding.mapView.onCreate(savedInstanceState?.getBundle(mapViewBundleKey))
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ) {
                    initMapView()
                } else {
                    requestMultiplePermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR ||
            type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_SENSOR_MANAGER
        ) {
            binding.mapView.onResume()
        }
        registerSensorListener()
    }

    override fun onPause() {
        if (type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_ORIENTATION_SENSOR ||
            type == TYPE_DETECTING_SCREEN_ORIENTATION_BY_SENSOR_MANAGER
        ) {
            binding.mapView.onPause()
        }
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
                    sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_GAME)
                }
            }
        }
    }
}