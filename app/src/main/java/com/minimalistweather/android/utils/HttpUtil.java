package com.minimalistweather.android.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {

    /**
     * 与服务器交互
     * @param requestUrl 请求地址
     * @param callback 回调
     */
    public static void sendHttpRequest(String requestUrl, Callback callback) {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(requestUrl).build();
        httpClient.newCall(request).enqueue(callback);
    }
}
