package com.chenyihong.exampledemo.gesturedetector

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import kotlin.math.abs

const val TAG = "GestureDetectorSimple"

open class BaseGestureDetectorActivity : AppCompatActivity() {

    private lateinit var gestureDetectorCompat: GestureDetectorCompat
    private var widthPixels: Int = 0
    private val simpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            onUserInteraction()
            return e.x < 100 || e.x > resources.displayMetrics.widthPixels - 100
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val distantX = abs(e2.x - e1.x)
            val distantY = abs(e2.y - e1.y)
            e1.x.let {
                //判定按下的落点是屏幕的边缘
                if (it < 100 || it > widthPixels - 100) {
                    //判定x轴移动的距离大于y轴移动的距离
                    if (distantX > distantY) {
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gestureDetectorCompat = GestureDetectorCompat(this, simpleOnGestureListener)
        widthPixels = resources.displayMetrics.widthPixels
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