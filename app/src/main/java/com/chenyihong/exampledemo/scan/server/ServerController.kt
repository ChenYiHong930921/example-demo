package com.chenyihong.exampledemo.scan.server

import fi.iki.elonen.NanoHTTPD

object ServerController {

    private var serverSocketClient: ServerSocketClient? = null
    private var serverHttpClient: ServerHttpClient? = null

    fun startServer() {
        (serverSocketClient ?: ServerSocketClient().also {
            serverSocketClient = it
        }).start(0)

        (serverHttpClient ?: ServerHttpClient {
            serverSocketClient?.sendMessage("Login Succeed, user id is $it")
        }.also {
            serverHttpClient = it
        }).start(NanoHTTPD.SOCKET_READ_TIMEOUT, true)
    }

    fun stopServer() {
        serverSocketClient?.stop()
        serverSocketClient = null

        serverHttpClient?.stop()
        serverHttpClient = null
    }
}