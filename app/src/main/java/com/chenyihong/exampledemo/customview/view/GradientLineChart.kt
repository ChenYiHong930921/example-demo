package com.chenyihong.exampledemo.customview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.entity.LineEntity
import com.chenyihong.exampledemo.utils.DensityUtil

class GradientLineChart : View {

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    private var chartWidth: Int = 0
    private var chartHeight: Int = 0

    /**
     * 折线宽度
     */
    private var lineWidth: Float = 0f

    /**
     * 网格线宽度
     */
    private var gridLineWidth: Float = 0f

    /**
     * 网格线颜色
     */
    private var gridLineColor: Int = 0

    /**
     * 背景颜色
     */
    private var backgroundColorRes: Int = 0

    private var linePaint: Paint? = null
    private var gridLinePaint: Paint? = null
    private var gradientColor: IntArray? = null

    private val rectF = RectF()
    private val linePath = Path()

    private val lineValueList = ArrayList<LineEntity>()

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        lineValueList.add(LineEntity(3f, 28f))
        lineValueList.add(LineEntity(7f, 2f))
        lineValueList.add(LineEntity(14f, 18f))
        lineValueList.add(LineEntity(17f, 12f))
        lineValueList.add(LineEntity(22f, 21f))

        context?.let {
            gradientColor = intArrayOf(
                ContextCompat.getColor(it, R.color.color_FFD200),
                ContextCompat.getColor(it, R.color.color_FF2600),
                ContextCompat.getColor(it, R.color.color_49E284),
                ContextCompat.getColor(it, R.color.color_00A5FF)
            )
        }

        initAttr(attrs, defStyleAttr)
        initPaint()
    }

    private fun initAttr(attrs: AttributeSet?, defStyleAttr: Int) {
        val typeArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.GradientLineChart, defStyleAttr, 0)

        lineWidth = typeArray.getDimension(
            R.styleable.GradientLineChart_tc_lineWidth,
            DensityUtil.dp2Px(2).toFloat()
        )
        gridLineWidth = typeArray.getDimension(
            R.styleable.GradientLineChart_tc_grid_line_width,
            DensityUtil.dp2Px(1).toFloat()
        )
        gridLineColor = typeArray.getColor(
            R.styleable.GradientLineChart_tc_grid_line_color,
            ContextCompat.getColor(context, R.color.color_1Affffff)
        )
        backgroundColorRes = typeArray.getColor(
            R.styleable.GradientLineChart_tc_background_color,
            ContextCompat.getColor(context, R.color.color_23242a)
        )

        typeArray.recycle()
    }

    private fun initPaint() {
        linePaint = Paint()
        linePaint?.isAntiAlias = true
        linePaint?.style = Paint.Style.STROKE
        linePaint?.strokeWidth = lineWidth

        gridLinePaint = Paint()
        gridLinePaint?.isAntiAlias = true
        gridLinePaint?.style = Paint.Style.FILL
        gridLinePaint?.color = gridLineColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        //MUST CALL THIS
        setMeasuredDimension(viewWidth, viewHeight)

        chartWidth = viewWidth - paddingStart - paddingEnd
        chartHeight = viewHeight - paddingTop - paddingBottom
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //设置画布背景色
        canvas.drawColor(backgroundColorRes)
        //绘制网格线
        drawGradLine(canvas)
        //绘制折线
        drawLine(canvas)
    }

    /**
     * 绘制网格线
     */
    private fun drawGradLine(canvas: Canvas?) {
        gridLinePaint?.let {
            val yGridValues = 7
            val xGridValues = 6

            //Y轴网格线间距
            val yGridDistance = (chartHeight - yGridValues * gridLineWidth) / (yGridValues - 1)
            for (index in 0 until yGridValues) {
                val left = paddingStart.toFloat()
                val top = paddingTop.toFloat() + index * yGridDistance + index * gridLineWidth
                val right = left + chartWidth
                val bottom = top + gridLineWidth
                rectF.set(left, top, right, bottom)
                canvas?.drawRect(rectF, it)
            }

            //X轴网格线间距
            val xGridDistance = (chartWidth - xGridValues * gridLineWidth) / (xGridValues - 1)
            for (index in 0 until xGridValues) {
                val left = paddingStart + xGridDistance * index + gridLineWidth * index
                val top = paddingTop.toFloat()
                val right = left + gridLineWidth
                val bottom = top + chartHeight
                rectF.set(left, top, right, bottom)
                canvas?.drawRect(rectF, gridLinePaint!!)
            }
        }
    }

    /**
     * 绘制折线
     */
    private fun drawLine(canvas: Canvas?) {
        val yGridValues = 7
        val xGridValues = 6
        val yGridDistance = (chartHeight - yGridValues * gridLineWidth) / (yGridValues - 1)
        val xGridDistance = (chartWidth - xGridValues * gridLineWidth) / (xGridValues - 1)

        for ((index, linePoint) in lineValueList.withIndex()) {
            val pointX = ((linePoint.xValue - 5 * index) / 5) * xGridDistance + (xGridDistance * index)
            val pointY = chartHeight - (linePoint.yValue / 30 * (yGridDistance * 6))
            if (index == 0) {
                linePath.moveTo(pointX, pointY)
            } else {
                linePath.lineTo(pointX, pointY)
            }
        }

        linePaint?.shader = createLineGradient(gradientColor!!)
        canvas?.drawPath(linePath, linePaint!!)
    }

    private fun createLineGradient(gradientColor: IntArray): LinearGradient {
        return LinearGradient(
            0f,
            0f,
            0f,
            viewHeight.toFloat(),
            gradientColor,
            null,
            Shader.TileMode.CLAMP
        )
    }
}