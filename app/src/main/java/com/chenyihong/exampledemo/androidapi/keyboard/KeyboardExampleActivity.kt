package com.chenyihong.exampledemo.androidapi.keyboard

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutKeyboardExampleActivityBinding

class KeyboardExampleActivity : BaseGestureDetectorActivity<LayoutKeyboardExampleActivityBinding>() {

    private var keyboardShown = false

    private var needRestore = false

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutKeyboardExampleActivityBinding {
        return LayoutKeyboardExampleActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, windowInsets ->
            windowInsets.getInsets(WindowInsetsCompat.Type.ime()).run {
                // 当软键盘弹出时，bottom为软件的高度
                keyboardShown = bottom > 0
                // 处理输入框被软键盘遮住的问题
                handleKeyboardObscuringView()
            }
            WindowInsetsCompat.CONSUMED
        }
        binding.btnShowKeyboard.setOnClickListener { showKeyboard(binding.etInputData) }
        binding.btnHideKeyboard.setOnClickListener { hideKeyboard(binding.etInputData) }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!isTouchTargetView(ev, binding.etInputData) && !isTouchTargetView(ev, binding.etInputDataBottom)) {
            // 判断不是触摸输入框的位置则隐藏软键盘
            currentFocus?.let { hideKeyboard(it) }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun showKeyboard(editText: View) {
        if (!keyboardShown) {
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.requestFocus()
            WindowInsetsControllerCompat(window, editText).show(WindowInsetsCompat.Type.ime())
        }
    }

    private fun hideKeyboard(editText: View) {
        if (keyboardShown) {
            editText.clearFocus()
            WindowInsetsControllerCompat(window, editText).hide(WindowInsetsCompat.Type.ime())
        }
    }

    private fun handleKeyboardObscuringView() {
        if (keyboardShown) {
            currentFocus?.let { focusView ->
                val rect = Rect()
                binding.root.getWindowVisibleDisplayFrame(rect)
                val locations = IntArray(2)
                focusView.getLocationInWindow(locations)
                if (rect.bottom < locations[1] + focusView.height) {
                    // 如果输入框被软件盘遮挡，则滚动页面至可以显示输入框的位置
                    binding.root.scrollTo(0, (locations[1] + focusView.height + focusView.height / 2) - rect.bottom)
                    needRestore = true
                }
            }
        } else if (needRestore) {
            binding.root.scrollTo(0, 0)
        }
    }

    private fun isTouchTargetView(ev: MotionEvent?, view: View?): Boolean {
        return if (ev != null && view != null) {
            val screenLocation = IntArray(2)
            view.getLocationOnScreen(screenLocation)
            val viewX = screenLocation[0]
            val viewY = screenLocation[1]
            (ev.rawX >= viewX && ev.rawX <= (viewX + view.width)) && (ev.rawY >= viewY && ev.rawY <= (viewY + view.height))
        } else {
            false
        }
    }
}