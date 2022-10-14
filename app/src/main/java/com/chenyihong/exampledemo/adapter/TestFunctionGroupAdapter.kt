package com.chenyihong.exampledemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chenyihong.exampledemo.R

import com.minigame.testapp.ui.entity.OptionsEntity

class TestFunctionGroupAdapter : RecyclerView.Adapter<TestFunctionGroupAdapter.TestFunctionGroupViewHolder>() {

    private val functionGroup = ArrayList<OptionsEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestFunctionGroupViewHolder {
        return TestFunctionGroupViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_options_parent_item, parent, false))
    }

    override fun onBindViewHolder(holder: TestFunctionGroupViewHolder, position: Int) {
        val functionGroupEntity = functionGroup[position]
        holder.vDividerTop.visibility = if (position == 0) View.VISIBLE else View.GONE
        holder.tvFunctionGroupName.text = functionGroupEntity.moduleName
        holder.tvFunctionGroupName.setOnClickListener {
            functionGroupEntity.expanded = !functionGroupEntity.expanded
            notifyItemChanged(position)
        }
        holder.rvTestFunction.visibility = if (functionGroupEntity.expanded) View.VISIBLE else View.GONE
        holder.vDividerMiddle.visibility = if (functionGroupEntity.expanded) View.VISIBLE else View.GONE
        holder.testFunctionAdapter?.setNewData(functionGroupEntity.containerTest)
    }

    override fun getItemCount(): Int {
        return functionGroup.size
    }

    fun setNewData(functionGroup: ArrayList<OptionsEntity>?) {
        val currentItemCount = itemCount
        if (currentItemCount != 0) {
            this.functionGroup.clear()
            notifyItemRangeRemoved(0, currentItemCount)
        }
        if (!functionGroup.isNullOrEmpty()) {
            this.functionGroup.addAll(functionGroup)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    class TestFunctionGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vDividerTop: View = itemView.findViewById(R.id.v_divider_top)
        val vDividerMiddle: View = itemView.findViewById(R.id.v_divider_middle)
        val tvFunctionGroupName: AppCompatTextView = itemView.findViewById(R.id.tv_group_name)
        val rvTestFunction: RecyclerView = itemView.findViewById(R.id.rv_test_function)
        var testFunctionAdapter: TestFunctionAdapter? = null

        init {
            testFunctionAdapter = TestFunctionAdapter()
            rvTestFunction.adapter = testFunctionAdapter
            val itemAnimator = rvTestFunction.itemAnimator
            if (itemAnimator != null) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
        }
    }
}