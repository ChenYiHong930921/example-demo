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

    private lateinit var binding: LayoutDeviceIdExampleActivityBinding

    private val deviceIdPermissionRequestLauncher = registerForActivityResult(/* contract = */ ActivityResultContracts.RequestPermission()) {
        // 无论是否授权，都进行获取，作对比
        getDeviceID()
    }

    private val serialPermissionRequestLauncher = registerForActivityResult(/* contract = */ ActivityResultContracts.RequestPermission()) {
        // 无论是否授权，都进行获取，作对比
        getSerial()
    }

    @SuppressLint("HardwareIds", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutDeviceIdExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.btnGetDeviceId.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                deviceIdPermissionRequestLauncher.launch(Manifest.permission.READ_PHONE_STATE)
            } else {
                getDeviceID()
            }
        }
        binding.btnGetAndroidId.setOnClickListener {
            binding.tvResultValue.text = "ANDROID_ID:${Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)}"
        }
        binding.btnGetDeviceSerial.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                serialPermissionRequestLauncher.launch(Manifest.permission.READ_PHONE_STATE)
            } else {
                getSerial()
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

    private fun getDeviceID() {
        val telephonyManager = getSystemService(TelephonyManager::class.java)
        val deviceIDStrBuilder = StringBuilder()
        try {
            deviceIDStrBuilder.append("deviceId:").append(telephonyManager.deviceId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                // 设备类型为GSM且有权限获取时不为空
                deviceIDStrBuilder.append("\nIMEI:").append(telephonyManager.imei)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                // 设备类型为CDMA且有权限获取时不为空
                deviceIDStrBuilder.append("\nMEID:").append(telephonyManager.meid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.tvResultValue.text = deviceIDStrBuilder
    }

    private fun getSerial() {
        val serialStrBuilder = StringBuilder()
        try {
            serialStrBuilder.append("Build.SERIAL:").append(Build.SERIAL)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serialStrBuilder.append("\nBuild.getSerial():").append(Build.getSerial())
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }
        binding.tvResultValue.text = serialStrBuilder
    }
}