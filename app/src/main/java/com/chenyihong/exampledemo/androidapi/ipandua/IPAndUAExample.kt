package com.chenyihong.exampledemo.androidapi.ipandua

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.text.format.Formatter
import android.webkit.WebSettings
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutConnectivityExampleActivityBinding
import java.net.NetworkInterface
import java.util.*

class IPAndUAExample : BaseGestureDetectorActivity() {

    private lateinit var binding: LayoutConnectivityExampleActivityBinding
    private var connectivityManager: ConnectivityManager? = null
    private var wifiManager: WifiManager? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_connectivity_example_activity)

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        binding.tvSystemUserAgent.text = "System UA:${System.getProperty("http.agent")}"
        binding.tvWebUserAgent.text = "WebView UA:${WebSettings.getDefaultUserAgent(this)}"
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            getIp()
        }
    }

    @SuppressLint("SetTextI18n", "ObsoleteSdkInt")
    private fun getIp() {
        connectivityManager?.run {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                when (activeNetworkInfo?.type) {
                    ConnectivityManager.TYPE_MOBILE -> {
                        binding.tvConnectedNetworkType.text = "Transport by Mobile"
                        binding.tvMobileIp.text = "Mobile IP:${getMobileIp()}"
                    }
                    ConnectivityManager.TYPE_WIFI -> {
                        binding.tvConnectedNetworkType.text = "Transport by WIFI"
                        binding.tvWifiIp.text = "WIFI IP:${getWIFIIp()}"
                    }
                    else -> {}
                }
            } else {
                // Android M 以上建议使用getNetworkCapabilities API
                activeNetwork?.let { network ->
                    getNetworkCapabilities(network)?.let { networkCapabilities ->
                        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                            when {
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                                    binding.tvConnectedNetworkType.text = "Transport by Mobile"
                                    binding.tvMobileIp.text = "Mobile IP:${getMobileIp()}"
                                }
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                                    binding.tvConnectedNetworkType.text = "Transport by WIFI"
                                    binding.tvWifiIp.text = "WIFI IP:${getWIFIIp()}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getMobileIp(): String {
        var mobileIp = "0.0.0.0"
        NetworkInterface.getNetworkInterfaces().let {
            loo@ for (networkInterface in Collections.list(it)) {
                for (inetAddresses in Collections.list(networkInterface.inetAddresses)) {
                    if (!inetAddresses.isLoopbackAddress && !inetAddresses.isLinkLocalAddress) {
                        inetAddresses.hostAddress?.let { hostAddress ->
                            mobileIp = hostAddress
                        }
                        break@loo
                    }
                }
            }
        }
        return mobileIp
    }

    private fun getWIFIIp(): String {
        var wifiIp = "0.0.0.0"
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            wifiManager?.run {
                wifiIp = Formatter.formatIpAddress(connectionInfo.ipAddress)
            }
        } else {
            // Android Q 以上建议使用getNetworkCapabilities API
            connectivityManager?.run {
                activeNetwork?.let { network ->
                    (getNetworkCapabilities(network)?.transportInfo as? WifiInfo)?.let { wifiInfo ->
                        wifiIp = Formatter.formatIpAddress(wifiInfo.ipAddress)
                    }
                }
            }
        }
        return wifiIp
    }
}