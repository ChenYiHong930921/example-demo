package com.chenyihong.exampledemo.scan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chenyihong.exampledemo.databinding.LayoutScanLoginExampleActivityBinding
import com.chenyihong.exampledemo.scan.server.ServerController

class ScanLoginExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutScanLoginExampleActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutScanLoginExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            ServerController.startServer()
            it.includeTitle.tvTitle.text = "Scan Login Example"
            it.btnOpenDeviceExample.setOnClickListener { startActivity(Intent(this, DeviceExampleActivity::class.java)) }
            it.btnOpenAppExample.setOnClickListener { startActivity(Intent(this, AppScanExampleActivity::class.java)) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ServerController.stopServer()
    }
}