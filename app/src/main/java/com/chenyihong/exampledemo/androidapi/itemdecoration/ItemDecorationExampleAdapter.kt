package com.chenyihong.exampledemo.androidapi.itemdecoration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.databinding.LayoutCustomItemDecorationExampleItemBinding

class ItemDecorationExampleAdapter : RecyclerView.Adapter<ItemDecorationExampleAdapter.ItemDecorationExampleViewHolder>() {

    private val containerData = ArrayList<ItemDecorationExampleDataEntity>()

    private var lastSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDecorationExampleViewHolder {
        return ItemDecorationExampleViewHolder(LayoutCustomItemDecorationExampleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return containerData.size
    }

    override fun onBindViewHolder(holder: ItemDecorationExampleViewHolder, position: Int) {
        containerData[position].run {
            if (lastSelectedItem == -1 && selected) {
                lastSelectedItem = holder.bindingAdapterPosition
            }
            holder.itemViewBinding.ivTagIcon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, icon))
            holder.itemViewBinding.tvTagText.text = name
            holder.itemViewBinding.ctlContainer.isSelected = selected
            holder.itemView.setOnClickListener { selectItem(position) }
        }
    }

    fun setNewData(newData: ArrayList<ItemDecorationExampleDataEntity>?) {
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

    private fun selectItem(position: Int) {
        if (lastSelectedItem != position) {
            if (lastSelectedItem != -1) {
                containerData[lastSelectedItem].selected = false
                notifyItemChanged(lastSelectedItem)
                lastSelectedItem = position
            }
            if (position >= 0) {
                containerData[position].selected = true
                notifyItemChanged(position)
            }
        }
    }

    class ItemDecorationExampleViewHolder(val itemViewBinding: LayoutCustomItemDecorationExampleItemBinding) : RecyclerView.ViewHolder(itemViewBinding.root)
}