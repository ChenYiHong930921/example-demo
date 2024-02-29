package com.chenyihong.exampledemo.tripartite.admob

const val ADVERTISE_TYPE_INTERSTITIAL = 1
const val ADVERTISE_TYPE_REWARDED = 2
const val ADVERTISE_TYPE_BANNER = 3

interface AdvertiseStatusListener {

    fun availableChange(adType: Int, enable: Boolean)

    fun showFailure(message: String)
}