package com.chenyihong.exampledemo.localserver.nanohttpd

import androidx.media3.common.util.UnstableApi
import com.chenyihong.exampledemo.base.ExampleApplication
import fi.iki.elonen.NanoHTTPD
import java.net.URLConnection

@UnstableApi
class NanoHttpdServer : NanoHTTPD(8080) {

    override fun serve(session: IHTTPSession?): Response {
        var mimeType = "*/*"
        session?.run {
            try {
                mimeType = URLConnection.getFileNameMap().getContentTypeFor(session.uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return try {
            val assetsStream = ExampleApplication.exampleContext?.assets?.open("${session?.uri?.substring(1)}")
            if (assetsStream != null) {
                newChunkedResponse(Response.Status.OK, mimeType, assetsStream)
            } else {
                super.serve(session)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            super.serve(session)
        }
    }
}