package com.chenyihong.exampledemo.scan.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chenyihong.exampledemo.databinding.LayoutAppScanExampleActivityBinding
import com.chenyihong.exampledemo.api.OkHttpHelper
import com.chenyihong.exampledemo.scan.server.DEVICE_ID
import com.king.camera.scan.CameraScan
import com.chenyihong.exampledemo.api.RequestCallback
import com.chenyihong.exampledemo.scan.server.APP_SCAN_INTERFACE
import com.chenyihong.exampledemo.scan.server.EXAMPLE_USER_ID
import com.chenyihong.exampledemo.scan.server.USER_ID
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody

class AppScanExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutAppScanExampleActivityBinding

    private var serverIp: String = ""

    private val scanQRCodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.getStringExtra(CameraScan.SCAN_RESULT)?.let { deviceId ->
                sendRequestToServer(deviceId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutAppScanExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        OkHttpHelper.init()

        binding.btnScan.setOnClickListener {
            // 获取输入的服务端ip（两台设备在同一WIFI下，直接通过IP访问服务端）
            serverIp = binding.etInputIp.text.toString()
            if (serverIp.isEmpty()) {
                showSnakeBar("Server ip can not be empty")
                return@setOnClickListener
            }
            scanQRCodeLauncher.launch(Intent(this, ScanQRCodeActivity::class.java))
        }
    }

    private fun sendRequestToServer(deviceId: String) {
        OkHttpHelper.sendGetRequest("http://${serverIp}:8080/${APP_SCAN_INTERFACE}", mapOf(Pair(USER_ID, EXAMPLE_USER_ID), Pair(DEVICE_ID, deviceId)), object : RequestCallback {
            override fun onResponse(success: Boolean, responseBody: ResponseBody?) {
                showSnakeBar("Scan login ${if (success) "succeed" else "failure"}")
            }

            override fun onFailure(errorMessage: String?) {
                showSnakeBar("Scan login failure")
            }
        })
    }

    private fun showSnakeBar(message: String) {
        runOnUiThread {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}