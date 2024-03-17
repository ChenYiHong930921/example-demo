package com.chenyihong.exampledemo.scan.server

import android.util.Log
import fi.iki.elonen.NanoHTTPD

const val APP_SCAN_INTERFACE = "loginViaScan"
const val USER_ID = "userId"
const val DEVICE_ID = "deviceId"

class ServerHttpClient : NanoHTTPD(8080) {

    init {
        Log.i(TEST_LOG, "ServerHttpClient init")
    }

    override fun serve(session: IHTTPSession?): Response {
        val uri = session?.uri
        Log.i(TEST_LOG, "NanoHttpDServerClient serve uri:$uri")
        return if (uri == null) {
            super.serve(session)
        } else {
            if (uri == "/$APP_SCAN_INTERFACE") {
                session.parameters.entries.forEach {
                    Log.i(TEST_LOG, "key:${it.key}, value:${it.value}")
                }
                newFixedLengthResponse("Login Succeed")
            } else {
                super.serve(session)
            }
        }
    }
}