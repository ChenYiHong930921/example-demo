package com.chenyihong.exampledemo.androidapi.gesturedetector

import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import kotlin.math.abs

const val TAG = "GestureDetectorSimple"

open class BaseGestureDetectorActivity : AppCompatActivity() {

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

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val distantX = abs(e2.x - e1.x)
            val distantY = abs(e2.y - e1.y)
            val finalScreeWidth = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                screenHeight
            } else {
                screenWidth
            }
            //判定按下的落点是屏幕的边缘
            if (e1.x < edgeSize || e1.x > finalScreeWidth - edgeSize) {
                //判定x轴移动的距离大于y轴移动的距离
                if (distantX > distantY) {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gestureDetectorCompat = GestureDetectorCompat(this, simpleOnGestureListener)
        val point = Point()
        windowManager.defaultDisplay.getRealSize(point)
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = point.y
        edgeSize = (screenWidth * 0.035).toInt()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val consumeMotionEvent = gestureDetectorCompat.onTouchEvent(ev)
        return if (consumeMotionEvent) {
            true
        } else {
            super.dispatchTouchEvent(ev)
        }
    }
}