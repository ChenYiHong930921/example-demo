package com.chenyihong.exampledemo.androidapi.language

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutChangeLanguageActivityBinding
import org.xmlpull.v1.XmlPullParser
import java.util.Locale

class ChangeLanguageActivity : BaseGestureDetectorActivity<LayoutChangeLanguageActivityBinding>() {

    private var currentLanguage = ""

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
        val supportLanguageTag = ArrayList<String>()
        val supportLanguageDisplayName = ArrayList<String>()
        resources.getXml(R.xml.locale_config).run {
            var eventType = eventType
            while (XmlPullParser.END_DOCUMENT != eventType) {
                if (XmlPullParser.START_TAG == eventType) {
                    if ("locale" == name) {
                        val languageTag = getAttributeValue(0)
                        supportLanguageTag.add(languageTag)
                        supportLanguageDisplayName.add(Locale.forLanguageTag(languageTag).getDisplayName(Locale.forLanguageTag(currentLanguage)))
                    }
                }
                eventType = next()
            }
        }
        Log.i("-,-,-", "init supportLanguageDisplayName:$supportLanguageDisplayName")
        binding.includeTitle.tvTitle.text = "Change Language Example"
        binding.btnChangeLanguage.setOnClickListener {
            val currentIndex = supportLanguageTag.indexOf(supportLanguageTag.find { currentLanguage.contains(it) })
            AlertDialog.Builder(this)
                .setTitle("Chose Language")
                .setSingleChoiceItems(supportLanguageDisplayName.toArray(emptyArray()), if (currentIndex == -1) 0 else currentIndex) { _, which ->
                    currentLanguage = supportLanguageTag[which]
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