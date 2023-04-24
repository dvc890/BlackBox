package com.wrbug.dumpdex.dump;

import com.jaredrummler.android.shell.BuildConfig;
import com.wrbug.dumpdex.Native;

import de.robv.android.xposed.XposedBridge;

/**
 * OreoDump
 *
 * @author WrBug
 * @since 2018/3/23
 */
public class OreoDump {

    public static void log(String txt) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        XposedBridge.log("dumpdex-> " + txt);
    }

    public static void init(final String packageName) {
        Native.dump(packageName);
    }
}
