package com.chenyihong.plugin.api;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpHelper {

    private OkHttpClient client;


    public void init() {
        //网络请求
        client = new OkHttpClient.Builder().build();
    }

    /**
     * 下载文件
     *
     * @param downloadPath    文件下载路径
     * @param requestCallback 请求回调
     */
    public void downloadFileFromPath(String downloadPath, RequestCallback requestCallback) {
        Request request = new Request.Builder()
                .url(downloadPath)
                .build();

        sendRequest(request, requestCallback);
    }

    private void sendRequest(Request request, RequestCallback requestCallback) {
        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            requestCallback.onResponse(response.isSuccessful(), response.body());
            response.close();
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            requestCallback.onFailure("request to " + request.url().url() + " failure errorMessage:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
