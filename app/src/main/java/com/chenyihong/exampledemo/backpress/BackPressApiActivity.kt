package com.chenyihong.exampledemo.backpress

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.adapter.ViewPager2Adapter
import com.chenyihong.exampledemo.databinding.LayoutBackPressApiActivityBinding
import com.chenyihong.exampledemo.gesturedetector.BaseGestureDetectorActivity

const val TAG = "BackPressApi"

class BackPressApiActivity : BaseGestureDetectorActivity() {

    private val canonicalName = this::class.java.canonicalName!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutBackPressApiActivityBinding>(this, R.layout.layout_back_press_api_activity)
        supportFragmentManager.setFragmentResultListener(canonicalName, this) { requestKey, result ->
            Log.i(com.chenyihong.exampledemo.fragmentresultapi.TAG, "Activity receive result requestKey:$requestKey ,result:$result")
            if (requestKey == canonicalName) {
                val resultIndex = result.getInt("result", -1)
                if (resultIndex != -1) {
                    binding.vpContainer.currentItem = resultIndex
                }
            }
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.i(TAG, "BackPressApiActivity OnBackPressedCallback  handleOnBackPressed function invoke")
                finish()
            }
        })
        binding.run {
            btnAFragment.setOnClickListener {
                vpContainer.currentItem = 0
            }
            btnBFragment.setOnClickListener {
                vpContainer.currentItem = 1
            }

            val fragments = ArrayList<Class<out Fragment?>>()
            fragments.add(FragmentA::class.java)
            fragments.add(FragmentB::class.java)

            vpContainer.adapter = ViewPager2Adapter(this@BackPressApiActivity, fragments)
            vpContainer.isUserInputEnabled = false

            vpContainer.currentItem = 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.clearFragmentResultListener(canonicalName)
    }
}