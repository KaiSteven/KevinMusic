package com.example.asus.codingplayer.utils;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.asus.codingplayer.*;

public class AppUtils {

    //隐藏键盘
    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) CodingPlayerAPP.context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()) {//判断是否已经激活
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
