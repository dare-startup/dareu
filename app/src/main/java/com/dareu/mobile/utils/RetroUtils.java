package com.dareu.mobile.utils;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jose.rubalcaba on 03/03/2017.
 */

public class RetroUtils {

    public static Retrofit getInstance(Context cxt){
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SharedUtils.getProperty(PropertyName.DEBUG_SERVER, cxt))
                .build();
    }
}
