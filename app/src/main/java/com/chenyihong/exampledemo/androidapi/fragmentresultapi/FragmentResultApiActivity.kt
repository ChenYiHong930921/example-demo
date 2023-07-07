package com.chenyihong.exampledemo.androidapi.fragmentresultapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.chenyihong.exampledemo.databinding.LayoutFragmentResultApiActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

const val TAG = "FragmentResultAPI"

class FragmentResultApiActivity : BaseGestureDetectorActivity<LayoutFragmentResultApiActivityBinding>() {

    private val canonicalName = this::class.java.canonicalName!!

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutFragmentResultApiActivityBinding {
        return LayoutFragmentResultApiActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeTitle.tvTitle.text = "Fragment Result Api"
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