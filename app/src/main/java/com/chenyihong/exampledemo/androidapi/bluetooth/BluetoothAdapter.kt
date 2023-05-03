package com.chenyihong.exampledemo.androidapi.bluetooth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.R

class BluetoothAdapter : RecyclerView.Adapter<BluetoothAdapter.BluetoothViewHolder>() {

    private val bluetoothData = ArrayList<BluetoothEntity>()

    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothViewHolder {
        return BluetoothViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_bluetooth_item, parent, false))
    }

    override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(bluetoothData[position])
        }
    }

    override fun getItemCount(): Int {
        return bluetoothData.size
    }

    fun setNewData(bluetoothData: ArrayList<BluetoothEntity>?) {
        val lastItemCount = itemCount
        if (lastItemCount != 0) {
            this.bluetoothData.clear()
            notifyItemRangeRemoved(0, lastItemCount)
        }
        bluetoothData?.let { this.bluetoothData.addAll(it) }
        notifyItemChanged(0, itemCount)
    }

    interface ItemClickListener {
        fun onItemClick(bluetoothInfo: BluetoothEntity)
    }

    class BluetoothViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBluetoothName: AppCompatTextView = itemView.findViewById(R.id.tv_bluetooth_name)
        val tvBluetoothSSID: AppCompatTextView = itemView.findViewById(R.id.tv_bluetooth_ssid)
    }
}