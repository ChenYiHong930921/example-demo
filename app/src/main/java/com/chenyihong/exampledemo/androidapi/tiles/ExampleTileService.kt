package com.chenyihong.exampledemo.androidapi.tiles

import android.annotation.SuppressLint
import android.service.quicksettings.TileService

@SuppressLint("NewApi")
class ExampleTileService : TileService() {

    override fun onTileAdded() {
        super.onTileAdded()
        // 当用户添加App提供的Tile时调用此方法
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        // 当用户移除App提供的Tile时调用此方法
    }

    override fun onClick() {
        super.onClick()
        // 用户在下拉栏中点击Tile时调用此方法
    }

    override fun onStartListening() {
        super.onStartListening()
        // 系统开始监听Tile的状态、内容时调用此方法
    }

    override fun onStopListening() {
        super.onStopListening()
        // 系统结束监听Tile的状态、内容时调用此方法
    }
}