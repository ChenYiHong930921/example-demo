package com.chenyihong.exampledemo.androidapi.gps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener

import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.location.GnssStatusCompat
import androidx.core.location.LocationManagerCompat
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutGpsSignalActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import java.text.SimpleDateFormat
import java.util.Calendar

const val TAG = "GpsSignal"

class GpsSignalActivity : BaseGestureDetectorActivity() {

    private lateinit var binding: LayoutGpsSignalActivityBinding
    private lateinit var locationManager: LocationManager

    private val requestMultiplePermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
        var locationPermissionAllGranted = true
        permissions.entries.forEach {
            val permissionName = it.key
            if (!it.value) {
                locationPermissionAllGranted = false
                Log.d(TAG, "$permissionName not granted")
                //未同意授权
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(permissionName)) {
                        //用户拒绝权限并且系统不再弹出请求权限的弹窗
                        //这时需要我们自己处理，比如自定义弹窗告知用户为何必须要申请这个权限
                        Log.d(TAG, "$permissionName not granted and should not show rationale")
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        if (locationPermissionAllGranted) {
            registerGnssStatusListener()
        }
    }

    private var gnssStatusListenerAdded: Boolean = false

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private val locationListener = LocationListener {
        Log.i(TAG, "receiver callback location$it")
        binding.tvLocation.run { post { text = "time ：${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)}\nlongitude ：${it.longitude}，latitude ：${it.latitude}" } }
    }
    private val gnssStatusListener = object : GnssStatusCompat.Callback() {
        @SuppressLint("SetTextI18n")
        override fun onSatelliteStatusChanged(status: GnssStatusCompat) {
            super.onSatelliteStatusChanged(status)
            val satelliteCount = status.satelliteCount
            Log.i(TAG, "satelliteCount:$satelliteCount")
            var cn0DbHz30SatelliteCount = 0
            var cn0DbHz37SatelliteCount = 0
            var satelliteInfo = ""
            for (index in 0 until satelliteCount) {
                val cn0DbHz = status.getCn0DbHz(index)
                satelliteInfo += "svid:${status.getSvid(index)},cn0DbHz:$cn0DbHz\n"
                if (cn0DbHz >= 30) {
                    cn0DbHz30SatelliteCount++
                }
                if (cn0DbHz >= 37) {
                    cn0DbHz37SatelliteCount++
                }
            }
            binding.tvTotalSatelliteCount.run { post { text = "total satellite count ：$satelliteCount" } }
            binding.tvAvailableSatelliteCount.run { post { text = "cno >37 count ：$cn0DbHz37SatelliteCount\ncno >30 count ：$cn0DbHz30SatelliteCount" } }
            binding.tvSatelliteInfo.run { post { text = "satellite info ：\n$satelliteInfo" } }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_gps_signal_activity)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.includeTitle.tvTitle.text = "GpsSignal Api"
        binding.btnDetect.setOnClickListener {
            if (checkLocationPermission()) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5.toFloat(), locationListener)
                }
            }
        }
        binding.btnStopDetect.setOnClickListener {
            locationManager.removeUpdates(locationListener)
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            registerGnssStatusListener()
            return true
        } else {
            requestMultiplePermissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
            return false
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerGnssStatusListener() {
        Log.i(TAG, "gnssStatusListenerAdded:$gnssStatusListenerAdded")
        if (!gnssStatusListenerAdded) {
            gnssStatusListenerAdded = LocationManagerCompat.registerGnssStatusCallback(locationManager, gnssStatusListener, Handler(Looper.myLooper()
                ?: Looper.getMainLooper()))
            Log.i(TAG, "gnssStatusListenerAdded:$gnssStatusListenerAdded")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (gnssStatusListenerAdded) {
            LocationManagerCompat.unregisterGnssStatusCallback(locationManager, gnssStatusListener)
        }
    }
}