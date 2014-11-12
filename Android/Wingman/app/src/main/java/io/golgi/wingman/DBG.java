package io.golgi.wingman;

import android.util.Log;

/**
 * Created by brian on 10/23/14.
 */
public class DBG {
    public static void write(String where, String str){
            Log.i(where, str);
        }

    public static void write(String str){
            write("WINGMAN", str);
        }
}
