package com.chenyihong.exampledemo.localserver.nanohttpd

import android.content.Context
import android.os.Environment
import android.text.TextUtils.substring
import android.util.Log
import androidx.media3.common.util.UnstableApi
import com.chenyihong.exampledemo.base.ExampleApplication
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream
import java.net.URLConnection

@UnstableApi
class NanoHttpdServer(private val context: Context) : NanoHTTPD(8080) {

    var openFromStorage = false

    override fun serve(session: IHTTPSession?): Response {
        var mimeType = "*/*"
        session?.run {
            try {
                // 根据链接获取 mimeType
                mimeType = URLConnection.getFileNameMap().getContentTypeFor(session.uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return try {
            val uri = session?.uri
            if (uri == null) {
                super.serve(session)
            } else {
                if (openFromStorage) {
                    // 打开存储空间内的H5资源
                    newChunkedResponse(Response.Status.OK, mimeType, FileInputStream(File(if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                        File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), context.packageName)
                    } else {
                        File(context.filesDir, context.packageName)
                    }, uri)))
                } else {
                    // 打开assets下的H5资源
                    newChunkedResponse(Response.Status.OK, mimeType, context.assets.open(uri.substring(1)))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            super.serve(session)
        }
    }
}