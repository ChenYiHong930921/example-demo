package com.chenyihong.exampledemo.androidapi.setting

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutSettingActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

const val TAG = "PreferenceApi"

class SettingActivity : BaseGestureDetectorActivity<LayoutSettingActivityBinding>(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutSettingActivityBinding {
        return LayoutSettingActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExampleDataStore.coroutineScope = lifecycleScope
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.ct_setting_container, SettingFragment())
            .commitAllowingStateLoss()
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        pref.fragment?.let {
            val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, it)
            supportFragmentManager.beginTransaction()
                .replace(R.id.ct_setting_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
        return true
    }
}