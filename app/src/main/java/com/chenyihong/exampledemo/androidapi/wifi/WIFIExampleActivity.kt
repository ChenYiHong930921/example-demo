package com.chenyihong.exampledemo.androidapi.wifi

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutWifiExampleActivityBinding
import com.chenyihong.exampledemo.web.PARAMS_LINK_URL
import com.chenyihong.exampledemo.web.WebViewActivity
import com.chenyihong.exampledemo.web.customtab.CustomTabHelper
import java.util.*

class WIFIExampleActivity : BaseGestureDetectorActivity<LayoutWifiExampleActivityBinding>() {

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
                    wifiData.add(WIFIEntity(ssid, bssid, capabilities.contains("wpa", true) || capabilities.contains("wep", true), capabilities, level))
                }
                // 根据信号强度降序排列
                wifiData.sortByDescending { it.wifiStrength }
                wifiAdapter.setNewData(wifiData)
            }
        }
    }

    private var passwordEditText: AppCompatEditText? = null
    private var inputWIFIPasswordDialog: AlertDialog? = null

    private var connectivityManager: ConnectivityManager? = null
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            connectivityManager?.run {
                if (boundNetworkForProcess != network) {
                    if (boundNetworkForProcess != network) {
                        bindProcessToNetwork(network)
                    }
                }
            }
        }
    }

    private var suggestionConnect = true

    private var innerWebView = true

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutWifiExampleActivityBinding {
        return LayoutWifiExampleActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
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
        binding.btnOpenWebsite.setOnClickListener {
            if (innerWebView) {
                startActivity(Intent(this, WebViewActivity::class.java).apply {
                    putExtra(PARAMS_LINK_URL, "https://go.minigame.vip/")
                })
            } else {
                CustomTabHelper.openCustomTabWithInitialHeight(this, "https://go.minigame.vip/")
            }
        }
        wifiAdapter.itemClickListener = object : WIFIAdapter.ItemClickListener {
            override fun onItemClick(wifiInfo: WIFIEntity) {
                if (wifiInfo.capabilities.contains("wpa", true) || wifiInfo.capabilities.contains("wep", true)) {
                    showInputWIFIPasswordDialog(wifiInfo)
                } else {
                    connectWIFIWithoutPassword(wifiInfo)
                }
            }
        }
        binding.rvWifiInfo.adapter = wifiAdapter
        // 注册广播
        registerReceiver(scanResultReceiver, IntentFilter().apply {
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        })
    }

    @SuppressLint("InflateParams")
    private fun showInputWIFIPasswordDialog(wifiInfo: WIFIEntity) {
        val dialogView = layoutInflater.inflate(R.layout.layout_wifi_input_password_dialog, null)
        passwordEditText = dialogView.findViewById(R.id.et_input_wifi_password)
        inputWIFIPasswordDialog = AlertDialog.Builder(this)
            .setTitle(wifiInfo.wifiSSID)
            .setView(dialogView)
            .setPositiveButton("ok", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    passwordEditText?.text?.toString()?.let {
                        connectSelectedWIFIByPassword(wifiInfo, it)
                    }
                    dialog?.dismiss()
                    inputWIFIPasswordDialog = null
                    passwordEditText = null
                }
            })
            .setNegativeButton("cancel", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                    inputWIFIPasswordDialog = null
                    passwordEditText = null
                }
            })
            .create()
        inputWIFIPasswordDialog?.setOnDismissListener {
            passwordEditText?.text?.clear()
        }
        inputWIFIPasswordDialog?.show()
    }

    private fun connectSelectedWIFIByPassword(wifiInfo: WIFIEntity, password: String) {
        when {
            wifiInfo.capabilities.contains("wpa", true) -> {
                // 加密类型为WPA、WPA2P、WPA3
                connectWIFIWithWPAPassword(wifiInfo, password)
            }

            wifiInfo.capabilities.contains("wep", true) -> {
                // 加密类型为WEP，已过时
                connectWIFIWithWEPPassword(wifiInfo, password)
            }
        }
    }

    private fun connectWIFIWithWPAPassword(wifiInfo: WIFIEntity, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (suggestionConnect) {
                val suggestionBuilder = WifiNetworkSuggestion.Builder()
                    .setSsid(wifiInfo.wifiSSID)
                    .setBssid(MacAddress.fromString(wifiInfo.wifiBSSID))
                if (wifiInfo.capabilities.contains("wpa3", true)) {
                    suggestionBuilder.setWpa3Passphrase(password)
                } else {
                    suggestionBuilder.setWpa2Passphrase(password)
                }
                wifiManager?.addNetworkSuggestions(listOf(suggestionBuilder.build()))
            } else {
                val specifierBuilder = WifiNetworkSpecifier.Builder()
                    .setSsid(wifiInfo.wifiSSID)
                    .setBssid(MacAddress.fromString(wifiInfo.wifiBSSID))
                if (wifiInfo.capabilities.contains("wpa3", true)) {
                    specifierBuilder.setWpa3Passphrase(password)
                } else {
                    specifierBuilder.setWpa2Passphrase(password)
                }
                val request = NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .setNetworkSpecifier(specifierBuilder.build())
                    .build()
                connectivityManager?.requestNetwork(request, networkCallback)
            }
        } else {
            val wifiConfig = WifiConfiguration()
            wifiConfig.SSID = "\"${wifiInfo.wifiSSID}\""
            wifiConfig.preSharedKey = "\"$password\""
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            wifiManager?.run {
                enableNetwork(addNetwork(wifiConfig), true)
            }
        }
    }

    private fun connectWIFIWithWEPPassword(wifiInfo: WIFIEntity, password: String) {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "\"${wifiInfo.wifiSSID}\""
        wifiConfig.wepKeys[0] = "\"$password\""
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
        wifiManager?.run {
            enableNetwork(addNetwork(wifiConfig), true)
        }
    }

    private fun connectWIFIWithoutPassword(wifiInfo: WIFIEntity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (suggestionConnect) {
                val suggestion = WifiNetworkSuggestion.Builder()
                    .setSsid(wifiInfo.wifiSSID)
                    .setBssid(MacAddress.fromString(wifiInfo.wifiBSSID))
                    .build()
                wifiManager?.addNetworkSuggestions(listOf(suggestion))
            } else {
                val specifier = WifiNetworkSpecifier.Builder()
                    .setSsid(wifiInfo.wifiSSID)
                    .setBssid(MacAddress.fromString(wifiInfo.wifiBSSID))
                    .build()
                val request = NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .setNetworkSpecifier(specifier)
                    .build()
                connectivityManager?.requestNetwork(request, networkCallback)
            }
        } else {
            val wifiConfig = WifiConfiguration()
            wifiConfig.SSID = "\"${wifiInfo.wifiSSID}\""
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            wifiManager?.run {
                enableNetwork(addNetwork(wifiConfig), true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除广播
        unregisterReceiver(scanResultReceiver)
        connectivityManager?.run {
            bindProcessToNetwork(null)
            try {
                unregisterNetworkCallback(networkCallback)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (inputWIFIPasswordDialog?.isShowing == true) {
            inputWIFIPasswordDialog?.dismiss()
        }
    }
}