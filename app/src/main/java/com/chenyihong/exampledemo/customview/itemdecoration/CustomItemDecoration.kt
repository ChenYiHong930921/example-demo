package com.chenyihong.exampledemo.customview.itemdecoration

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class CustomItemDecoration(
    private val space: Int,
    private val dividerLinePosition: Int,
    private val dividerLineHeight: Int,
    @ColorInt private val dividerLineColor: Int
) : ItemDecoration() {

    private val paint = Paint().apply {
        color = dividerLineColor
        style = Paint.Style.FILL
    }

    private var specialItemView: View? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        parent.getChildLayoutPosition(view).let {
            // 指定位置底部间距设置为2倍
            if (it == dividerLinePosition) {
                specialItemView = view
                outRect.bottom = space * 2
            } else {
                outRect.bottom = space
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        /*// 分割线不会跟随Item项Random滑动
        parent.getChildAt(dividerLinePosition)?.run {
            c.drawRect(parent.paddingStart.toFloat(), (bottom + space - dividerLineHeight / 2).toFloat(), (parent.width - parent.paddingEnd).toFloat(), (bottom + space + dividerLineHeight / 2).toFloat(), paint)
        }*/
        /*// onDraw中使用for循环，多少会影响性能
        for (index in 0 until parent.childCount) {
            val itemView = parent.getChildAt(index)
            // 确保itemView在适配器中的position是需要绘制分割线的position
            if (dividerLinePosition == parent.getChildAdapterPosition(itemView)) {
                itemView.run {
                    c.drawRect(parent.paddingStart.toFloat(), (bottom + space - dividerLineHeight / 2).toFloat(), (parent.width - parent.paddingEnd).toFloat(), (bottom + space + dividerLineHeight / 2).toFloat(), paint)
                }
                break
            }
        }*/
        specialItemView?.run {
            c.drawRect(parent.paddingStart.toFloat(), (bottom + space - dividerLineHeight / 2).toFloat(), (parent.width - parent.paddingEnd).toFloat(), (bottom + space + dividerLineHeight / 2).toFloat(), paint)
        }
    }
}