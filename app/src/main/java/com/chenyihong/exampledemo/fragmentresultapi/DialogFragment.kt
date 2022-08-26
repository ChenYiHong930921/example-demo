package com.chenyihong.exampledemo.fragmentresultapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutFragmentResultApiDialogFragmentBinding
import com.chenyihong.exampledemo.adapter.ViewPager2Adapter

class DialogFragment : DialogFragment() {

    private var binding: LayoutFragmentResultApiDialogFragmentBinding? = null

    private val canonicalName = this::class.java.canonicalName!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.run {
            setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), android.R.color.transparent))
            decorView.setBackgroundResource(android.R.color.transparent)

            val layoutParams = attributes
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.gravity = Gravity.CENTER
            attributes = layoutParams
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_fragment_result_api_dialog_fragment, container, false)
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.setFragmentResultListener(canonicalName, this) { requestKey, result ->
            Log.i(TAG, "Dialog Fragment receive result requestKey:$requestKey ,result:$result")
            binding?.tvReceiver?.text = "Dialog Fragment receive: requestKey = $requestKey ,result = $result"
            val bundle = Bundle()
            bundle.putString("result", "DialogFragment transit ${result.getString("result", "")}")
            parentFragmentManager.setFragmentResult(FragmentResultApiActivity::class.java.canonicalName!!, bundle)
        }
        binding?.run {
            btnAFragment.setOnClickListener {
                vpContainer.currentItem = 0
            }
            btnBFragment.setOnClickListener {
                vpContainer.currentItem = 1
            }

            val fragments = ArrayList<Class<out Fragment?>>()
            fragments.add(FragmentA::class.java)
            fragments.add(FragmentB::class.java)

            vpContainer.adapter = ViewPager2Adapter(this@DialogFragment, fragments)
            vpContainer.isUserInputEnabled = false

            vpContainer.currentItem = 0
        }
    }

    override fun onDestroyView() {
        binding?.unbind()
        childFragmentManager.clearFragmentResultListener(canonicalName)
        super.onDestroyView()
    }
}