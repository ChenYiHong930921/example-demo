package com.chenyihong.exampledemo.api

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object OkHttpHelper {

    private var okHttpClient: OkHttpClient? = null

    private val jsonMediaType = "application/json;charset=UTF-8".toMediaTypeOrNull()

    @JvmStatic
    fun isInit(): Boolean {
        return okHttpClient != null
    }

    @JvmStatic
    @JvmOverloads
    fun init(connectTimeout: Long = 5, callTimeout: Long = 10, readTimeout: Long = 10) {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .callTimeout(callTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .build()
        }
    }

    @JvmStatic
    fun convertJsonToRequestBody(jsonString: String): RequestBody {
        return jsonString.toRequestBody(jsonMediaType)
    }

    @JvmStatic
    fun sendGetRequest(url: String, params: Map<String, Any?>, requestCallback: RequestCallback? = null) {
        url.toHttpUrlOrNull()?.run {
            val urlBuild = newBuilder()
            params.entries.forEach { urlBuild.addQueryParameter(it.key, it.value.toString()) }
            sendRequest(Request.Builder()
                .get()
                .url(urlBuild.build())
                .build(), requestCallback)
        }
    }

    @JvmStatic
    fun sendPostRequest(url: String, params: Map<String, Any?>, requestCallback: RequestCallback? = null) {
        try {
            val requestJson = JSONObject()
            params.entries.forEach { requestJson.put(it.key, it.value) }
            sendRequest(Request.Builder()
                .post(convertJsonToRequestBody(requestJson.toString()))
                .url(url)
                .build(), requestCallback)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun sendRequest(request: Request, requestCallback: RequestCallback? = null) {
        okHttpClient?.run {
            newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    requestCallback?.onResponse(response.isSuccessful, response.body)

                }

                override fun onFailure(call: Call, e: IOException) {
                    requestCallback?.onFailure(e.message)
                }
            })
        }
    }
}