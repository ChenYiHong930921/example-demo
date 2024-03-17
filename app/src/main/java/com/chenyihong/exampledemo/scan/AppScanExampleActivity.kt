package com.chenyihong.exampledemo.scan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chenyihong.exampledemo.databinding.LayoutAppScanExampleActivityBinding
import com.chenyihong.exampledemo.api.OkHttpHelper
import com.chenyihong.exampledemo.scan.server.DEVICE_ID
import com.chenyihong.exampledemo.scan.server.TEST_LOG
import com.king.camera.scan.CameraScan
import com.chenyihong.exampledemo.api.RequestCallback
import com.chenyihong.exampledemo.scan.server.USER_ID
import okhttp3.ResponseBody

const val EXAMPLE_USER_ID = "123456789"

class AppScanExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutAppScanExampleActivityBinding

    private val scanQRCodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.getStringExtra(CameraScan.SCAN_RESULT)?.let { deviceId ->
                Log.i(TEST_LOG, "scanQRCodeLauncher deviceId:$deviceId")
                sendRequestToServer(deviceId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutAppScanExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            it.btnScan.setOnClickListener {
                scanQRCodeLauncher.launch(Intent(this, ScanQRCodeActivity::class.java))
            }
        }

        OkHttpHelper.init()
    }

    private fun sendRequestToServer(deviceId: String) {
        OkHttpHelper.sendGetRequest("http://192.168.0.106:8080/", mapOf(Pair(USER_ID, EXAMPLE_USER_ID), Pair(DEVICE_ID, deviceId)), object : RequestCallback {
            override fun onResponse(success: Boolean, responseBody: ResponseBody?) {
                Log.i(TEST_LOG, "sendRequestToServer onResponse success:$success, responseBody:$responseBody")
            }

            override fun onFailure(errorMessage: String?) {
                Log.e(TEST_LOG, "sendRequestToServer onFailure errorMessage:$errorMessage")
            }
        })
    }
}