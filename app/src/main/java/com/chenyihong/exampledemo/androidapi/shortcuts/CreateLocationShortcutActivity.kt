package com.chenyihong.exampledemo.androidapi.shortcuts

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gps.GpsSignalActivity
import com.chenyihong.exampledemo.databinding.LayoutCreateShortcutsActivityBinding

class CreateLocationShortcutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LayoutCreateShortcutsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvTips.text = "Do you want to add the Location Launcher icon to your home screen?"
        binding.btnAddShortcut.setOnClickListener {
            createPinShortcuts()
        }
        binding.btnReject.setOnClickListener {
            finish()
        }
    }

    private fun createPinShortcuts() {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
            val pinShortcutInfo = ShortcutInfoCompat.Builder(this, "location")
                .setShortLabel(getString(R.string.location_shortcuts_short_label))
                .setLongLabel(getString(R.string.location_shortcuts_long_label))
                .setDisabledMessage(getString(R.string.shortcuts_disable_message))
                .setIcon(IconCompat.createWithResource(this, R.drawable.icon_location))
                .setIntent(Intent(Intent.ACTION_VIEW).apply {
                    component = ComponentName(packageName, GpsSignalActivity::class.java.name)
                })
                .build()
            val pinnedShortcutCallbackIntent = ShortcutManagerCompat.createShortcutResultIntent(this, pinShortcutInfo)
            setResult(RESULT_OK, pinnedShortcutCallbackIntent)
            finish()
        }
    }
}