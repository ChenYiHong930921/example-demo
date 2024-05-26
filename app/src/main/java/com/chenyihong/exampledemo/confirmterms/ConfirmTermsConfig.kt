package com.chenyihong.exampledemo.confirmterms

import androidx.collection.ArrayMap
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.utils.DensityUtil

class ConfirmTermsConfiguration private constructor() {

    // 同意提示文案
    var confirmTipsContent: String = ""
        private set

    // 可点击的条款文案,键为条款文案，值为条款内容（链接）
    var clickableTerms = ArrayMap<String, String>()
        private set

    // 同意条款控件距离底部的距离，默认为32dp
    // 左右两侧的边距可以根据实际需求决定是否需要提供配置方法
    var viewBottomMargin = DensityUtil.dp2Px(36)
        private set

    // 文字大小，默认14sp
    var textSize = 14f
        private set

    // 文字颜色，默认黑色
    var textColor = android.R.color.black
        private set

    // 可点击文字的颜色，默认为蓝色
    var clickableTextColor = R.color.color_blue_229CE9
        private set

    // 是否显示下滑线，默认不显示
    var showUnderline = false
        private set

    // 是否显示勾选框，默认为false
    // 示例中勾选框直接使用可点击文案的颜色
    // 可以根据实际需求决定是否提供相应的配置方法
    var showCheckbox = false
        private set

    class Builder() {
        private var confirmTipsContent: String = ""
        private val clickableTerms = ArrayMap<String, String>()
        private var viewBottomMargin = DensityUtil.dp2Px(36)
        private var textSize = 14f
        private var textColor = android.R.color.black
        private var clickableTextColor = R.color.color_blue_229CE9
        private var showUnderline = false
        private var showCheckbox = false

        fun setConfirmTipContent(confirmTipsContent: String): Builder {
            this.confirmTipsContent = confirmTipsContent
            return this
        }

        fun setClickableTerm(clickableTerm: String, termsLink: String): Builder {
            clickableTerms.clear()
            clickableTerms[clickableTerm] = termsLink
            return this
        }

        fun addClickableTerms(clickableTerms: Map<String, String>): Builder {
            this.clickableTerms.clear()
            this.clickableTerms.putAll(clickableTerms)
            return this
        }

        fun setViewBottomMargin(viewBottomMargin: Int): Builder {
            this.viewBottomMargin = viewBottomMargin
            return this
        }

        fun setTextSize(textSize: Float): Builder {
            this.textSize = textSize
            return this
        }

        fun setTextColor(textColor: Int): Builder {
            this.textColor = textColor
            return this
        }

        fun setClickableTextColor(clickableTextColor: Int): Builder {
            this.clickableTextColor = clickableTextColor
            return this
        }

        fun setShowUnderline(showUnderline: Boolean): Builder {
            this.showUnderline = showUnderline
            return this
        }

        fun setShowCheckbox(showCheckbox: Boolean): Builder {
            this.showCheckbox = showCheckbox
            return this
        }

        fun build(): ConfirmTermsConfiguration {
            return ConfirmTermsConfiguration().also {
                it.confirmTipsContent = confirmTipsContent
                it.clickableTerms = clickableTerms
                it.viewBottomMargin = viewBottomMargin
                it.textSize = textSize
                it.textColor = textColor
                it.clickableTextColor = clickableTextColor
                it.showUnderline = showUnderline
                it.showCheckbox = showCheckbox
            }
        }
    }
}