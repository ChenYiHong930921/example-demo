package com.chenyihong.exampledemo.androidapi.motionlayout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutMotionLayoutExampleActivityBinding

const val TAG = "MotionLayoutExampleTag"

class MotionLayoutExampleActivity : BaseGestureDetectorActivity<LayoutMotionLayoutExampleActivityBinding>() {

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutMotionLayoutExampleActivityBinding {
        return LayoutMotionLayoutExampleActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
                Log.i(TAG, "onTransitionStarted startId:$startId, endId:$endId")
                // 动画开始
                // 把发散的按钮显示出来
                binding.ivThumbUp1.visibility = View.VISIBLE
                binding.ivThumbUp2.visibility = View.VISIBLE
                binding.ivThumbUp3.visibility = View.VISIBLE
                binding.ivThumbUp4.visibility = View.VISIBLE
                binding.ivThumbUp5.visibility = View.VISIBLE
            }

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                Log.i(TAG, "onTransitionChange startId:$startId, endId:$endId, progress:$progress")
                // 动画进行中
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                Log.i(TAG, "onTransitionCompleted currentId:$currentId")
                // 动画完成
                // 隐藏发散的按钮，将状态还原
                binding.root.postDelayed({
                    binding.ivThumbUp1.visibility = View.GONE
                    binding.ivThumbUp2.visibility = View.GONE
                    binding.ivThumbUp3.visibility = View.GONE
                    binding.ivThumbUp4.visibility = View.GONE
                    binding.ivThumbUp5.visibility = View.GONE
                    binding.motionLayout.progress = 0f
                }, 200)
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
                Log.i(TAG, "onTransitionTrigger triggerId:$triggerId, positive:$positive, progress:$progress")
            }
        })
    }
}