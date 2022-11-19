package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.location.IGnssNmeaListener;
import android.location.IGnssStatusListener;
import android.location.IGpsStatusListener;
import android.location.ILocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.Method;

import black.android.location.BRILocationManagerStub;
import black.android.location.provider.BRProviderProperties;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.entity.location.BLocation;
import top.niunaijun.blackbox.fake.frameworks.BLocationManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.service.context.GnssStatusListenerStub;
import top.niunaijun.blackbox.fake.service.context.LocationConsumerStub;
import top.niunaijun.blackbox.fake.service.context.LocationListenerStub;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.Reflector;

/**
 * Created by Milk on 4/8/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ILocationManagerProxy extends BinderInvocationStub {
    public static final String TAG = "ILocationManagerProxy";

    public ILocationManagerProxy() {
        super(BRServiceManager.get().getService(Context.LOCATION_SERVICE));
    }

    // get provider from LocationRequest obj.
    private static String getProvider(Object[] args) {
        try {
            Object r = MethodParameterUtils.getFirstParamByInstance(args, LocationRequest.class);
            if(r != null)
                return (String)Reflector.with(r).field("mProvider").get();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return LocationManager.FUSED_PROVIDER;
    }

    private static Object chgLocation(Object obj) {
        if(obj != null) {
            BLocation bl = BLocationManager.get().getLocation(BActivityThread.getUserId(),
                    BActivityThread.getAppPackageName());
            Location l = (Location)obj;
            l.setLatitude(bl.getLatitude());
            l.setLongitude(bl.getLongitude());
        }
        return obj;
    }

    @Override
    protected Object getWho() {
        return BRILocationManagerStub.get().asInterface(BRServiceManager.get().getService(Context.LOCATION_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "call: " + method.getName());
        MethodParameterUtils.replaceFirstAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    // for xiaomi android 7.1.1
    // at top.niunaijun.blackbox.fake.service.ILocationManagerProxy$AsBinder.hook(ILocationManagerProxy.java:99)
    //        at top.niunaijun.blackbox.fake.hook.ClassInvocationStub.invoke(ClassInvocationStub.java:130)
    //        at top.niunaijun.blackbox.fake.service.ILocationManagerProxy.invoke(ILocationManagerProxy.java:90)
    //        at java.lang.reflect.Proxy.invoke(Proxy.java:813)
    //        at $Proxy82.asBinder(Unknown Source)
    //        at android.location.MiuiLocationManagerProxy.getProxy(MiuiLocationManagerProxy.java:27)
    //        at android.location.LocationManager.<init>(LocationManager.java:328)
    //        at android.app.SystemServiceRegistry$24.createService(SystemServiceRegistry.java:341)
    //        at android.app.SystemServiceRegistry$24.createService(SystemServiceRegistry.java:339)
    //        at android.app.SystemServiceRegistry$CachedServiceFetcher.getService(SystemServiceRegistry.java:848)
    //        at android.app.SystemServiceRegistry.getSystemService(SystemServiceRegistry.java:799)
    //        at android.app.ContextImpl.getSystemService(ContextImpl.java:1522)
    //        at androidx.appcompat.view.ContextThemeWrapper.getSystemService(ContextThemeWrapper.java:167)
    //        at android.view.ContextThemeWrapper.getSystemService(ContextThemeWrapper.java:171)
    //        at android.app.Activity.getSystemService(Activity.java:5782)
    @ProxyMethod("asBinder")
    public static class AsBinder extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BRServiceManager.get().getService(Context.LOCATION_SERVICE);
        }
    }

    // >= android sdk 24
    @ProxyMethod("registerGnssStatusCallback")
    public static class RegisterGnssStatusCallback extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable() && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                // <= android sdk 29, args[0] is GnssStatusListenerTransport type
                Object transport = MethodParameterUtils.getFirstParamByInstance(args, IGnssStatusListener.Stub.class);
                if (transport != null) {
                    final String mlist[] = {"mGnssNmeaListener", // sdk 24~29
                            "mGpsNmeaListener", // sdk 24~28
                            "mOldGnssNmeaListener"}; // sdk 24~25
                    Reflector ref = Reflector.with(transport);
                    Reflector handler;
                    for(String m: mlist) {
                        try {
                            handler = ref.field(m);
                            if(handler.get() != null)
                                handler.set(new GnssStatusListenerStub().wrapper(handler.get(transport)));
                        } catch (Exception e) {
                            Log.d(TAG, "registerGnssStatusCallback hook failed. field: " + m);
                        }
                    }
                }
            }
            return method.invoke(who, args);
        }
    }

    // >= android sdk 31
    @ProxyMethod("registerGnssNmeaCallback")
    public static class RegisterGnssNmeaCallback extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable()) {
                Object transport = MethodParameterUtils.getFirstParamByInstance(args, IGnssNmeaListener.Stub.class);
                if (transport != null) {
                    try {
                        Reflector handler = Reflector.with(transport).field("mListener");
                        handler.set(new GnssStatusListenerStub().wrapper(handler.get()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return method.invoke(who, args);
        }
    }

    // >= android sdk 31
    @ProxyMethod("registerLocationListener")
    public static class RegisterLocationListener extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable()) {
                Object listener = MethodParameterUtils.getFirstParamByInstance(args, ILocationListener.Stub.class);
                if (listener != null) {
                    try {
                        Reflector handler = Reflector.with(listener).field("mListener");
                        handler.set(new LocationListenerStub().wrapper(handler.get()));
                        // Due to IWifiManagerProxy.java getScanResults returns an empty result set.
                        // Baidu Map SDK positioning may be problematic.
                        BLocationManager.get().postLocationChanged(((IInterface)listener).asBinder(),
                                (String)args[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getLastLocation")
    public static class GetLastLocation extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "GetLastLocation");
            Object ret = method.invoke(who, args);
            if (BLocationManager.isFakeLocationEnable())
                ret = chgLocation(ret);
            return ret;
        }
    }

    @ProxyMethod("getLastKnownLocation")
    public static class GetLastKnownLocation extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {

            Log.d(TAG, "GetLastKnownLocation");
            Object ret = method.invoke(who, args);
            if (BLocationManager.isFakeLocationEnable())
                ret = chgLocation(ret);
            return ret;
        }
    }

    // >= android sdk 30
    @ProxyMethod("getCurrentLocation")
    public static class  GetCurrentLocation extends MethodHook {
        // LocationManagerService.java
        // sdk 30
        // boolean getCurrentLocation(LocationRequest locationRequest,
        //            ICancellationSignal remoteCancellationSignal, ILocationListener listener,
        //            String packageName, String featureId, String listenerId);
        // >= sdk 31
        // ICancellationSignal getCurrentLocation(String provider, LocationRequest request,
        //            ILocationCallback consumer, String packageName, @Nullable String attributionTag,
        //            String listenerId)
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "GetCurrentLocation");
            if (BLocationManager.isFakeLocationEnable()) {
                try {
                    int i = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? 2 : 1;
                    Reflector ref = Reflector.with(args[i]).field("mConsumer");
                    ref.set(new LocationConsumerStub().wrapper(ref.get()));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            return method.invoke(who, args);
        }
    }

    // <= android sdk 30
    @ProxyMethod("requestLocationUpdates")
    public static class RequestLocationUpdates extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//            Log.d(TAG, "RequestLocationUpdates");
            if (BLocationManager.isFakeLocationEnable()) {
                Log.d(TAG, "isFakeLocationEnable RequestLocationUpdates");
                /*if (args[1] instanceof IInterface) {
                    IInterface listener = (IInterface) args[1];
                    BLocationManager.get().requestLocationUpdates(listener.asBinder());
                    return 0;
                }*/
                Object listener = MethodParameterUtils.getFirstParamByInstance(args, ILocationListener.Stub.class);
                if (listener != null) {
                    String provider = getProvider(args);
                    try {
                        Reflector handler = Reflector.with(listener).field("mListener");
                        handler.set(new LocationListenerStub().wrapper(handler.get()));
                        BLocationManager.get().postLocationChanged(((IInterface)listener).asBinder(),
                                provider);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return method.invoke(who, args);
        }
    }
/*
    @ProxyMethod("removeUpdates")
    public static class RemoveUpdates extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args[0] instanceof IInterface) {
                IInterface listener = (IInterface) args[0];
                BLocationManager.get().removeUpdates(listener.asBinder());
                return 0;
            }
            return method.invoke(who, args);
        }
    }
*/

    @ProxyMethod("getProviderProperties")
    public static class GetProviderProperties extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Object providerProperties = method.invoke(who, args);
            if (BLocationManager.isFakeLocationEnable()) {
                BRProviderProperties.get(providerProperties)._set_mHasNetworkRequirement(false);
                if (BLocationManager.get().getCell(BActivityThread.getUserId(), BActivityThread.getAppPackageName()) == null) {
                    BRProviderProperties.get(providerProperties)._set_mHasCellRequirement(false);
                }
            }
            return providerProperties;
        }
    }

    // <= android sdk 23
    @ProxyMethod("addGpsStatusListener")
    public static class AddGpsStatusListener extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable()) {
                Object transport = MethodParameterUtils.getFirstParamByInstance(args, IGpsStatusListener.Stub.class);
                if (transport != null) {
                    try {
                        Reflector handler = Reflector.with(transport).field("mNmeaListener");
                        if(handler.get() != null)
                            handler.set(new GnssStatusListenerStub().wrapper(handler.get()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return method.invoke(who, args);
        }
    }
/*
    @ProxyMethod("getBestProvider")
    public static class GetBestProvider extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable()) {
                return LocationManager.GPS_PROVIDER;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getAllProviders")
    public static class GetAllProviders extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return Arrays.asList(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER);
        }
    }

    @ProxyMethod("isProviderEnabledForUser")
    public static class isProviderEnabledForUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String provider = (String) args[0];
            return Objects.equals(provider, LocationManager.GPS_PROVIDER);
        }
    }
*/

    @ProxyMethod("setExtraLocationControllerPackageEnabled")
    public static class setExtraLocationControllerPackageEnabled extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }
}
