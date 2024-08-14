package com.chenyihong.plugin.api;

import okhttp3.ResponseBody;

public interface RequestCallback {

    /**
     * 请求回调
     *
     * @param success      是否成功
     * @param responseBody 回应体
     */
    void onResponse(boolean success, ResponseBody responseBody);

    /**
     * 请求失败
     *
     * @param errorMessage 错误信息
     */
    void onFailure(String errorMessage);
}
