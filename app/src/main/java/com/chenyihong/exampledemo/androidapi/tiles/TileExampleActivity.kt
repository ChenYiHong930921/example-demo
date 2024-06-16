package com.chenyihong.exampledemo.androidapi.tiles

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutTileExmpleActivityBinding
import com.chenyihong.exampledemo.utils.SpUtils

class TileExampleActivity : AppCompatActivity() {

    private var currentStatus = Tile.STATE_ACTIVE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LayoutTileExmpleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        if (!SpUtils.isInit()) {
            SpUtils.init(this)
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
        binding.btnChangeStatus.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                when (currentStatus) {
                    Tile.STATE_ACTIVE -> {
                        currentStatus = Tile.STATE_INACTIVE
                        SpUtils.put("TileStatus", currentStatus)
                    }

                    Tile.STATE_INACTIVE -> {
                        currentStatus = Tile.STATE_UNAVAILABLE
                        SpUtils.put("TileStatus", currentStatus)
                    }

                    Tile.STATE_UNAVAILABLE -> {
                        currentStatus = Tile.STATE_ACTIVE
                        SpUtils.put("TileStatus", currentStatus)
                    }
                }
                TileService.requestListeningState(this, ComponentName(this, ExampleTileService::class.java))
            }
        }
        binding.btnDisableTile.setOnClickListener {
            packageManager.setComponentEnabledSetting(ComponentName(this, ExampleTileService::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
        }
    }
}