package com.chenyihong.exampledemo.androidapi.tiles

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.service.quicksettings.TileService
import com.chenyihong.exampledemo.androidapi.camerax.CameraActivity
import com.chenyihong.exampledemo.utils.SpUtils

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
        if (isSecure) {
            handleClick()
        } else {
            unlockAndRun { handleClick() }
        }

    }

    override fun onStartListening() {
        super.onStartListening()
        // 系统开始监听Tile的状态、内容时调用此方法

        // 此处使用SharedPreference记录要切换的状态
        if (!SpUtils.isInit()) {
            SpUtils.init(this)
        }
        val nextState = SpUtils.getInt("TileStatus", -1)
        if (nextState != -1) {
            // 除了更新state之外
            // 还可以更新Tile的Icon、Label、SubTitle等，根据实际需求选用
            qsTile.state = nextState
            // 调用update方法后设置才会生效
            qsTile.updateTile()
        }
    }

    override fun onStopListening() {
        super.onStopListening()
        // 系统结束监听Tile的状态、内容时调用此方法
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    private fun handleClick() {
//        showDialog(AlertDialog.Builder(this)
//            .setTitle("Tile Example")
//            .setMessage("This is a tile example dialog")
//            .setPositiveButton("ok") { dialog, _ ->
//                dialog?.dismiss()
//            }
//            .create())

        // 此方法提示过时，但是在实测中，我的Google Pixel 3a XL只能使用此方法
        // 使用下面那个方法会报错。
        startActivityAndCollapse(Intent(this, CameraActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//        // 此方法为当前推荐使用的方法。
//        // 实测小米14可以通过此方法打开页面。
//        startActivityAndCollapse(PendingIntent.getActivity(this, this.hashCode(), Intent(this, CameraActivity::class.java), PendingIntent.FLAG_IMMUTABLE))
    }
}