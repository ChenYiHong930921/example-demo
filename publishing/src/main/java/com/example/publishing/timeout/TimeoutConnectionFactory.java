package com.example.publishing.timeout;

import com.google.api.client.http.javanet.ConnectionFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class TimeoutConnectionFactory implements ConnectionFactory {

    private final Proxy proxy;

    private final int timeout;

    public TimeoutConnectionFactory() {
        this(null, 0);
    }

    public TimeoutConnectionFactory(int timeout) {
        this(null, timeout);
    }

    public TimeoutConnectionFactory(Proxy proxy, int timeout) {
        this.proxy = proxy;
        this.timeout = timeout;
    }

    @Override
    public HttpURLConnection openConnection(URL url) throws IOException, ClassCastException {
        HttpURLConnection urlConnection = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));
        if (timeout != 0) {
            System.out.println("openConnection url:" + url + ", timeout:" + timeout);
            // 设置超时时间
            urlConnection.setConnectTimeout(timeout);
            urlConnection.setReadTimeout(timeout);
        }
        return urlConnection;
    }
}
