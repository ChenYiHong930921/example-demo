package com.chenyihong.exampledemo.utils

import android.graphics.drawable.GradientDrawable
import java.util.ArrayList

object ShapeDrawableUtils {

    /**
     * get shape
     *
     * @param radius          corner radius
     * @param backgroundColor background color
     */
    @JvmStatic
    fun getRectAngleGradientDrawable(radius: Float, backgroundColor: Int): GradientDrawable {
        return getRectAngleGradientDrawable(radius, backgroundColor, 0, 0, true, true, true, true)
    }

    /**
     * get shape
     *
     * @param radius          corner radius
     * @param backgroundColor background color
     * @param width           view width
     * @param height          view height
     * @param needLeftCorner  set left corner or not
     * @param needRightCorner set right corner or not
     */
    @JvmStatic
    fun getRectAngleGradientDrawable(radius: Float, backgroundColor: Int, width: Int, height: Int, needLeftCorner: Boolean, needRightCorner: Boolean): GradientDrawable {
        return getRectAngleGradientDrawable(radius, backgroundColor, width, height, needLeftCorner, needRightCorner, needRightCorner, needLeftCorner)
    }

    /**
     * get shape
     *
     * @param radius                corner radius
     * @param backgroundColor       background color
     * @param width                 view width
     * @param height                view height
     * @param needLeftTopCorner     set left top corner or not
     * @param needRightTopCorner    set right top corner or not
     * @param needRightBottomCorner set right bottom corner or not
     * @param needLeftBottomCorner  set left bottom corner or not
     */
    @JvmStatic
    fun getRectAngleGradientDrawable(radius: Float, backgroundColor: Int, width: Int, height: Int,
                                     needLeftTopCorner: Boolean, needRightTopCorner: Boolean,
                                     needRightBottomCorner: Boolean, needLeftBottomCorner: Boolean): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        val radiusArray = ArrayList<Float>()
        if (needLeftTopCorner) {
            radiusArray.add(radius)
            radiusArray.add(radius)
        } else {
            radiusArray.add(0f)
            radiusArray.add(0f)
        }
        if (needRightTopCorner) {
            radiusArray.add(radius)
            radiusArray.add(radius)
        } else {
            radiusArray.add(0f)
            radiusArray.add(0f)
        }
        if (needRightBottomCorner) {
            radiusArray.add(radius)
            radiusArray.add(radius)
        } else {
            radiusArray.add(0f)
            radiusArray.add(0f)
        }
        if (needLeftBottomCorner) {
            radiusArray.add(radius)
            radiusArray.add(radius)
        } else {
            radiusArray.add(0f)
            radiusArray.add(0f)
        }
        val radii = FloatArray(radiusArray.size)
        for (i in radiusArray.indices) {
            radii[i] = radiusArray[i]
        }
        drawable.cornerRadii = radii
        if (width != 0 && height != 0) {
            drawable.setSize(width, height)
        }
        drawable.setColor(backgroundColor)
        return drawable
    }
}