package com.daniel.lojamobile.sync;

import android.content.Context;
import android.util.Log;


import com.daniel.lojamobile.util.NullOnEmptyConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SyncDefaut {
    private static String ip = "100.64.103.178";
    private static final String url = "http://100.64.102.23:8089/LojaServer/";


    public static final Retrofit RETROFIT_LOJA() {
        return new Retrofit.Builder().
                baseUrl("http://100.64.102.23:8089/LojaServer/").
                addConverterFactory(GsonConverterFactory.create()).
                build();

    }
}
