package com.chenyihong.exampledemo.fragmentresultapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutFragmentResultApiActivityBinding
import com.chenyihong.exampledemo.fragmentresultapi.adapter.ViewPager2Adapter

const val TAG = "FragmentResultAPI"

class FragmentResultApiActivity : FragmentActivity() {

    private val canonicalName = this::class.java.canonicalName!!

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LayoutFragmentResultApiActivityBinding>(this, R.layout.layout_fragment_result_api_activity)

        supportFragmentManager.setFragmentResultListener(canonicalName, this) { requestKey, result ->
            Log.i(TAG, "Activity receive result requestKey:$requestKey ,result:$result")
            binding.tvReceiver.text = "Activity receive: requestKey = $requestKey ,result = $result"
        }

        binding.btnShowDialog.setOnClickListener {
            DialogFragment().show(supportFragmentManager, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.clearFragmentResultListener(canonicalName)
    }
}