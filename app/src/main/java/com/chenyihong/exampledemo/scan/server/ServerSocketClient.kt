package com.chenyihong.exampledemo.scan.server

import android.util.Log
import fi.iki.elonen.NanoWSD
import java.io.IOException

const val TEST_LOG = "testlog"

class ServerSocketClient : NanoWSD(9090) {

    override fun openWebSocket(handshake: IHTTPSession?): WebSocket {
        Log.i(TEST_LOG, "ServerSocketClient openWebSocket uri:${handshake?.uri}")
        return ServerWebSocket(handshake)
    }

    private class ServerWebSocket(handshake: IHTTPSession?) : WebSocket(handshake) {
        override fun onOpen() {
            Log.i(TEST_LOG, "server websocket onOpen")
        }

        override fun onClose(code: WebSocketFrame.CloseCode?, reason: String?, initiatedByRemote: Boolean) {
            Log.i(TEST_LOG, "server websocket onClose code:$code, reason:$reason, initiatedByRemote:$initiatedByRemote")
        }

        override fun onMessage(message: WebSocketFrame?) {
            Log.i(TEST_LOG, "server websocket onMessage message:$message")
        }

        override fun onPong(pong: WebSocketFrame?) {
            Log.i(TEST_LOG, "server websocket onPong pong:$pong")
        }

        override fun onException(exception: IOException?) {
            Log.e(TEST_LOG, "server websocket onException:${exception?.message}")
        }
    }
}