package com.santoni7.readme.util;

import android.util.Log;

public class Logger {
    private String tag;


    public Logger(String tag){
        this.tag = tag;
    }

    public void i(String msg){
        Log.i(tag, msg);
    }
    public void i(String msg, Throwable t){
        Log.i(tag, msg, t);
    }

    public void i(String event, String msg){
        this.i(formatEventMessage(event, msg));
    }

    public void d(String msg){
        Log.d(tag, msg);
    }

    public void d(String msg, Throwable t){
        Log.d(tag, msg, t);
    }

    public void d(String event, String msg){
        this.d(formatEventMessage(event, msg));
    }

    public void e(String msg){
        Log.e(tag, msg);
    }

    public void e(String event, String msg){
        this.e(formatEventMessage(event, msg));
    }

    private String formatEventMessage(String event, String msg){
        final String fmt = "[%s]: %s";

        return String.format(fmt, event, msg);
    }
}
