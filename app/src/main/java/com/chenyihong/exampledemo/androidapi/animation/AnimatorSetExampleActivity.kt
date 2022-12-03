package com.chenyihong.exampledemo.androidapi.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutAnimatorsetExampleActivityBinding
import com.chenyihong.exampledemo.utils.DensityUtil

class AnimatorSetExampleActivity : BaseGestureDetectorActivity() {

    private lateinit var binding: LayoutAnimatorsetExampleActivityBinding

    private val animatorConfig: ArrayList<java.util.ArrayList<Float>> = arrayListOf(
        arrayListOf(-160f, 150f, 1f),
        arrayListOf(80f, 130f, 1.1f),
        arrayListOf(-120f, -170f, 1.3f),
        arrayListOf(80f, -130f, 1f),
        arrayListOf(-20f, -80f, 0.8f))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_animatorset_example_activity)
        binding.ivThumbUp.setOnClickListener {
            playThumbUpScaleAnimator()
            playDiffusionAnimator()
        }
    }

    private fun playThumbUpScaleAnimator() {
        // x，y轴方向都从1倍放大到2倍，以控件的中心为原点进行缩放
        ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).run {
            // 先取消控件当前的动画效果（重复点击时）
            binding.ivThumbUp.clearAnimation()
            // 设置动画的持续时间
            duration = 300
            // 开始播放动画
            binding.ivThumbUp.startAnimation(this)
        }
    }

    private fun playDiffusionAnimator() {
        for (index in 0 until 5) {
            binding.root.run {
                if (this is ViewGroup) {
                    // 创建控件
                    val ivThumbUp = AppCompatImageView(context)
                    ivThumbUp.setImageResource(R.drawable.icon_thumb_up)
                    // 设置与原控件一样的大小
                    ivThumbUp.layoutParams = FrameLayout.LayoutParams(DensityUtil.dp2Px(25), DensityUtil.dp2Px(25))
                    // 先设置为全透明
                    ivThumbUp.alpha = 0f
                    addView(ivThumbUp)
                    // 设置与原控件一样的位置
                    ivThumbUp.x = binding.ivThumbUp.x
                    ivThumbUp.y = binding.ivThumbUp.y
                    AnimatorSet().apply {
                        // 设置动画集开始播放前的延迟
                        startDelay = 330L + index * 50L
                        // 设置动画监听
                        addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                // 开始播放时把控件设置为不透明
                                ivThumbUp.alpha = 1f
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                // 播放结束后再次设置为透明，并从根布局中移除
                                ivThumbUp.alpha = 0f
                                ivThumbUp.clearAnimation()
                                ivThumbUp.post { removeView(ivThumbUp) }
                            }

                            override fun onAnimationCancel(animation: Animator) {}

                            override fun onAnimationRepeat(animation: Animator) {}
                        })
                        // 设置三个动画同时播放
                        playTogether(
                            // 缩放动画
                            ValueAnimator.ofFloat(1f, animatorConfig[index][2]).apply {
                                duration = 700
                                // 设置插值器，速度一开始快，快结束时减缓
                                interpolator = DecelerateInterpolator()
                                addUpdateListener { values ->
                                    (values.animatedValue as Float).let { value ->
                                        ivThumbUp.scaleX = value
                                        ivThumbUp.scaleY = value
                                    }
                                }
                            },
                            // Y轴的移动动画
                            ValueAnimator.ofFloat(ivThumbUp.x, ivThumbUp.x + animatorConfig[index][0]).apply {
                                duration = 700
                                interpolator = DecelerateInterpolator()
                                addUpdateListener { values ->
                                    ivThumbUp.x = values.animatedValue as Float
                                }
                            },
                            // X轴的移动动画
                            ValueAnimator.ofFloat(ivThumbUp.y, ivThumbUp.y + animatorConfig[index][1]).apply {
                                duration = 700
                                interpolator = DecelerateInterpolator()
                                addUpdateListener { values ->
                                    ivThumbUp.y = values.animatedValue as Float
                                }
                            })
                    }.start()
                }
            }
        }
    }
}