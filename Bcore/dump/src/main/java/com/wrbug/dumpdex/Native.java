package com.wrbug.dumpdex;

import de.robv.android.xposed.XposedBridge;

/**
 * Native
 *
 * @author WrBug
 * @since 2018/3/23
 */
public class Native {
    public static void log(String txt) {
        XposedBridge.log("dumpdex.Native-> " + txt);
    }
    static {
        System.loadLibrary("nativeDump");
        log("loaded libnativeDump.so");
    }

    public static native void dump(String packageName);
}
