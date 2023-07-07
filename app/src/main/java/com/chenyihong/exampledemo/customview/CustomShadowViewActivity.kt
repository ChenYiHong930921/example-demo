package com.chenyihong.exampledemo.customview

import android.annotation.SuppressLint
import android.graphics.Outline
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.databinding.LayoutCustomShadowViewActivityBinding
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.utils.DensityUtil
import com.chenyihong.exampledemo.utils.ShapeDrawableUtils
import com.chenyihong.exampledemo.customview.view.ShadowView
import com.chenyihong.exampledemo.customview.view.TO_LEFT
import com.chenyihong.exampledemo.customview.view.TO_RIGHT

class CustomShadowViewActivity : BaseGestureDetectorActivity<LayoutCustomShadowViewActivityBinding>() {

    private var onRight: Boolean = true

    private var dragView: View? = null

    override fun initViewBinding(layoutInflater: LayoutInflater): LayoutCustomShadowViewActivityBinding {
        return LayoutCustomShadowViewActivityBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels

        binding.includeTitle.tvTitle.text = "Custom Shadow View"
        binding.vShadowTestOutline.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val width = view.measuredWidth.toFloat()
                val height = view.measuredHeight.toFloat()

                if (width != 0f && height != 0f) {
                    val radius = height / 2

                    val path = Path()
                    if (onRight) {
                        path.moveTo(width, 0f)
                        path.lineTo(0 + radius, 0f)
                        path.addArc(0f, 0f, 0 + radius * 2, height, 270f, -180f)
                        path.lineTo(width, height)
                        path.lineTo(width, 0f)
                    } else {
                        path.moveTo(0f, 0f)
                        path.lineTo(width - radius, 0f)
                        path.addArc(width - radius * 2, 0f, width, height, 270f, 180f)
                        path.lineTo(0f, height)
                        path.lineTo(0f, 0f)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        outline.setPath(path)
                    } else {
                        outline.setConvexPath(path)
                    }
                }

                outline.alpha = 0.9f
            }
        }

        binding.vShadowTestOutline.setOnLongClickListener {
            dragView = it
            val dragShadowBuilder = View.DragShadowBuilder(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(null, dragShadowBuilder, null, 0)
            } else {
                it.startDrag(null, dragShadowBuilder, null, 0)
            }
            true
        }

        binding.vShadowTestShadowLayer.setOnLongClickListener {
            dragView = it
            val dragShadowBuilder = View.DragShadowBuilder(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(null, dragShadowBuilder, null, 0)
            } else {
                it.startDrag(null, dragShadowBuilder, null, 0)
            }
            true
        }

        binding.root.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    dragView?.visibility = View.INVISIBLE
                }

                DragEvent.ACTION_DROP -> {
                    val height: Int = (dragView?.height ?: 0) / 2

                    onRight = event.x >= widthPixels / 2

                    changeCenterViewOrientation(onRight)

                    dragView?.y = event.y - height
                    dragView?.visibility = View.VISIBLE
                }
            }
            true
        }
    }

    /**
     * 切换中心View的方向
     *
     * @param onRight 中心View右侧贴边
     */
    private fun changeCenterViewOrientation(onRight: Boolean) {
        dragView?.run {
            val centerViewLayoutParams = layoutParams as ConstraintLayout.LayoutParams

            if (this is ShadowView) {
                setOrientation(if (onRight) TO_LEFT else TO_RIGHT)
            } else {
                //切换背景图片
                val backgroundDrawable = ShapeDrawableUtils.getRectAngleGradientDrawable(
                    DensityUtil.dp2Px(27).toFloat(), ContextCompat.getColor(this@CustomShadowViewActivity, android.R.color.white),
                    DensityUtil.dp2Px(82), DensityUtil.dp2Px(54),
                    onRight, !onRight)
                background = backgroundDrawable
            }

            if (onRight) {
                //在右侧
                centerViewLayoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
                centerViewLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            } else {
                //在左侧
                centerViewLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                centerViewLayoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
            }
            layoutParams = centerViewLayoutParams
        }
    }
}