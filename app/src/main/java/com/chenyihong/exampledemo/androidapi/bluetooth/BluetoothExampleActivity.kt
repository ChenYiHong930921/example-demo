package com.chenyihong.exampledemo.androidapi.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutBluetoothExampleActivityBinding

class BluetoothExampleActivity : BaseGestureDetectorActivity() {

    private val bluetoothAdapter = BluetoothAdapter()

    private var requestPermissionName = arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private val requestSinglePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantPermission ->

    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutBluetoothExampleActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_bluetooth_example_activity)
        binding.includeTitle.tvTitle.text = "BluetoothExample"
        binding.btnScanNearbyDevices.setOnClickListener {

        }
    }
}