@file:Suppress("unused")

package com.chenyihong.exampledemo.customview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.chenyihong.exampledemo.R

import java.util.concurrent.atomic.AtomicBoolean

const val RECTANGLE = 0
const val HALT_RECTANGLE = 1
const val OVAL = 2

const val TO_LEFT = 0
const val TO_RIGHT = 1

const val CORNER_POSITION_ALL = 10
const val CORNER_POSITION_TOP = 11
const val CORNER_POSITION_LEFT = 12
const val CORNER_POSITION_BOTTOM = 13
const val CORNER_POSITION_RIGHT = 14
const val CORNER_POSITION_TOP_LEFT = 15
const val CORNER_POSITION_TOP_RIGHT = 16
const val CORNER_POSITION_BOTTOM_LEFT = 17
const val CORNER_POSITION_BOTTOM_RIGHT = 18

@Suppress("MemberVisibilityCanBePrivate")
class ShadowView : View {

    private var viewWidth = 0
    private var viewHeight = 0

    private var viewShape = RECTANGLE

    private var viewAlpha = 0f

    /**
     * orientation only takes effect when viewShape is HALT_RECTANGLE
     */
    private var orientation = TO_LEFT

    private var shadowRoundHalfHeight = false

    private var shadowRound = 0f

    /**
     * cornerPosition only takes effect when viewShape is RECTANGLE
     */
    private var cornerPosition = CORNER_POSITION_ALL

    private var shadowColor = 0

    private var shadowViewColor = 0

    private var shadowRadius = 0f

    private var shadowOffsetX = 0f

    private var shadowOffsetY = 0f

    private var shadowViewPaint: Paint? = null
    private var shadowViewPath: Path? = null

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

        if (shadowRoundHalfHeight) {
            shadowRound = (viewHeight - paddingTop - paddingBottom) / 2f
        }

        if (viewWidth != 0 && viewHeight != 0) {
            if (isInitOrientation.get()) {
                isInitOrientation.getAndSet(false)

                setOrientation(orientation)
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
        cornerPosition = typedArray.getInt(R.styleable.ShadowView_cornerPosition, CORNER_POSITION_ALL)

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

    fun setOrientation(orientation: Int) {
        if (this.orientation != orientation) {
            this.orientation = orientation
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
            if (orientation == TO_LEFT) {
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
            val radius = when (cornerPosition) {
                CORNER_POSITION_TOP -> floatArrayOf(shadowRound, shadowRound, shadowRound, shadowRound, 0f, 0f, 0f, 0f)
                CORNER_POSITION_LEFT -> floatArrayOf(shadowRound, shadowRound, 0f, 0f, 0f, 0f, shadowRound, shadowRound)
                CORNER_POSITION_BOTTOM -> floatArrayOf(0f, 0f, 0f, 0f, shadowRound, shadowRound, shadowRound, shadowRound)
                CORNER_POSITION_RIGHT -> floatArrayOf(0f, 0f, shadowRound, shadowRound, shadowRound, shadowRound, 0f, 0f)
                CORNER_POSITION_TOP_LEFT -> floatArrayOf(shadowRound, shadowRound, 0f, 0f, 0f, 0f, 0f, 0f)
                CORNER_POSITION_TOP_RIGHT -> floatArrayOf(0f, 0f, shadowRound, shadowRound, 0f, 0f, 0f, 0f)
                CORNER_POSITION_BOTTOM_LEFT -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, shadowRound, shadowRound)
                CORNER_POSITION_BOTTOM_RIGHT -> floatArrayOf(0f, 0f, 0f, 0f, shadowRound, shadowRound, 0f, 0f)
                else -> floatArrayOf(shadowRound, shadowRound, shadowRound, shadowRound, shadowRound, shadowRound, shadowRound, shadowRound)
            }
            addRoundRect(paddingStart.toFloat(), paddingTop.toFloat(), (viewWidth - paddingEnd).toFloat(), (viewHeight - paddingBottom).toFloat(), radius, Path.Direction.CW)
        }
    }

    private fun setOvalPath() {
        shadowViewPath?.run {
            reset()
            addOval(paddingStart.toFloat(), paddingTop.toFloat(), (viewWidth - paddingEnd).toFloat(), (viewHeight - paddingBottom).toFloat(), Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.run {
            if (shadowViewPath != null && shadowViewPaint != null) {
                shadowViewPaint!!.setShadowLayer(shadowRadius, if (viewShape == HALT_RECTANGLE && orientation == TO_LEFT) -shadowOffsetX else shadowOffsetX, shadowOffsetY, shadowColor)
                drawPath(shadowViewPath!!, shadowViewPaint!!)
                save()
            }
        }
    }

    fun setViewColor(@ColorInt colorRes: Int) {
        shadowViewColor = colorRes
        invalidate()
    }
}