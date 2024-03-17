package com.chenyihong.exampledemo.scan

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chenyihong.exampledemo.databinding.LayoutDeviceExampleActivityBinding
import com.chenyihong.exampledemo.scan.server.DevicesSocketHelper
import com.chenyihong.exampledemo.scan.server.TEST_LOG
import com.chenyihong.exampledemo.utils.DensityUtil
import com.king.zxing.util.CodeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val EXAMPLE_DEVICE_ID = "example_device_id0001"

class DeviceExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutDeviceExampleActivityBinding

    private var socketHelper: DevicesSocketHelper? = DevicesSocketHelper() { message ->
        Log.i(TEST_LOG, "DeviceExampleActivity receive message:$message")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutDeviceExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            it.includeTitle.tvTitle.text = "Device Example"
        }

        lifecycleScope.launch(Dispatchers.IO) {
            CodeUtils.createQRCode(EXAMPLE_DEVICE_ID, DensityUtil.dp2Px(200)).let { qrCode ->
                withContext(Dispatchers.Main) {
                    binding.ivQrCode.setImageBitmap(qrCode)
                }
            }
        }

        socketHelper?.openSocketConnection("ws://localhost:9090/")
    }

    override fun onDestroy() {
        super.onDestroy()
        socketHelper?.release()
        socketHelper = null
    }
}