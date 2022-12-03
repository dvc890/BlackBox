package top.niunaijun.blackbox.fake.service.context;

import java.lang.reflect.Method;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.entity.location.BLocation;
import top.niunaijun.blackbox.fake.frameworks.BLocationManager;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

public class GnssStatusListenerStub extends ClassInvocationStub {
    public static final String TAG = "GnssStatusListenerStub";
    private Object mBase;

    public Object wrapper(final Object locationListenerProxy) {
        mBase = locationListenerProxy;
        injectHook();
        return getProxyInvocation();
    }

    @Override
    protected Object getWho() {
        return mBase;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {

    }

    @Override
    protected void onBindMethod() {

    }

    private static String nmeaGen(String s) {
        int sum = 0;
        for (int i = 1; i < s.length(); i++) {
            sum ^= (byte)s.charAt(i);
        }
        return String.format("%s*%02X\r\n", s, sum);
    }

    private static Object chgNmeaMsg(Object arg) {
        try {
            String nmea = (String)arg;
            String id = nmea.substring(3, 7);
            if(id.equals("GGA,")) { // $GNGGA $GPGGA
                BLocation location = BLocationManager.get().getLocation(BActivityThread.getUserId(),
                        BActivityThread.getAppPackageName());
                String dat[] = nmea.split(",");
                if(dat[2].length() <= 0 && dat[4].length() <= 0)
                    return nmea;
                dat[2] = location.getGPSLatitude();
                dat[3] = location.getLatitudeDir();
                dat[4] = location.getGPSLongitude();
                dat[5] = location.getLongitudeDir();
                int last = dat.length - 1;
                int idx = dat[last].indexOf('*');
                dat[last] = dat[last].substring(0, idx);
                return nmeaGen(String.join(",", dat));
            } else if(id.equals("RMC,")) { // $GNRMC $GPRMC
                BLocation location = BLocationManager.get().getLocation(BActivityThread.getUserId(),
                        BActivityThread.getAppPackageName());
                String dat[] = nmea.split(",");
                if(dat[3].length() <= 0 && dat[5].length() <= 0)
                    return nmea;
                dat[3] = location.getGPSLatitude();
                dat[4] = location.getLatitudeDir();
                dat[5] = location.getGPSLongitude();
                dat[6] = location.getLongitudeDir();
                int last = dat.length - 1;
                int idx = dat[last].indexOf('*');
                dat[last] = dat[last].substring(0, idx);
                return nmeaGen(String.join(",", dat));
            } else if(id.equals("GLL,")) { // $GNGLL $GPGLL
                BLocation location = BLocationManager.get().getLocation(BActivityThread.getUserId(),
                        BActivityThread.getAppPackageName());
                String dat[] = nmea.split(",");
                if(dat[1].length() <= 0 && dat[3].length() <= 0)
                    return nmea;
                dat[1] = location.getGPSLatitude();
                dat[2] = location.getLatitudeDir();
                dat[3] = location.getGPSLongitude();
                dat[4] = location.getLongitudeDir();
                int last = dat.length - 1;
                int idx = dat[last].indexOf('*');
                dat[last] = dat[last].substring(0, idx);
                return nmeaGen(String.join(",", dat));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return arg;
    }

    @ProxyMethod("onNmeaMessage")
    public static class OnNmeaMessage extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // void onNmeaMessage(String message, long timestamp);
            args[0] = chgNmeaMsg(args[0]);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("onNmeaReceived")
    public static class OnNmeaReceived extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // void onNmeaReceived(long timestamp, String nmea);
            args[1] = chgNmeaMsg(args[1]);
            return method.invoke(who, args);
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
