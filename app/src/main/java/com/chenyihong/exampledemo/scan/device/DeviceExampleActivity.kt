package com.chenyihong.exampledemo.scan.device

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chenyihong.exampledemo.databinding.LayoutDeviceExampleActivityBinding
import com.chenyihong.exampledemo.scan.server.EXAMPLE_DEVICE_ID
import com.chenyihong.exampledemo.utils.DensityUtil
import com.king.zxing.util.CodeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceExampleActivity : AppCompatActivity() {

    private lateinit var binding: LayoutDeviceExampleActivityBinding

    private var socketHelper: DevicesSocketHelper? = DevicesSocketHelper() { message ->
        // 接收到服务端发来的消息，改变显示内容
        runOnUiThread {
            binding.tvUserInfo.text = message
            binding.ivQrCode.visibility = View.GONE
            binding.tvUserInfo.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutDeviceExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            it.includeTitle.tvTitle.text = "Device Example"
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // 使用设备id生成二维码
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