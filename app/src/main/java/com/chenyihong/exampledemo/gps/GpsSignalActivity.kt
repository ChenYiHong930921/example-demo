package com.chenyihong.exampledemo.gps

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.GnssStatusCompat
import androidx.core.location.LocationManagerCompat
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutGpsSignalActivityBinding

const val TAG = "GpsSignal"

class GpsSignalActivity : AppCompatActivity() {

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
    private val locationListener = LocationListener {
        Log.i(TAG, "receiver callback location$it")
    }
    private val gnssStatusListener = object : GnssStatusCompat.Callback() {
        @SuppressLint("SetTextI18n")
        override fun onSatelliteStatusChanged(status: GnssStatusCompat) {
            super.onSatelliteStatusChanged(status)
            val satelliteCount = status.satelliteCount
            Log.i(TAG, "satelliteCount:$satelliteCount")
            var availableSatelliteCount = 0
            for (index in 0 until satelliteCount) {
                val cn0DbHz = status.getCn0DbHz(index)
                val basebandCn0DbHz = status.getBasebandCn0DbHz(index)
                Log.i(TAG, "cn0DbHz:$cn0DbHz")
                Log.i(TAG, "basebandCn0DbHz:$basebandCn0DbHz")
                if (cn0DbHz >= 37) {
                    availableSatelliteCount++
                }
            }
            Log.i(TAG, "availableSatelliteCount:$availableSatelliteCount")
            binding.tvTotalSatelliteCount.run { post { text = "total satellite count ：$satelliteCount" } }
            binding.tvAvailableSatelliteCount.run { post { text = "available satellite count ：$availableSatelliteCount" } }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_gps_signal_activity)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.btnDetect.setOnClickListener {
            checkLocationPermission()
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5.toFloat(), locationListener)
            }
        }
        binding.btnStopDetect.setOnClickListener {
            locationManager.removeUpdates(locationListener)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            registerGnssStatusListener()
        } else {
            requestMultiplePermissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
            return
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