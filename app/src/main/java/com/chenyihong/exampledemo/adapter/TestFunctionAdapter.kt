package com.chenyihong.exampledemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.entity.OptionsChildEntity

class TestFunctionAdapter : RecyclerView.Adapter<TestFunctionAdapter.TestFunctionViewHolder>() {

    private val testFunctions = ArrayList<OptionsChildEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestFunctionViewHolder {
        return TestFunctionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_options_child_item, parent, false))
    }

    override fun onBindViewHolder(holder: TestFunctionViewHolder, position: Int) {
        val functionEntity = testFunctions[position]
        holder.tvFunctionName.text = functionEntity.testFunctionName
        holder.vDividerBottom.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE
        holder.itemView.setOnClickListener {
            functionEntity.testFunction.invoke()
        }
    }

    override fun getItemCount(): Int {
        return testFunctions.size
    }

    fun setNewData(testFunctions: ArrayList<OptionsChildEntity>?) {
        val currentItemCount = itemCount
        if (currentItemCount != 0) {
            this.testFunctions.clear()
            notifyItemRangeRemoved(0, currentItemCount)
        }
        if (!testFunctions.isNullOrEmpty()) {
            this.testFunctions.addAll(testFunctions)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    class TestFunctionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vDividerBottom: View = itemView.findViewById(R.id.v_divider_bottom)
        val tvFunctionName: AppCompatTextView = itemView.findViewById(R.id.tv_test_function_name)
    }
}