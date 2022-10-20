package com.chenyihong.exampledemo.androidapi.shortcuts

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.camerax.CameraActivity
import com.chenyihong.exampledemo.androidapi.gps.GpsSignalActivity
import com.chenyihong.exampledemo.databinding.LayoutCreateShortcutsActivityBinding

class CreateCameraShortcutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LayoutCreateShortcutsActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_create_shortcuts_activity)
        binding.tvTips.text = "Do you want to add the Camera Launcher icon to your home screen?"
        binding.btnAddShortcut.setOnClickListener {
            createPinShortcuts()
        }
        binding.btnReject.setOnClickListener {
            finish()
        }
    }

    private fun createPinShortcuts() {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
            val pinShortcutInfo = ShortcutInfoCompat.Builder(this, "camera")
                .setShortLabel(getString(R.string.camera_shortcuts_short_label))
                .setLongLabel(getString(R.string.camera_shortcuts_long_label))
                .setDisabledMessage(getString(R.string.shortcuts_disable_message))
                .setIcon(IconCompat.createWithResource(this, R.drawable.icon_camera))
                .setIntent(Intent(Intent.ACTION_VIEW).apply {
                    component = ComponentName(packageName, CameraActivity::class.java.name)
                })
                .build()
            val pinnedShortcutCallbackIntent = ShortcutManagerCompat.createShortcutResultIntent(this, pinShortcutInfo)
            setResult(RESULT_OK, pinnedShortcutCallbackIntent)
            finish()
        }
    }
}