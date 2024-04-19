package com.chenyihong.exampledemo.roomandexcel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.databinding.LayoutTextContentItemBinding
import com.chenyihong.exampledemo.roomandexcel.entity.BlogExampleEntity

class BlogAdapter : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val containerData = ArrayList<BlogExampleEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        return BlogViewHolder(LayoutTextContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return containerData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        containerData[position].run {
            holder.binding.tvContent.text = "title:$title\nsummary:$summary\ncontent:$content"
        }
    }

    fun setNewData(newData: List<BlogExampleEntity>?) {
        val lastItemCount = itemCount
        if (lastItemCount != 0) {
            containerData.clear()
            notifyItemRangeRemoved(0, lastItemCount)
        }
        newData?.let { containerData.addAll(it) }
        notifyItemChanged(0, itemCount)
    }

    class BlogViewHolder(val binding: LayoutTextContentItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ctlTextContentContainer.updateLayoutParams<RecyclerView.LayoutParams> {
                height = RecyclerView.LayoutParams.WRAP_CONTENT
            }
        }
    }
}