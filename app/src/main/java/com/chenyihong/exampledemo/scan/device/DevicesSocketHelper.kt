package com.chenyihong.exampledemo.scan.device

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

class DevicesSocketHelper(private val messageListener: ((message: String) -> Unit)? = null) {

    private var webSocket: WebSocket? = null

    private val webSocketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            messageListener?.invoke(bytes.utf8())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            messageListener?.invoke(text)
        }
    }

    fun openSocketConnection(serverPath: String) {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder().url(serverPath).build()
        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
    }

    fun release() {
        webSocket?.close(1000, "")
        webSocket = null
    }
}