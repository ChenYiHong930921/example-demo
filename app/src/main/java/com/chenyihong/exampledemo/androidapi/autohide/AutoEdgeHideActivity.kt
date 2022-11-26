package com.chenyihong.exampledemo.androidapi.autohide

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutAutoEdgeHideActivityBinding

class AutoEdgeHideActivity : BaseGestureDetectorActivity() {

    private lateinit var binding: LayoutAutoEdgeHideActivityBinding

    private var widthPixels: Int = 0

    private val autoShowInterval = 2
    private var interacting = false
    private var hidden = false
    private var lastPositionX: Float = 0f

    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper())
    private val autoShowRunnable = Runnable { autoShow() }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_auto_edge_hide_activity)
        widthPixels = resources.displayMetrics.widthPixels
        binding.includeTitle.tvTitle.text = "AutoEdgeHideExample"
        binding.vFloatView.setOnClickListener {
            if (hidden) {
                // 当前为隐藏状态，先显示
                // 把之前的延迟线程先取消
                handler.removeCallbacks(autoShowRunnable)
                autoShow()
                Toast.makeText(this, "手动显示控件", Toast.LENGTH_SHORT).show()
            } else {
                // 相应正常的事件
                Toast.makeText(this, "点击了浮标控件", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!checkIsTouchFloatView(ev, binding.vFloatView)) {
                    // 起始ACTION_DOWN事件在浮标控件外，自动隐藏浮标控件，标记正在交互
                    interacting = true
                    handler.removeCallbacks(autoShowRunnable)
                    autoHide()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (interacting) {
                    // 交互结束，一定时间后自动显示，时间可以自由配置
                    interacting = false
                    handler.postDelayed(autoShowRunnable, autoShowInterval * 1000L)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 检查是否触摸浮标控件
     */
    private fun checkIsTouchFloatView(ev: MotionEvent, view: View): Boolean {
        val screenLocation = IntArray(2)
        view.getLocationOnScreen(screenLocation)
        val viewX = screenLocation[0]
        val viewY = screenLocation[1]
        return (ev.x >= viewX && ev.x <= (viewX + view.width)) && (ev.y >= viewY && ev.y <= (viewY + view.height))
    }

    private fun autoShow() {
        if (hidden) {
            hidden = false
            binding.vFloatView.let {
                xCoordinateAnimator(it, it.x, lastPositionX)
            }
        }
    }

    private fun autoHide() {
        if (!hidden) {
            hidden = true
            binding.vFloatView.let {
                // 记录一下显示状态下的x坐标
                lastPositionX = it.x
                // 隐藏时的x坐标，留一点控件的边缘显示（示例中默认控件在屏幕右侧）
                val endX = widthPixels - it.width * 0.23f
                xCoordinateAnimator(it, lastPositionX, endX)
            }
        }
    }

    private fun xCoordinateAnimator(view: View, startX: Float, endX: Float) {
        val animator = ValueAnimator.ofFloat(startX, endX)
        animator.addUpdateListener {
            view.x = it.animatedValue as Float
        }
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 500
        animator.start()
    }
}