package com.chenyihong.exampledemo.androidapi.language

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutChangeLanguageActivityBinding
import java.util.Locale

class ChangeLanguageActivity : BaseGestureDetectorActivity<LayoutChangeLanguageActivityBinding>() {

    private val availableLanguage = arrayOf("zh", "en", "ru", "ko", "ja")
    private val availableLanguageDisplayName = arrayOf("Chinese", "English", "Russian", "korean", "Japanese")

    private var currentLanguage = "zh"

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutChangeLanguageActivityBinding {
        return LayoutChangeLanguageActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 获取当前的语言
        currentLanguage = if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            Locale.getDefault().toLanguageTag()
        } else {
            AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() ?: ""
        }
        binding.includeTitle.tvTitle.text = "Change Language Example"
        binding.btnChangeLanguage.setOnClickListener {
            val currentIndex = availableLanguage.indexOf(currentLanguage)
            AlertDialog.Builder(this)
                .setTitle("Chose Language")
                .setSingleChoiceItems(availableLanguageDisplayName, if (currentIndex == -1) 0 else currentIndex) { _, which ->
                    currentLanguage = availableLanguage[which]
                }
                .setPositiveButton("ok") { dialog, _ ->
                    // 切换语言
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(currentLanguage))
                    dialog?.dismiss()
                }
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog?.dismiss()
                }
                .create()
                .show()
        }
    }
}