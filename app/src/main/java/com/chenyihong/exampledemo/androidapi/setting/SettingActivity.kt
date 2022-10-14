package com.chenyihong.exampledemo.androidapi.setting

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutSettingActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

const val TAG = "PreferenceApi"

class SettingActivity : BaseGestureDetectorActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private lateinit var binding: LayoutSettingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_setting_activity)
        ExampleDataStore.lifecycleScope = lifecycleScope
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