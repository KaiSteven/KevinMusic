package com.example.asus.codingplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.asus.codingplayer.utils.Contant;

public class CodingPlayerAPP extends Application {

    public static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Contant.SP_NAME, Context.MODE_PRIVATE);
    }
}
