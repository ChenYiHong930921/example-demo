package com.chenyihong.exampledemo.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.chenyihong.exampledemo.R

class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.example_setting, rootKey)
        preferenceManager.preferenceDataStore = ExampleDataStore
        val notificationOpenStatus = findPreference<SwitchPreferenceCompat>("notifications_open_status")
        val autoLoginStatus = findPreference<SwitchPreferenceCompat>("auto_login_enable")
        val googleAccountStatus = findPreference<SwitchPreferenceCompat>("google_account_bind_status")
        val facebookAccountStatus = findPreference<SwitchPreferenceCompat>("facebook_account_bind_status")
        findPreference<Preference>("system_system")?.setOnPreferenceClickListener {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            true
        }

        val notificationOpenStatusValue = ExampleDataStore.getBoolean("notifications_open_status", false)
        val autoLoginStatusValue = ExampleDataStore.getBoolean("auto_login_enable", false)
        val googleAccountStatusValue = ExampleDataStore.getBoolean("google_account_bind_status", false)
        val facebookAccountStatusValue = ExampleDataStore.getBoolean("facebook_account_bind_status", false)

        notificationOpenStatus?.isChecked = notificationOpenStatusValue
        autoLoginStatus?.isChecked = autoLoginStatusValue
        googleAccountStatus?.run {
            isChecked = googleAccountStatusValue
            onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
                googleAccountStatus.summaryOn = "xxxxx@gmail.com"
                true
            }
        }
        facebookAccountStatus?.run {
            isChecked = facebookAccountStatusValue
            onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
                facebookAccountStatus.summaryOn = "xxxxxxfbaccount"
                true
            }
        }
    }
}