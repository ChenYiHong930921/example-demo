package com.chenyihong.exampledemo.web.reserve

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import com.chenyihong.exampledemo.databinding.LayoutWebViewFrontPageActivityBinding
import com.chenyihong.exampledemo.web.PARAMS_LINK_URL

class WebViewFrontPageActivity : AppCompatActivity() {

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutWebViewFrontPageActivityBinding.inflate(layoutInflater).let {
            setContentView(it.root)
            it.btnWebsiteGame1.setOnClickListener {
                startActivity(Intent(this, ReservePageExampleActivity::class.java).apply {
                    putExtra(PARAMS_LINK_URL,"https://go.minigame.vip/game/popstone2/play")
                })
            }
            it.btnWebsiteGame2.setOnClickListener {
                startActivity(Intent(this, ReservePageExampleActivity::class.java).apply {
                    putExtra(PARAMS_LINK_URL,"https://go.minigame.vip/game/bubble-spinner/play")
                })
            }
        }
    }
}