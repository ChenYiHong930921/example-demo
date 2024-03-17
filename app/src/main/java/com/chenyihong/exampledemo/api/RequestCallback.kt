package com.chenyihong.exampledemo.api

import okhttp3.ResponseBody

interface RequestCallback {

    fun onResponse(success: Boolean, responseBody: ResponseBody?)

    fun onFailure(errorMessage: String?)
}