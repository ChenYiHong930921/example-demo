package com.chenyihong.exampledemo.androidapi.wifi

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutWifiExampleActivityBinding

const val TAG = "WIFIExampleTag"

class WIFIExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutWifiExampleActivityBinding

    private val wifiAdapter = WIFIAdapter()

    private var wifiManager: WifiManager? = null

    private var requestPermissionName: String = Manifest.permission.ACCESS_FINE_LOCATION

    private val requestSinglePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted: Boolean ->
        if (granted) {
            // 申请定位权限通过，扫描WIFI
            if (wifiManager?.isWifiEnabled == true) {
                wifiManager?.startScan()
            }
        } else {
            //未同意授权
            if (!shouldShowRequestPermissionRationale(requestPermissionName)) {
                //用户拒绝权限并且系统不再弹出请求权限的弹窗
                //这时需要我们自己处理，比如自定义弹窗告知用户为何必须要申请这个权限
            }
        }
    }

    private val scanResultReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) == true) {
                // 扫描完成
                val wifiData = ArrayList<WIFIEntity>()
                wifiManager?.scanResults?.forEach {
                    val ssid = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.wifiSsid.toString()
                    } else {
                        it.SSID
                    }
                    val bssid = it.BSSID
                    // 获取WIFI加密类型
                    val capabilities = it.capabilities
                    // 获取WIFI信号强度
                    val level = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        wifiManager?.calculateSignalLevel(it.level) ?: 0
                    } else {
                        WifiManager.calculateSignalLevel(it.level, 4)
                    }
                    wifiData.add(WIFIEntity(ssid, bssid, capabilities.contains("wpa", true) || capabilities.contains("web", true), capabilities, level))
                }
                // 根据信号强度降序排列
                wifiData.sortByDescending { it.wifiStrength }
                wifiAdapter.setNewData(wifiData)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_wifi_example_activity)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        binding.includeTitle.tvTitle.text = "WIFI Example"
        binding.btnStartScan.setOnClickListener {
            // 检测定位权限
            if (ActivityCompat.checkSelfPermission(this, requestPermissionName) == PackageManager.PERMISSION_GRANTED) {
                if (wifiManager?.isWifiEnabled == true) {
                    wifiManager?.startScan()
                }
            } else {
                requestSinglePermissionLauncher.launch(requestPermissionName)
            }
        }
        wifiAdapter.itemClickListener = object : WIFIAdapter.ItemClickListener {
            override fun onItemClick(wifiInfo: WIFIEntity) {
                // TODO: 连接wifi
            }
        }
        binding.rvWifiInfo.adapter = wifiAdapter
        // 注册广播
        registerReceiver(scanResultReceiver, IntentFilter().apply {
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除广播
        unregisterReceiver(scanResultReceiver)
    }
}