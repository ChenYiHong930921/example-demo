package com.chenyihong.exampledemo.androidapi.gaid

import android.os.Bundle
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import androidx.ads.identifier.AdvertisingIdNotAvailableException
import androidx.databinding.DataBindingUtil
import com.chenyihong.exampledemo.R
import com.chenyihong.exampledemo.androidapi.gesturedetector.BaseGestureDetectorActivity
import com.chenyihong.exampledemo.databinding.LayoutGaidActivityBinding
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeoutException

const val TAG = "GAIDTag"

class GaIdActivity : BaseGestureDetectorActivity() {

    private lateinit var binding: LayoutGaidActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_gaid_activity)
        binding.includeTitle.tvTitle.text = "GAIDExample"
        getGAIDViaGoogle()

    }

    private fun getGAIDViaGoogle() {
        Thread {
            try {
                val advertisingIdInfo = com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo(this)
                if (!advertisingIdInfo.isLimitAdTrackingEnabled) {
                    val gaId = advertisingIdInfo.id
                    binding.tvGaid.run { post { text = "Google GAID:$gaId" } }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            }
            getGAIDViaJetpack()
        }.start()
    }

    private fun getGAIDViaJetpack() {
        // 必须先判断是否有广告id提供者
        if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(this)) {
            try {
                val advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)
                Futures.addCallback(advertisingIdInfo, object : FutureCallback<AdvertisingIdInfo> {
                    override fun onSuccess(result: AdvertisingIdInfo?) {
                        //获取成功
                        result?.run {
                            if (!isLimitAdTrackingEnabled) {
                                val gaId = result.id
                                binding.tvGaid.run {
                                    post {
                                        val oldText = text
                                        text = "$oldText\nJetpack GAID:$gaId"
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(t: Throwable) {
                        // 获取失败
                    }
                }, Executors.newSingleThreadExecutor())
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: AdvertisingIdNotAvailableException) {
                e.printStackTrace()
            } catch (e: TimeoutException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}