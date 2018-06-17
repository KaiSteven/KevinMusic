package com.example.asus.codingplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.lidroid.xutils.DbUtils;

import com.example.asus.codingplayer.utils.Constant;

import cn.bmob.v3.Bmob;

public class CodingPlayerAPP extends Application {

    public static SharedPreferences sp;
    public static DbUtils dbUtils;
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        dbUtils = DbUtils.create(getApplicationContext(),Constant.DB_NAME);
        context = getApplicationContext();
        //初始化Bmob
        Bmob.initialize(this, "b370986e199df0a270abe17e09752723");
    }

}
