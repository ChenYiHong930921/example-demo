package com.chenyihong.exampledemo.backpress

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutBackPressApiFragmentBinding

class FragmentB : Fragment() {

    lateinit var binding: LayoutBackPressApiFragmentBinding

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.i(TAG, "FragmentB OnBackPressedCallback  handleOnBackPressed function invoke")
            parentFragmentManager.setFragmentResult(BackPressApiActivity::class.java.canonicalName!!, Bundle().apply { putInt("result", 0) })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_back_press_api_fragment, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        binding.includeTitle.tvTitle.text = "BackPressFragmentB"
        binding.btnBackPress.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        onBackPressedCallback.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        onBackPressedCallback.isEnabled = false
    }
}