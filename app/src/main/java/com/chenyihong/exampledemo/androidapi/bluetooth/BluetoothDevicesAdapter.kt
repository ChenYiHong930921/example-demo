package com.chenyihong.exampledemo.androidapi.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.R

class BluetoothDevicesAdapter : RecyclerView.Adapter<BluetoothDevicesAdapter.BluetoothViewHolder>() {

    private val bluetoothData = ArrayList<BluetoothDevice>()

    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothViewHolder {
        return BluetoothViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_bluetooth_item, parent, false))
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(bluetoothData[position])
        }
        holder.tvBluetoothName.text = "Name:${bluetoothData[position].name}"
        holder.tvBluetoothMac.text = "MacAddress:${bluetoothData[position].address}"
    }

    override fun getItemCount(): Int {
        return bluetoothData.size
    }

    fun setNewData(bluetoothData: ArrayList<BluetoothDevice>?) {
        val lastItemCount = itemCount
        if (lastItemCount != 0) {
            this.bluetoothData.clear()
            notifyItemRangeRemoved(0, lastItemCount)
        }
        bluetoothData?.let { this.bluetoothData.addAll(it) }
        notifyItemChanged(0, itemCount)
    }

    fun addSingleData(bluetoothData: BluetoothDevice?) {
        bluetoothData?.let {
            this.bluetoothData.add(it)
            notifyItemChanged(0, itemCount)
        }
    }

    interface ItemClickListener {
        fun onItemClick(bluetoothDevice: BluetoothDevice)
    }

    class BluetoothViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBluetoothName: AppCompatTextView = itemView.findViewById(R.id.tv_bluetooth_name)
        val tvBluetoothMac: AppCompatTextView = itemView.findViewById(R.id.tv_bluetooth_mac)
    }
}