package com.chenyihong.exampledemo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.R
import java.util.concurrent.atomic.AtomicBoolean

const val RECTANGLE = 0
const val HALT_RECTANGLE = 1
const val OVAL = 2

const val TO_LEFT = 0
const val TO_RIGHT = 1

@Suppress("MemberVisibilityCanBePrivate")
class ShadowView : View {


    private var viewWidth = 0
    private var viewHeight = 0

    private var viewShape = RECTANGLE

    private var viewAlpha = 0f

    private var orientation = TO_LEFT

    /**
     * 控件圆角是否为高度的一半
     */
    private var shadowRoundHalfHeight = false

    /**
     * 控件圆角
     */
    private var shadowRound = 0f

    /**
     * 阴影的颜色
     */
    private var shadowColor = 0

    /**
     * 阴影控件的颜色
     */
    private var shadowViewColor = 0

    /**
     * 阴影模糊度
     */
    private var shadowRadius = 0f

    /**
     * x轴方向的阴影偏移度
     */
    private var shadowOffsetX = 0f

    /**
     * y轴方向的阴影偏移度
     */
    private var shadowOffsetY = 0f

    private var shadowViewPaint: Paint? = null
    private var shadowViewPath: Path? = null

    private val onParentRight = AtomicBoolean(true)
    private val isChangeOrientation = AtomicBoolean(false)
    private val isInitOrientation = AtomicBoolean(true)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttr(attrs, defStyleAttr, defStyleRes)
        initPaint()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        //圆角度为高度的一半
        if (shadowRoundHalfHeight) {
            shadowRound = (viewHeight - paddingTop - paddingBottom) / 2f
        }

        if (viewWidth != 0 && viewHeight != 0) {
            if (isInitOrientation.get()) {
                isInitOrientation.getAndSet(false)

                setOrientation(orientation == TO_LEFT)
            }
        }

        setMeasuredDimension(viewWidth, viewHeight)
    }

    private fun initAttr(attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.theme.obtainStyledAttributes(attributeSet, R.styleable.ShadowView, defStyleAttr, defStyleRes)

        viewShape = typedArray.getInt(R.styleable.ShadowView_viewShape, RECTANGLE)
        viewAlpha = typedArray.getFloat(R.styleable.ShadowView_viewAlpha, 0f)
        orientation = typedArray.getInt(R.styleable.ShadowView_viewOrientation, TO_LEFT)

        shadowRoundHalfHeight = typedArray.getBoolean(R.styleable.ShadowView_roundRadiusHalfHeight, false)
        shadowRound = typedArray.getDimension(R.styleable.ShadowView_roundRadius, 0f)

        shadowViewColor = typedArray.getColor(R.styleable.ShadowView_viewColor, ContextCompat.getColor(context, R.color.white))
        shadowColor = typedArray.getColor(R.styleable.ShadowView_shadowColor, ContextCompat.getColor(context, android.R.color.darker_gray))

        shadowRadius = typedArray.getFloat(R.styleable.ShadowView_shadowRadius, 10f)

        shadowOffsetX = typedArray.getDimension(R.styleable.ShadowView_shadowOffsetX, 0f)
        shadowOffsetY = typedArray.getDimension(R.styleable.ShadowView_shadowOffsetY, 0f)

        typedArray.recycle()
    }

    private fun initPaint() {
        shadowViewPaint = Paint()
        shadowViewPaint?.run {
            color = shadowViewColor
            isAntiAlias = true
            style = Paint.Style.FILL
            alpha = (255 * viewAlpha).toInt()
        }

        shadowViewPath = Path()
    }

    fun setOrientation(onParentRight: Boolean) {
        if (this.onParentRight.get() != onParentRight) {
            this.onParentRight.getAndSet(onParentRight)
            isChangeOrientation.getAndSet(true)
        }

        when (viewShape) {
            RECTANGLE -> setRectanglePath()
            HALT_RECTANGLE -> setHaltRectanglePath()
            OVAL -> setOvalPath()
            else -> {}
        }

        if (isChangeOrientation.get()) {
            isChangeOrientation.getAndSet(false)
            invalidate()
        }
    }

    private fun setHaltRectanglePath() {
        shadowViewPath?.run {
            reset()
            if (onParentRight.get()) {
                moveTo(viewWidth.toFloat(), paddingTop.toFloat())
                lineTo(paddingStart + shadowRound, 0f)
                addArc(paddingStart.toFloat(), paddingTop.toFloat(), paddingStart + shadowRound * 2, (viewHeight - paddingBottom).toFloat(), 270f, -180f)
                lineTo(viewWidth.toFloat(), (viewHeight - paddingBottom).toFloat())
                lineTo(viewWidth.toFloat(), paddingTop.toFloat())
            } else {
                moveTo(0f, paddingTop.toFloat())
                lineTo(viewWidth - paddingEnd - shadowRound, 0f)
                addArc(viewWidth - paddingEnd - shadowRound * 2, paddingTop.toFloat(), (viewWidth - paddingEnd).toFloat(), (viewHeight - paddingBottom).toFloat(), 270f, 180f)
                lineTo(0f, (viewHeight - paddingBottom).toFloat())
                lineTo(0f, paddingTop.toFloat())
            }
        }
    }

    private fun setRectanglePath() {
        shadowViewPath?.run {
            reset()
            val radius = floatArrayOf(shadowRound, shadowRound, shadowRound, shadowRound, shadowRound, shadowRound, shadowRound, shadowRound)
            addRoundRect(paddingStart.toFloat(), paddingTop.toFloat(), (viewWidth - paddingEnd).toFloat(), (viewHeight - paddingBottom).toFloat(), radius, Path.Direction.CW)
        }
    }

    private fun setOvalPath() {
        shadowViewPath?.run {
            reset()
            addOval(paddingStart.toFloat(), paddingTop.toFloat(), (viewWidth - paddingEnd).toFloat(), (viewHeight - paddingBottom).toFloat(), Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            if (shadowViewPath != null && shadowViewPaint != null) {
                shadowViewPaint!!.setShadowLayer(shadowRadius, if (viewShape == HALT_RECTANGLE) if (onParentRight.get()) -shadowOffsetX else shadowOffsetX else 0f, shadowOffsetY, shadowColor)
                drawPath(shadowViewPath!!, shadowViewPaint!!)
                save()
            }
        }
    }
}