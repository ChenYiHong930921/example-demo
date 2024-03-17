package com.chenyihong.exampledemo.scan.server

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

class DevicesSocketHelper(private val messageListener: ((message: String) -> Unit)? = null) {

    private var webSocket: WebSocket? = null

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.i(TEST_LOG, "Devices Socket onOpen")
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            Log.i(TEST_LOG, "Devices Socket onMessage bytes:$bytes")
            messageListener?.invoke(bytes.utf8())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.i(TEST_LOG, "Devices Socket onMessage text:$text")
            messageListener?.invoke(text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            Log.i(TEST_LOG, "Devices Socket onClosing code:$code, reason:$reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.i(TEST_LOG, "Devices Socket onClosed code:$code, reason:$reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.e(TEST_LOG, "Devices Socket onFailure error:${t.message}")
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

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun closeConnection() {
        webSocket?.close(1000, "")
    }

    fun release() {
        closeConnection()
        webSocket = null
    }
}