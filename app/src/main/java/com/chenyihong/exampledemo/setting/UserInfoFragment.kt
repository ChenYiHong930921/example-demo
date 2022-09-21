package com.chenyihong.exampledemo.setting

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chenyihong.exampledemo.R

class UserInfoFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.example_user_info, rootKey)
        findPreference<EditTextPreference>("user_info_nick_name")?.summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
            preference.text ?: "Please enter a nickname"
        }
        findPreference<EditTextPreference>("user_info_real_name")?.summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
            preference.text ?: "Please enter real name"
        }
        findPreference<EditTextPreference>("user_info_age")?.run {
            summaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
                preference.text ?: "Please enter your age"
            }
            setOnBindEditTextListener { it.inputType = InputType.TYPE_CLASS_NUMBER }
        }
    }
}