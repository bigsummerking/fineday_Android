package com.example.finedayapp.StarMovie;

import android.util.Log;

public class L {
    private static final String TAG = "水牛电影";
    private static boolean debug=true;
    public  static void  e(String msg){

        if(debug){
            Log.e(TAG,msg);
        }
    }
}
