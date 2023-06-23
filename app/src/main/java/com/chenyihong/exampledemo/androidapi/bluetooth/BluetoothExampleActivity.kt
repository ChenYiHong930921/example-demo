package com.chenyihong.exampledemo.androidapi.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutBluetoothExampleActivityBinding

class BluetoothExampleActivity : BaseGestureDetectorActivity() {

    private var bluetoothAdapter: BluetoothAdapter? = null

    private val bluetoothDevicesAdapter = BluetoothDevicesAdapter()

    private var requestPermissionNames = ArrayList<String>()

    private val requestMultiplePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
        val noGrantedPermissions = ArrayList<String>()
        permissions.entries.forEach {
            if (!it.value) {
                noGrantedPermissions.add(it.key)
            }
        }
        if (noGrantedPermissions.isEmpty()) {
            // 申请权限通过，扫描蓝牙设备
            scanBluetoothDevices()
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

    private val intentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            checkPermission()
        }
    }

    private val scanResultReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    bluetoothDevicesAdapter.addSingleData(device)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutBluetoothExampleActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_bluetooth_example_activity)
        binding.includeTitle.tvTitle.text = "BluetoothExample"
        bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        // 注册蓝牙设备扫描结果监听
        registerReceiver(scanResultReceiver, IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
        })
        binding.btnScanNearbyDevices.setOnClickListener {
            if (!bluetoothCapabilityEnable()) {
                // 当前设备蓝牙功能不可用
                return@setOnClickListener
            }
            checkPermission()
        }
        binding.rvBluetoothInfo.adapter = bluetoothDevicesAdapter
        bluetoothDevicesAdapter.itemClickListener = object : BluetoothDevicesAdapter.ItemClickListener {
            @SuppressLint("MissingPermission")
            override fun onItemClick(bluetoothInfo: BluetoothDevice) {
                //绑定设备
                bluetoothInfo.createBond()
            }
        }
    }

    private fun bluetoothCapabilityEnable(): Boolean {
        return bluetoothAdapter != null
    }

    private fun checkPermission() {
        // 检测权限，Android S及以上检测BLUETOOTH_SCAN权限，反之检测定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionNames.add(Manifest.permission.BLUETOOTH_SCAN)
            requestPermissionNames.add(Manifest.permission.BLUETOOTH_CONNECT)
            requestPermissionNames.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        } else {
            requestPermissionNames.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (requestPermissionNames.find { ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED } != null) {
            requestMultiplePermissionLauncher.launch(requestPermissionNames.toArray(arrayOfNulls(0)))
        } else {
            scanBluetoothDevices()
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanBluetoothDevices() {
        if (bluetoothAdapter?.isEnabled == true) {
            bluetoothDevicesAdapter.setNewData(bluetoothAdapter?.bondedDevices?.toList() as? ArrayList<BluetoothDevice>)
            // 开始扫描
            bluetoothAdapter?.startDiscovery()
        } else {
            // 蓝牙未开启，通过系统启用蓝牙
            intentLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消扫描结果监听
        unregisterReceiver(scanResultReceiver)
    }
}