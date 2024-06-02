package com.chenyihong.exampledemo.androidapi.tiles

import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutTileExmpleActivityBinding

class TileExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LayoutTileExmpleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        val statusBarManager: StatusBarManager? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSystemService(StatusBarManager::class.java)
        } else {
            null
        }
        binding.btnAddTile.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                statusBarManager?.requestAddTileService(
                    ComponentName(this, ExampleTileService::class.java),
                    getString(R.string.label_tile_example_scan),
                    Icon.createWithResource(this, R.drawable.icon_scan),
                    ContextCompat.getMainExecutor(this),
                ) { code ->
                    // 添加事件回调，返回下列状态码之一
                    // TILE_ADD_REQUEST_RESULT_TILE_NOT_ADDED（未添加成功）
                    // TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED （之前已经添加过）
                    // TILE_ADD_REQUEST_RESULT_TILE_ADDED（添加成功）
                }
            }
        }
    }
}