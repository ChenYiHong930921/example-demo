package com.chenyihong.exampledemo.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.entity.OptionEntity

class OptionAdapter : RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    private val optionList = ArrayList<OptionEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_option_item, parent, false)
        return OptionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val context = holder.itemView.context
        val option = optionList[position]
        holder.tvOptionName.text = option.optionName
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, option.targetClass))
        }
    }

    override fun getItemCount(): Int {
        return optionList.size
    }

    fun setNewData(optionList: ArrayList<OptionEntity>?) {
        optionList?.let {
            this@OptionAdapter.optionList.clear()
            this@OptionAdapter.optionList.addAll(optionList)
        }
        notifyItemRangeChanged(0, itemCount)
    }

    class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOptionName: TextView = itemView.findViewById(R.id.tv_option_name)
    }
}