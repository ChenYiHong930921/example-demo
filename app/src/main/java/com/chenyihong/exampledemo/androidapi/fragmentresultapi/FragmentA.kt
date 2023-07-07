package com.chenyihong.exampledemo.androidapi.fragmentresultapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chenyihong.exampledemo.databinding.LayoutFragmentResultApiFragmentABinding

class FragmentA : Fragment() {

    lateinit var binding: LayoutFragmentResultApiFragmentABinding

    private val canonicalName = this::class.java.canonicalName!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LayoutFragmentResultApiFragmentABinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragmentManager.setFragmentResultListener(canonicalName, this) { requestKey, result ->
            Log.i(TAG, "A Fragment receive result requestKey:$requestKey ,result:$result")
            binding.tvReceiver.text = "A Fragment receive: requestKey = $requestKey ,result = $result"
        }
        binding.includeTitle.tvTitle.text = "Fragment A"
        binding.btnSendParent.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("result", "a result from Fragment A")
            parentFragmentManager.setFragmentResult(DialogFragment::class.java.canonicalName!!, bundle)
        }
        binding.btnSendToB.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("result", "a result from Fragment A")
            parentFragmentManager.setFragmentResult(FragmentB::class.java.canonicalName!!, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        parentFragmentManager.clearFragmentResultListener(canonicalName)
    }
}