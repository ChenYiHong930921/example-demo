package com.chenyihong.exampledemo.androidapi.bluetooth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BluetoothEntity(
    val name: String?,
    val ssid: String?
) : Parcelable{

    override fun toString(): String {
        return "BluetoothEntity(name=$name, ssid=$ssid)"
    }
}
