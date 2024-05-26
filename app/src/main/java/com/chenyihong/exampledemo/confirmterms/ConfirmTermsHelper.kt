package com.chenyihong.exampledemo.confirmterms

import android.app.Activity
import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.method.LinkMovementMethodCompat
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.utils.DensityUtil
import com.chenyihong.exampledemo.web.customtab.CustomTabHelper

class ConfirmTermsHelper {

    private var confirmTermsView: View? = null

    var confirmStatus = false
        private set

    fun showConfirmTermsView(activity: Activity, confirmTermsConfiguration: ConfirmTermsConfiguration) {
        val confirmTipsContent = confirmTermsConfiguration.confirmTipsContent
        val clickableTerms = confirmTermsConfiguration.clickableTerms
        val showCheckBox = confirmTermsConfiguration.showCheckbox
        // 同意条款的提示文案为空直接结束方法执行
        if (confirmTipsContent.isEmpty()) {
            return
        }
        // 先把当前的控件移除
        hideConfirmTermsView()
        activity.runOnUiThread {
            if (showCheckBox) {
                ConstraintLayout(activity).apply {
                    // 代码中创建CheckBox存在Padding，暂时未解决
                    addView(AppCompatCheckBox(activity).apply {
                        id = R.id.cb_confirm_terms
                        val checkboxSize = DensityUtil.dp2Px(30)
                        layoutParams = ConstraintLayout.LayoutParams(checkboxSize, checkboxSize).apply {
                            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        }
                        setButtonDrawable(R.drawable.selector_confirm_terms_chekcbox)
                        buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, confirmTermsConfiguration.clickableTextColor))
                        setOnCheckedChangeListener { _, isChecked ->
                            confirmStatus = isChecked
                        }
                    })
                    addView(AppCompatTextView(activity).apply {
                        id = R.id.tv_confirm_terms
                        layoutParams = ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                            startToEnd = R.id.cb_confirm_terms
                            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                            marginStart = DensityUtil.dp2Px(10)
                        }
                        textSize = confirmTermsConfiguration.textSize
                        setTextColor(ContextCompat.getColor(activity, confirmTermsConfiguration.textColor))
                        movementMethod = LinkMovementMethodCompat.getInstance()
                        text = SpannableStringBuilder(confirmTipsContent).apply {
                            clickableTerms.entries.forEach { clickableTermEntry ->
                                val startHighlightIndex = confirmTipsContent.indexOf(clickableTermEntry.key)
                                if (startHighlightIndex > 0) {
                                    setSpan(
                                        ClickSpan(ContextCompat.getColor(activity, confirmTermsConfiguration.clickableTextColor), confirmTermsConfiguration.showUnderline) {
                                            // 通过CustomTab打开链接
                                            CustomTabHelper.openSimpleCustomTab(activity, clickableTermEntry.value)
                                        },
                                        startHighlightIndex, startHighlightIndex + clickableTermEntry.key.length,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }
                            }
                        }
                    })
                    layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM).apply {
                        val defaultLeftRightSpace = DensityUtil.dp2Px(20)
                        marginStart = defaultLeftRightSpace
                        marginEnd = defaultLeftRightSpace
                        bottomMargin = confirmTermsConfiguration.viewBottomMargin
                    }
                }
            } else {
                AppCompatTextView(activity).apply {
                    layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM).apply {
                        val defaultLeftRightSpace = DensityUtil.dp2Px(20)
                        marginStart = defaultLeftRightSpace
                        marginEnd = defaultLeftRightSpace
                        bottomMargin = confirmTermsConfiguration.viewBottomMargin
                    }
                    textSize = confirmTermsConfiguration.textSize
                    setTextColor(ContextCompat.getColor(activity, confirmTermsConfiguration.textColor))
                    movementMethod = LinkMovementMethodCompat.getInstance()
                    text = SpannableStringBuilder(confirmTipsContent).apply {
                        clickableTerms.entries.forEach { clickableTermEntry ->
                            val startHighlightIndex = confirmTipsContent.indexOf(clickableTermEntry.key)
                            if (startHighlightIndex > 0) {
                                setSpan(
                                    ClickSpan(ContextCompat.getColor(activity, confirmTermsConfiguration.clickableTextColor), confirmTermsConfiguration.showUnderline) {
                                        // 通过CustomTab打开链接
                                        CustomTabHelper.openSimpleCustomTab(activity, clickableTermEntry.value)
                                    },
                                    startHighlightIndex, startHighlightIndex + clickableTermEntry.key.length,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        }
                    }
                }
            }.run {
                confirmTermsView = this
                removeViewInParent(this)
                getRootView(activity).addView(this)
            }
        }
    }

    fun hideConfirmTermsView() {
        confirmStatus = false
        confirmTermsView?.run { post { removeViewInParent(this) } }
        confirmTermsView = null
    }

    private fun getRootView(activity: Activity): FrameLayout {
        return activity.findViewById(android.R.id.content)
    }

    private fun removeViewInParent(targetView: View) {
        try {
            (targetView.parent as? ViewGroup)?.removeView(targetView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}