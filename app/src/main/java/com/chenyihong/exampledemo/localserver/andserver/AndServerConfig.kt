package com.chenyihong.exampledemo.localserver.andserver

import android.content.Context
import com.yanzhenjie.andserver.annotation.Config
import com.yanzhenjie.andserver.framework.config.WebConfig
import com.yanzhenjie.andserver.framework.website.AssetsWebsite

@Config
class AndServerConfig : WebConfig {

    override fun onConfig(context: Context?, delegate: WebConfig.Delegate?) {
        context?.run { delegate?.addWebsite(AssetsWebsite(this, "/localweb/")) }
    }
}