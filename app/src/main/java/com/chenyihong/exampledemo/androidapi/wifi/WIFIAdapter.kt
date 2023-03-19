package com.chenyihong.exampledemo.androidapi.wifi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.chenyihong.exampledemo.R

class WIFIAdapter : RecyclerView.Adapter<WIFIAdapter.WIFIViewHolder>() {

    private val wifiData = ArrayList<WIFIEntity>()

    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WIFIViewHolder {
        return WIFIViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_wifi_item, parent, false))
    }

    override fun onBindViewHolder(holder: WIFIViewHolder, position: Int) {
        wifiData[position].run {
            holder.tvWifiName.text = wifiSSID
            holder.tvWifiSSID.text = wifiBSSID
            holder.ivWifiStrength.setImageResource(getStrengthIcon(wifiStrength))
            holder.ivNeedPassword.setImageResource(if (needPassword) R.drawable.icon_lock else R.drawable.icon_unlock)
        }
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(wifiData[position])
        }
    }

    override fun getItemCount(): Int {
        return wifiData.size
    }

    fun setNewData(wifiData: ArrayList<WIFIEntity>?) {
        val lastItemCount = itemCount
        if (lastItemCount != 0) {
            this.wifiData.clear()
            notifyItemRangeRemoved(0, lastItemCount)
        }
        wifiData?.let { this.wifiData.addAll(it) }
        notifyItemChanged(0, itemCount)
    }

    private fun getStrengthIcon(wifiStrength: Int): Int {
        return when (wifiStrength) {
            0 -> R.drawable.wifi_strength_0
            1 -> R.drawable.wifi_strength_1
            2 -> R.drawable.wifi_strength_2
            else -> R.drawable.wifi_strength_3
        }
    }

    interface ItemClickListener {
        fun onItemClick(wifiInfo: WIFIEntity)
    }

    class WIFIViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWifiName: AppCompatTextView = itemView.findViewById(R.id.tv_wifi_name)
        val tvWifiSSID: AppCompatTextView = itemView.findViewById(R.id.tv_wifi_ssid)
        val ivNeedPassword: AppCompatImageView = itemView.findViewById(R.id.iv_need_password)
        val ivWifiStrength: AppCompatImageView = itemView.findViewById(R.id.iv_wifi_strength)
    }
}