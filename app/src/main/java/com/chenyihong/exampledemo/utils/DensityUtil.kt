package com.chenyihong.exampledemo.utils

import android.content.res.Resources
import android.util.TypedValue

object DensityUtil {

    /**
     * dp convert to px
     *
     * @param dpValue dp
     */
    @JvmStatic
    fun dp2Px(dpValue: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue.toFloat(), Resources.getSystem().displayMetrics).toInt()
    }

    /**
     * px convert to dp
     *
     * @param pxValue px
     */
    @JvmStatic
    fun px2Dp(pxValue: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pxValue.toFloat(), Resources.getSystem().displayMetrics).toInt()
    }
}