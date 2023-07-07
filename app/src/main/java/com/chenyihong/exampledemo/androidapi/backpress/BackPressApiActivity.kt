package com.chenyihong.exampledemo.androidapi.backpress

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.chenyihong.exampledemo.adapter.ViewPager2Adapter
import com.chenyihong.exampledemo.databinding.LayoutBackPressApiActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

const val TAG = "BackPressApi"

class BackPressApiActivity : BaseGestureDetectorActivity<LayoutBackPressApiActivityBinding>() {

    private val canonicalName = this::class.java.canonicalName!!

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutBackPressApiActivityBinding {
        return LayoutBackPressApiActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.setFragmentResultListener(canonicalName, this) { requestKey, result ->
            Log.i(com.chenyihong.exampledemo.androidapi.fragmentresultapi.TAG, "Activity receive result requestKey:$requestKey ,result:$result")
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
            includeTitle.tvTitle.text = "BackPress Api"
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