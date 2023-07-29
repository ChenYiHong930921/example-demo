package com.chenyihong.exampledemo.androidapi.gesturedetector

import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.*
import androidx.viewbinding.ViewBinding
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.customview.view.VolumeControllerDialog
import com.chenyihong.exampledemo.utils.DensityUtil
import kotlin.math.abs

const val TAG = "GestureDetectorSimple"

abstract class BaseGestureDetectorActivity<VB : ViewBinding> : AppCompatActivity() {

    private lateinit var gestureDetectorCompat: GestureDetectorCompat
    private var screenWidth = 0
    private var screenHeight = 0
    private var edgeSize = 0
    private val simpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            onUserInteraction()
            val finalScreeWidth = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                screenHeight
            } else {
                screenWidth
            }
            return e.x < edgeSize || e.x > finalScreeWidth - edgeSize
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            e1?.let {
                val distantX = abs(e2.x - it.x)
                val distantY = abs(e2.y - it.y)
                val finalScreeWidth = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    screenHeight
                } else {
                    screenWidth
                }
                //判定按下的落点是屏幕的边缘
                if (it.x < edgeSize || it.x > finalScreeWidth - edgeSize) {
                    //判定x轴移动的距离大于y轴移动的距离
                    if (distantX > distantY) {
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    lateinit var binding: VB

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initViewBinding(layoutInflater)
        setContentView(binding.root)
        gestureDetectorCompat = GestureDetectorCompat(this, simpleOnGestureListener)
        val point = Point()
        windowManager.defaultDisplay.getRealSize(point)
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = point.y
        edgeSize = (screenWidth * 0.035).toInt()
    }

    abstract fun initViewBinding(layoutInflater: LayoutInflater): VB

    override fun onResume() {
        super.onResume()
        initVolumeControllerView()
    }

    private fun initVolumeControllerView() {
        val controllerView = AppCompatImageView(this)
        controllerView.layoutParams = FrameLayout.LayoutParams(DensityUtil.dp2Px(80), DensityUtil.dp2Px(12)).apply {
            gravity = Gravity.START
            marginStart = DensityUtil.dp2Px(20)
            topMargin = DensityUtil.dp2Px(10)
        }
        controllerView.setImageResource(R.drawable.shape_vollume_controller)
        controllerView.setOnClickListener {
            VolumeControllerDialog().show(supportFragmentManager, null)
        }
        val rootView = findViewById<FrameLayout>(android.R.id.content)
        rootView.addView(controllerView)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val consumeMotionEvent = gestureDetectorCompat.onTouchEvent(ev)
        return if (consumeMotionEvent) {
            true
        } else {
            super.dispatchTouchEvent(ev)
        }
    }

    fun showToast(toastMessage: String) {
        runOnUiThread {
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }
}