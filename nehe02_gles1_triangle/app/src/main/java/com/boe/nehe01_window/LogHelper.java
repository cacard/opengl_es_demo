package com.boe.nehe01_window;

import android.util.Log;

public class LogHelper {

    public static void log(String methodName, String msg) {
        Log.i("_MainActivity_", String.format("【%s】 %s", methodName, msg));
    }

}
