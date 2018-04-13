package com.apkfuns.androidsourceviewer.util;

/**
 * Created by pengwei on 17/2/23.
 */
public class Log {

    private static final boolean DEBUG = true;

    public static void debug(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }

    public static void e(Throwable msg) {
        if (DEBUG) {
            msg.printStackTrace();
        }
    }
}
