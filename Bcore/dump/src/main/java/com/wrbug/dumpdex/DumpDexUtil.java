package com.wrbug.dumpdex;

import android.content.Context;
import android.os.Build;

import com.jaredrummler.android.shell.BuildConfig;
import com.wrbug.dumpdex.dump.LowSdkDump;
import com.wrbug.dumpdex.dump.OreoDump;
import com.wrbug.dumpdex.util.DeviceUtils;

import java.io.File;

import de.robv.android.xposed.XposedBridge;

/**
 * XposedInit
 *
 * @author wrbug
 * @since 2018/3/20
 */
public class DumpDexUtil {

    public static void log(String txt) {
        XposedBridge.log("dumpdex-> " + txt);
    }

    public static void log(Throwable t) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        XposedBridge.log(t);
    }

    public static boolean dump(Context context) {
        if (context == null) {
            return false;
        }
        PackerInfo.Type type = PackerInfo.find(context.getClassLoader());
        if (type == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        String path = context.getFilesDir().getAbsolutePath() + "/dump";
        File parent = new File(path);
        if (!parent.exists() || !parent.isDirectory()) {
            parent.mkdirs();
        }
        if (DeviceUtils.isOreo() || DeviceUtils.isPie() || DeviceUtils.isAndroid10()) {
            OreoDump.init(packageName);
        } else if (DeviceUtils.isNougat() || DeviceUtils.isMarshmallow()) {
            LowSdkDump.init(context,type);
        } else {
            return false;
        }
        return true;
    }
}
