package com.chenyihong.exampledemo.androidapi.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutLoadingDialogBinding
import com.chenyihong.exampledemo.utils.DensityUtil

class LoadingDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.LoadingDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.let { containerDialog ->
            containerDialog.window?.run {
                WindowCompat.setDecorFitsSystemWindows(this, false)
                WindowCompat.getInsetsController(this, decorView).also {
                    it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    it.hide(WindowInsetsCompat.Type.navigationBars())
                }
                setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), android.R.color.transparent))
                decorView.setBackgroundResource(android.R.color.transparent)
                val layoutParams = attributes
                layoutParams.width = DensityUtil.dp2Px(200)
                layoutParams.height = DensityUtil.dp2Px(120)
                layoutParams.gravity = Gravity.CENTER
                attributes = layoutParams
            }
            containerDialog.setCancelable(true)
            containerDialog.setCanceledOnTouchOutside(false)
        }
        return LayoutLoadingDialogBinding.inflate(layoutInflater, container, false).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 这里通过setFragmentResult API 来传递弹窗已经关闭的消息。
        parentFragmentManager.setFragmentResult(DialogFragmentExampleActivity::class.java.simpleName, Bundle())
    }
}