package com.chenyihong.exampledemo.confirmterms

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class ClickSpan(
    // 默认颜色为白色
    private var colorRes: Int = -1,
    // 默认不显示下划线
    private var isShoeUnderLine: Boolean = false,
    // 点击事件监听，必须传入
    private var clickListener: () -> Unit
) : ClickableSpan() {

    override fun onClick(widget: View) {
        // 回调点击事件监听
        clickListener.invoke()
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        //设置文本颜色
        ds.color = colorRes
        //设置是否显示下划线
        ds.isUnderlineText = isShoeUnderLine
    }
}