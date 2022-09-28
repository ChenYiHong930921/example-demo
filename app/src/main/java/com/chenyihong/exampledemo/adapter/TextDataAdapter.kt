package com.chenyihong.exampledemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.R

class TextDataAdapter : RecyclerView.Adapter<TextDataAdapter.TextDataViewHolder>() {

    private val textData = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextDataViewHolder {
        return TextDataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_text_content_item, parent, false))
    }

    override fun onBindViewHolder(holder: TextDataViewHolder, position: Int) {
        holder.tvTextDataContent.text = textData[position]
    }

    override fun getItemCount(): Int {
        return textData.size
    }

    fun setNewData(searchData: ArrayList<String>?) {
        val lastItemCount = itemCount
        if (lastItemCount != 0) {
            this.textData.clear()
            notifyItemRangeRemoved(0, lastItemCount)
        }
        searchData?.let { this.textData.addAll(it) }
        notifyItemChanged(0, itemCount)
    }

    class TextDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTextDataContent: AppCompatTextView = itemView.findViewById(R.id.tv_content)
    }
}