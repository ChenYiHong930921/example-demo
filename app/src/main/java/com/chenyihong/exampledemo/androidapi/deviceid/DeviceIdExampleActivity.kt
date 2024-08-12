package com.chenyihong.exampledemo.androidapi.deviceid

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.chenyihong.exampledemo.databinding.LayoutDeviceIdExampleActivityBinding
import java.net.NetworkInterface

class DeviceIdExampleActivity : AppCompatActivity() {

    private val singlePermissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    @SuppressLint("HardwareIds", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LayoutDeviceIdExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            singlePermissionRequestLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }
        val telephonyManager = getSystemService(TelephonyManager::class.java)
        binding.btnGetDeviceId.setOnClickListener {
            val deviceIDStrBuilder = StringBuilder()
            try {
                deviceIDStrBuilder.append("deviceId:").append(telephonyManager.deviceId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    deviceIDStrBuilder.append("\nimei:").append(telephonyManager.imei)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    deviceIDStrBuilder.append("\nmeid:").append(telephonyManager.meid)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.tvResultValue.text = deviceIDStrBuilder
        }
        binding.btnGetAndroidId.setOnClickListener {
            binding.tvResultValue.text = "ANDROID_ID:${Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)}"
        }
        binding.btnGetDeviceSerial.setOnClickListener {
            // 设备序列号 10.0开始无法获取
            var deviceSerial = ""
            try {
                Build.SERIAL?.takeIf { !it.equals("UNKNOWN", true) }?.let { deviceSerial = it }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    deviceSerial = Build.getSerial()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvResultValue.text = "get device serial failed due to ${e.message}"
            }
            if (deviceSerial.isNotEmpty()) {
                binding.tvResultValue.text = "deviceSerial:$deviceSerial"
            }
        }
        binding.btnGetMacAddress.setOnClickListener {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val network = networkInterfaces.nextElement()
                if (network.name == "wlan0") {
                    binding.tvResultValue.text = "macAddress:${network.hardwareAddress?.joinToString(":") { macAddress -> String.format("%02X", macAddress) }}"
                    break
                }
            }
        }
    }
}