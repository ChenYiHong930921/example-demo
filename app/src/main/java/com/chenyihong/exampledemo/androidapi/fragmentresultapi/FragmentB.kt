package com.chenyihong.exampledemo.androidapi.fragmentresultapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chenyihong.exampledemo.databinding.LayoutFragmentResultApiFragmentBBinding

class FragmentB : Fragment() {

    lateinit var binding: LayoutFragmentResultApiFragmentBBinding

    private val canonicalName = this::class.java.canonicalName!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutFragmentResultApiFragmentBBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragmentManager.setFragmentResultListener(canonicalName, this) { requestKey, result ->
            Log.i(TAG, "B Fragment receive result requestKey:$requestKey ,result:$result")
            binding.tvReceiver.text = "B Fragment receive: requestKey = $requestKey ,result = $result"
        }
        binding.includeTitle.tvTitle.text = "Fragment B"
        binding.btnSendToA.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("result", "a result from Fragment B")
            parentFragmentManager.setFragmentResult(FragmentA::class.java.canonicalName!!, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        parentFragmentManager.clearFragmentResultListener(canonicalName)
    }
}