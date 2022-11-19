package top.niunaijun.blackbox.fake.service.context;

import android.location.Location;

import java.lang.reflect.Method;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.entity.location.BLocation;
import top.niunaijun.blackbox.fake.frameworks.BLocationManager;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

public class LocationConsumerStub extends ClassInvocationStub {
    public static final String TAG = "ConsumerStub";
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

    @ProxyMethod("accept")
    public static class Accept extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            try {
                Location l = (Location)args[0];
                BLocation bl = BLocationManager.get().getLocation(BActivityThread.getUserId(),
                        BActivityThread.getAppPackageName());
                l.setLatitude(bl.getLatitude());
                l.setLongitude(bl.getLongitude());
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return method.invoke(who, args);
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
