package com.spl.network;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .build();

    public static Response request(Request request) {

        Response response = null;
        try {
            Call call = okHttpClient.newCall(request);
            response = call.execute();
        } catch (IOException e) {
            System.out.println("OkHttpUtil, 网络请求失败， requestUrl=" + request);
            e.printStackTrace();
        }
        return response;

    }



}
