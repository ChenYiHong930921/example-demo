package com.chenyihong.exampledemo.androidapi.fullscreen

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.databinding.LayoutImmersionActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity

class ImmersionActivity : BaseGestureDetectorActivity() {

    private lateinit var windowInsetsController: WindowInsetsControllerCompat

    lateinit var binding: LayoutImmersionActivityBinding

    private var hadChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_immersion_activity)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        //透明的导航栏
        windowInsetsController.isAppearanceLightNavigationBars = false

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            if (!hadChange) {
                hadChange = true
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                //状态栏高度
                val statusBarHeight = insets.top
                //调整顶部控件的高度
                binding.topView.run {
                    updateLayoutParams<ConstraintLayout.LayoutParams> {
                        height += statusBarHeight
                    }
                    updatePadding(top = paddingTop + statusBarHeight)
                }
                binding.tvTitle.run {
                    updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topMargin += statusBarHeight
                    }
                }

                //导航栏高度
                val navigationBarHeight = insets.bottom
                //调整圆点控件不被导航栏遮挡
                binding.bottomView.run {
                    updateLayoutParams<ConstraintLayout.LayoutParams> {
                        bottomMargin += navigationBarHeight
                    }
                }
            }
            WindowInsetsCompat.CONSUMED
        }

        binding.btnChangeToLightBar.setOnClickListener {
            lightBar()
        }

        binding.btnChangeToDarkBar.setOnClickListener {
            darkBar()
        }
    }

    private fun lightBar() {
        binding.topView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        binding.tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
        setBarStatus(true)
    }

    private fun darkBar() {
        binding.topView.setBackgroundColor(ContextCompat.getColor(this, R.color.color_23242a))
        binding.tvTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
        setBarStatus(false)
    }

    private fun setBarStatus(light: Boolean) {
        windowInsetsController.isAppearanceLightStatusBars = light
    }
}