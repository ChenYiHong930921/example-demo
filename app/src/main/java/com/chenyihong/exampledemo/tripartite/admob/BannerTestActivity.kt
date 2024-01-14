package com.chenyihong.exampledemo.tripartite.admob

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chenyihong.exampledemo.databinding.LayoutBannerTestActivityBinding

class BannerTestActivity : AppCompatActivity() {

    private var bannerOnTop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LayoutBannerTestActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        val controller = AdMobController().apply {
            initSdk(this@BannerTestActivity) { succeed ->
                binding.btnShowBanner.isEnabled = succeed
                binding.btnHideBanner.isEnabled = succeed
                binding.btnChangeBannerResidentStatus.isEnabled = succeed
            }
        }

        binding.btnShowBanner.setOnClickListener {
            controller.showBanner(bannerOnTop)
        }
        binding.btnHideBanner.setOnClickListener {
            controller.hideBanner()
            bannerOnTop = !bannerOnTop
        }
        binding.btnChangeBannerResidentStatus.setOnClickListener {
            controller.enableBannerResident = !controller.enableBannerResident
        }
    }
}