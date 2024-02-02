package com.chenyihong.exampledemo.localserver.andserver

import android.content.Context
import android.os.Environment
import com.yanzhenjie.andserver.annotation.Config
import com.yanzhenjie.andserver.framework.config.WebConfig
import com.yanzhenjie.andserver.framework.website.AssetsWebsite
import com.yanzhenjie.andserver.framework.website.StorageWebsite
import java.io.File

@Config
class AndServerConfig : WebConfig {

    override fun onConfig(context: Context?, delegate: WebConfig.Delegate?) {
        context?.run { delegate?.addWebsite(AssetsWebsite(this, "/assetsweb/")) }
        context?.run {
            delegate?.addWebsite(StorageWebsite(File(if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), packageName)
            } else {
                File(filesDir, packageName)
            }, "storageweb").absolutePath))
        }
    }
}