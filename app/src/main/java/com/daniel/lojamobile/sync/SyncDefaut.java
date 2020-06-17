package com.daniel.lojamobile.sync;

import android.content.Context;
import android.util.Log;


import com.daniel.lojamobile.modelo.persistencia.BdServidor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SyncDefaut {
    private static String ip = "192.168.0.6";
    private String url;

    public static String getUrl(Context c) {

        BdServidor bd = new BdServidor(c);
        String ip = bd.listar().getIp();
        if (!ip.equals("")) {
            Log.i("[IFMG]", "" + ip);
            return "http://" + ip + ":8089/LojaServer/";
        } else {
            Log.i("[IFMG]", "localhost");
            return "http://localhost:8089/LojaServer/";
        }
    }


    public static final Retrofit RETROFIT_LOJA(Context c) {
        return new Retrofit.Builder().
                baseUrl(getUrl(c)).
                addConverterFactory(GsonConverterFactory.create()).
                build();

    }
}
