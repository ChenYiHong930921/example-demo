package com.chenyihong.exampledemo.androidapi.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.databinding.LayoutAutoLoadMoreExampleItemHorizontalBinding
import com.chenyihong.exampledemo.databinding.LayoutAutoLoadMoreExampleItemVerticalBinding

class AutoLoadMoreExampleAdapter(private val vertical: Boolean = true) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val containerData = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (vertical) {
            AutoLoadMoreItemVerticalViewHolder(LayoutAutoLoadMoreExampleItemVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            AutoLoadMoreItemHorizontalViewHolder(LayoutAutoLoadMoreExampleItemHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemCount(): Int {
        return containerData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AutoLoadMoreItemVerticalViewHolder -> {
                holder.itemViewBinding.tvTextContent.text = containerData[position]
            }

            is AutoLoadMoreItemHorizontalViewHolder -> {
                holder.itemViewBinding.tvTextContent.text = containerData[position]
            }
        }
    }

    fun setNewData(newData: ArrayList<String>) {
        val currentItemCount = itemCount
        if (currentItemCount != 0) {
            containerData.clear()
            notifyItemRangeRemoved(0, currentItemCount)
        }
        if (newData.isNotEmpty()) {
            containerData.addAll(newData)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    fun addData(newData: ArrayList<String>) {
        val currentItemCount = itemCount
        if (newData.isNotEmpty()) {
            this.containerData.addAll(newData)
            notifyItemRangeChanged(currentItemCount, itemCount)
        }
    }

    class AutoLoadMoreItemVerticalViewHolder(val itemViewBinding: LayoutAutoLoadMoreExampleItemVerticalBinding) : RecyclerView.ViewHolder(itemViewBinding.root)

    class AutoLoadMoreItemHorizontalViewHolder(val itemViewBinding: LayoutAutoLoadMoreExampleItemHorizontalBinding) : RecyclerView.ViewHolder(itemViewBinding.root)
}