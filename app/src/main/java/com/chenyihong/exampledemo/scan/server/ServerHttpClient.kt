package com.chenyihong.exampledemo.scan.server

import fi.iki.elonen.NanoHTTPD

const val APP_SCAN_INTERFACE = "loginViaScan"

const val USER_ID = "userId"
const val EXAMPLE_USER_ID = "123456789"

const val DEVICE_ID = "deviceId"
const val EXAMPLE_DEVICE_ID = "example_device_id0001"

class ServerHttpClient(private var scanLoginSucceedListener: ((userId: String) -> Unit)? = null) : NanoHTTPD(8080) {

    override fun serve(session: IHTTPSession?): Response {
        val uri = session?.uri
        return if (uri == "/$APP_SCAN_INTERFACE" &&
            session.parameters[USER_ID]?.first() == EXAMPLE_USER_ID &&
            session.parameters[DEVICE_ID]?.first() == EXAMPLE_DEVICE_ID
        ) {
            scanLoginSucceedListener?.invoke(session.parameters[USER_ID]?.first() ?: "")
            newFixedLengthResponse("Login Succeed")
        } else {
            super.serve(session)
        }
    }
}