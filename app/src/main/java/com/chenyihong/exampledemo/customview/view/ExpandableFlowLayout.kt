package com.chenyihong.exampledemo.customview.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.utils.DensityUtil

class ExpandableFlowLayout : ViewGroup {

    private val defaultVerticalSpace = paddingTop + paddingBottom
    private val defaultHorizontalSpace = paddingStart + paddingEnd

    private var defaultShowRow = 2

    private var measureNeedExpandView = false
    var expand = false

    private var expandView: View

    private var elementDividerVertical: Int = DensityUtil.dp2Px(8)
    private var elementDividerHorizontal: Int = DensityUtil.dp2Px(8)

    var elementClickCallback: ((content: String) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.ExpandableFlowLayout).run {
            defaultShowRow = getInt(R.styleable.ExpandableFlowLayout_default_show_row, 2)
            expand = getBoolean(R.styleable.ExpandableFlowLayout_default_expand_status, false)

            elementDividerVertical = getDimensionPixelSize(R.styleable.ExpandableFlowLayout_element_divider_vertical, DensityUtil.dp2Px(8))
            elementDividerHorizontal = getDimensionPixelSize(R.styleable.ExpandableFlowLayout_element_divider_horizontal, DensityUtil.dp2Px(8))

            recycle()
        }
        expandView = AppCompatImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, DensityUtil.dp2Px(30))
            setImageResource(R.mipmap.icon_triangular_arrow_down)
            rotation = if (!expand) 0f else 180f
            setOnClickListener {
                expand = !expand
                rotation = if (!expand) 0f else 180f
                requestLayout()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val rootWidth = MeasureSpec.getSize(widthMeasureSpec)
        var usedWidth = defaultHorizontalSpace
        var usedHeight = defaultVerticalSpace

        measureChild(expandView, widthMeasureSpec, heightMeasureSpec)

        var rowCount = 1
        for (index in 0 until childCount - 1) {
            val childView = getChildAt(index)
            if (childView != null) {
                // 测量当前子控件的宽高。
                measureChild(childView, widthMeasureSpec, heightMeasureSpec)
                val realChildViewUsedWidth = childView.measuredWidth + elementDividerHorizontal
                val realChildViewUsedHeight = childView.measuredHeight + elementDividerVertical

                if (usedHeight == defaultVerticalSpace) {
                    usedHeight += realChildViewUsedHeight
                }

                // 当前子控件宽度加上之前已用宽度大于根布局宽度，需要换行。
                if (usedWidth + realChildViewUsedWidth > rootWidth) {
                    // 换行
                    rowCount++

                    // 当前为未展开状态，并且此时行数已经超过了默认显示行数，跳过后续的测量。
                    if (!expand && rowCount > defaultShowRow) {
                        break
                    }

                    // 重置已用宽度
                    usedWidth = defaultHorizontalSpace
                    // 增加已用高度
                    usedHeight += realChildViewUsedHeight
                }

                usedWidth += realChildViewUsedWidth

                if (index == childCount - 2 && expand && rowCount > defaultShowRow) {
                    // 展开状态下的最后一个元素，
                    // 此时判断能否再放下展开控件，不能则需要增加一行用于显示展开控件。
                    if (usedWidth + expandView.measuredWidth > rootWidth) {
                        usedHeight += expandView.measuredHeight + elementDividerVertical
                    }
                }
            }
        }
        measureNeedExpandView = rowCount > defaultShowRow
        setMeasuredDimension(rootWidth, usedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val availableWidth = right - left
        var usedWidth = defaultHorizontalSpace

        var positionX = paddingStart
        var positionY = paddingTop

        var rowCount = 1
        for (index in 0 until childCount - 1) {
            val childView = getChildAt(index)
            if (childView != null) {
                val realChildViewUsedWidth = childView.measuredWidth + elementDividerHorizontal
                val realChildViewUsedHeight = childView.measuredHeight + elementDividerVertical

                val changeRowCondition = if ((!expand && rowCount == defaultShowRow)) {
                    // 未展开状态，并且当前行已经是默认显示行，已用空间需要加上展开控件的空间
                    usedWidth + realChildViewUsedWidth + (if (measureNeedExpandView) expandView.measuredWidth else 0) > availableWidth
                } else {
                    usedWidth + realChildViewUsedWidth > availableWidth
                }
                if (changeRowCondition) {
                    // 换行
                    rowCount++

                    // 当前为未展开状态，并且此时行数已经超过了默认显示行数，跳过后续处理
                    if (!expand && rowCount > defaultShowRow) {
                        childView.layout(0, 0, 0, 0)
                        break
                    }

                    // 重置已用宽度
                    usedWidth = defaultHorizontalSpace
                    // 新行开始的x轴坐标重置
                    positionX = paddingStart
                    // 新行开始的y轴坐标增加
                    positionY += realChildViewUsedHeight
                }

                childView.layout(positionX, positionY, positionX + childView.measuredWidth, positionY + childView.measuredHeight)
                positionX += realChildViewUsedWidth
                usedWidth += realChildViewUsedWidth

                if (index == childCount - 2 && expand && rowCount > defaultShowRow) {
                    // 展开状态下的最后一个元素，
                    // 此时判断能否再放下展开控件，不能则需要增加一行用于显示展开控件。
                    if (usedWidth + expandView.measuredWidth > availableWidth) {
                        positionX = paddingStart
                        // 新行开始的y轴坐标增加
                        positionY += realChildViewUsedHeight
                    }
                }
            }
        }
        if (measureNeedExpandView) {
            expandView.layout(positionX, positionY, positionX + expandView.measuredWidth, positionY + expandView.measuredHeight)
        } else {
            expandView.layout(0, 0, 0, 0)
        }
    }

    @SuppressLint("InflateParams")
    fun setData(data: List<String>) {
        removeAllViews()
        for (content in data) {
            LayoutInflater.from(context).inflate(R.layout.layout_example_flow_item, null, false).apply {
                layoutParams = MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, DensityUtil.dp2Px(30))
                findViewById<AppCompatTextView>(R.id.tv_example_flow_item_content).run {
                    text = content
                    gravity = Gravity.CENTER_VERTICAL
                    setOnClickListener {
                        elementClickCallback?.invoke(content)
                    }
                }
                addView(this)
            }
        }
        addView(expandView)
    }
}