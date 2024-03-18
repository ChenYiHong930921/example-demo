package com.chenyihong.exampledemo.scan.server

import fi.iki.elonen.NanoWSD
import java.io.IOException

class ServerSocketClient : NanoWSD(9090) {

    private var serverWebSocket: ServerWebSocket? = null

    override fun openWebSocket(handshake: IHTTPSession?): WebSocket {
        return ServerWebSocket(handshake).also { serverWebSocket = it }
    }

    private class ServerWebSocket(handshake: IHTTPSession?) : WebSocket(handshake) {
        override fun onOpen() {}

        override fun onClose(code: WebSocketFrame.CloseCode?, reason: String?, initiatedByRemote: Boolean) {}

        override fun onMessage(message: WebSocketFrame?) {}

        override fun onPong(pong: WebSocketFrame?) {}

        override fun onException(exception: IOException?) {}
    }

    override fun stop() {
        super.stop()
        serverWebSocket = null
    }

    fun sendMessage(message: String) {
        serverWebSocket?.send(message)
    }
}