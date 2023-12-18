package com.chenyihong.exampledemo.androidapi.dialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.chenyihong.exampledemo.databinding.LayoutDialogFragmentExampleActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogFragmentExampleActivity : AppCompatActivity() {

    val DIALOG_TYPE_LOADING = "loadingDialog"

    private lateinit var insetsController: WindowInsetsControllerCompat

    private var alreadyChanged = false

    private var callDismissDialogTime = 0L

    private var navigationBarShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LayoutDialogFragmentExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        supportFragmentManager.setFragmentResultListener(this::class.java.simpleName, this) { requestKey, result ->
            // 接收加载弹窗关闭的消息
            if (requestKey == this::class.java.simpleName) {
                if (navigationBarShow) {
                    // 根据实践，不延迟500毫秒有概率出现无法隐藏的情况。
                    lifecycleScope.launch(Dispatchers.IO) {
                        delay(500L)
                        withContext(Dispatchers.Main) {
                            hideNavigationBar()
                        }
                    }
                }
            }
        }

        // 调整系统栏
        WindowCompat.setDecorFitsSystemWindows(window, false)
        insetsController = WindowCompat.getInsetsController(window, window.decorView).also {
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.hide(WindowInsetsCompat.Type.navigationBars())
        }
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).run {
                if (!alreadyChanged) {
                    alreadyChanged = true
                    binding.topView.updateLayoutParams<ConstraintLayout.LayoutParams> { height += top }
                    binding.tvTitle.updateLayoutParams<ConstraintLayout.LayoutParams> { topMargin += top }
                }
                // 当底部空间不为0时可以判断导航栏显示
                navigationBarShow = bottom != 0
            }
            WindowInsetsCompat.CONSUMED
        }

        binding.btnShowLoadingDialog.setOnClickListener {
            showLoadingDialog()
        }
    }

    private fun showLoadingDialog() {
        LoadingDialogFragment().run {
            show(supportFragmentManager, DIALOG_TYPE_LOADING)
        }
        // 模拟耗时操作，两秒后关闭弹窗
        lifecycleScope.launch(Dispatchers.IO) {
            delay(2000)
            dismissLoadingDialog()
        }
    }

    private fun dismissLoadingDialog() {
        callDismissDialogTime = System.currentTimeMillis()
        lifecycleScope.launch(Dispatchers.IO) {
            if (async { checkLoadingDialogStatue() }.await()) {
                withContext(Dispatchers.Main) {
                    // 从supportFragmentManager中获取加载弹窗，并调用隐藏方法
                    (supportFragmentManager.findFragmentByTag(DIALOG_TYPE_LOADING) as? DialogFragment)?.run {
                        if (dialog?.isShowing == true) {
                            dismissAllowingStateLoss()
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查加载弹窗的状态直到获取到加载弹窗或者超过时间
     */
    private suspend fun checkLoadingDialogStatue(): Boolean {
        return if (supportFragmentManager.findFragmentByTag(DIALOG_TYPE_LOADING) == null && System.currentTimeMillis() - callDismissDialogTime < 1500L) {
            delay(100)
            checkLoadingDialogStatue()
        } else {
            true
        }
    }

    private fun hideNavigationBar() {
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }

    override fun onDestroy() {
        super.onDestroy()
        // 页面销毁时清除监听
        supportFragmentManager.clearFragmentResultListener(this::class.java.simpleName)
    }
}