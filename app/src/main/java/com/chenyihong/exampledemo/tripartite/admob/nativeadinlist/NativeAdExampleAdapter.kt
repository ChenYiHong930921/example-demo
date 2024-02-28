package com.chenyihong.exampledemo.tripartite.admob.nativeadinlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.databinding.LayoutAdvertiseItemBinding
import com.chenyihong.exampledemo.databinding.LayoutTextContentItemBinding

class NativeAdExampleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val containerData = ArrayList<ExampleEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 根据viewType创建不同类型的ViewHolder
        return if (viewType == LAYOUT_TYPE_AD) {
            AdvertiseItemViewHolder(LayoutAdvertiseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            NormalItemViewHolder(LayoutTextContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemCount(): Int {
        return containerData.size
    }

    override fun getItemViewType(position: Int): Int {
        return containerData[position].layoutType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NormalItemViewHolder -> holder.itemViewBiding.tvContent.text = containerData[position].exampleContent

            is AdvertiseItemViewHolder -> {
                val nativeAdView = (holder.itemView.context as? NativeAdInListExampleActivity)?.getNativeAdView(position)
                if (nativeAdView != null) {
                    holder.itemViewBinding.flNativeAdContainer.removeAllViews()
                    holder.itemViewBinding.flNativeAdContainer.addView(nativeAdView)
                    holder.itemViewBinding.flNativeAdContainer.visibility = View.VISIBLE
                } else {
                    holder.itemViewBinding.flNativeAdContainer.visibility = View.GONE
                    holder.itemViewBinding.flNativeAdContainer.removeAllViews()
                }
            }
        }
    }

    fun setNewData(newData: List<ExampleEntity>?) {
        val currentItemCount = itemCount
        if (currentItemCount != 0) {
            containerData.clear()
            notifyItemRangeRemoved(0, currentItemCount)
        }
        if (!newData.isNullOrEmpty()) {
            containerData.addAll(newData)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    fun release() {
        containerData.clear()
    }

    class AdvertiseItemViewHolder(val itemViewBinding: LayoutAdvertiseItemBinding) : RecyclerView.ViewHolder(itemViewBinding.root)

    class NormalItemViewHolder(val itemViewBiding: LayoutTextContentItemBinding) : RecyclerView.ViewHolder(itemViewBiding.root)
}