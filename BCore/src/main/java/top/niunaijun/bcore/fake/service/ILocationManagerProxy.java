package top.niunaijun.bcore.fake.service;

import android.content.Context;
import android.location.ILocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import black.android.location.BRILocationManagerStub;
import black.android.location.provider.BRProviderProperties;
import black.android.os.BRServiceManager;
import top.niunaijun.bcore.app.BActivityThread;
import top.niunaijun.bcore.entity.location.BLocation;
import top.niunaijun.bcore.fake.frameworks.BLocationManager;
import top.niunaijun.bcore.fake.hook.BinderInvocationStub;
import top.niunaijun.bcore.fake.hook.MethodHook;
import top.niunaijun.bcore.fake.hook.ProxyMethod;
import top.niunaijun.bcore.fake.service.context.LocationListenerStub;
import top.niunaijun.bcore.utils.MethodParameterUtils;
import top.niunaijun.bcore.utils.Reflector;

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

    @ProxyMethod("registerGnssStatusCallback")
    public static class RegisterGnssStatusCallback extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (BLocationManager.isFakeLocationEnable()) {
                Class<?> gnssStatusListenerTransportClass = Class.forName("android.location.LocationManager$GnssStatusListenerTransport");
                Object transport = MethodParameterUtils.getFirstParam(args, gnssStatusListenerTransportClass);
                if (transport != null) {
                    gnssStatusListenerTransportClass.getMethod("onGnssStarted").invoke(transport);
                    BLocation location = BLocationManager.get().getLocation(BActivityThread.getUserId(), BActivityThread.getAppPackageName());
                    if (location != null) {
                        try {
                            String date = new SimpleDateFormat("HHmmss:SS", Locale.US).format(new Date());
                            String latitude = BLocation.getGPSLatitude(location.getLatitude());
                            String longitude = BLocation.getGPSLatitude(location.getLongitude());
                            String latitudeNorthWest = BLocation.getNorthWest(location);
                            String longitudeSouthEast = BLocation.getSouthEast(location);
                            String $GPGGA = BLocation.checkSum(String.format("$GPGGA,%s,%s,%s,%s,%s,1,%s,692,.00,M,.00,M,,,", date, latitude, latitudeNorthWest, longitude, longitudeSouthEast, location.convert2SystemLocation().getExtras().getInt("satellites")));
                            String $GPRMC = BLocation.checkSum(String.format("$GPRMC,%s,A,%s,%s,%s,%s,0,0,260717,,,A,", date, latitude, latitudeNorthWest, longitude, longitudeSouthEast));

                            gnssStatusListenerTransportClass.getMethod("onNmeaReceived", long.class, String.class).invoke(transport, System.currentTimeMillis(), "$GPGSV,1,1,04,12,05,159,36,15,41,087,15,19,38,262,30,31,56,146,19,*73");
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                                Class<?> gpsStatusListenerTransportClass = Class.forName("android.location.LocationManager$GpsStatusListenerTransport");
                                gpsStatusListenerTransportClass.getMethod("onNmeaReceived", long.class, String.class).invoke(transport, System.currentTimeMillis(), "$GPGSV,1,1,04,12,05,159,36,15,41,087,15,19,38,262,30,31,56,146,19,*73");
                                gpsStatusListenerTransportClass.getMethod("onNmeaReceived", long.class, String.class).invoke(transport, System.currentTimeMillis(), $GPGGA);
                                gpsStatusListenerTransportClass.getMethod("onNmeaReceived", long.class, String.class).invoke(transport, System.currentTimeMillis(), "$GPVTG,0,T,0,M,0,N,0,K,A,*25");
                                gpsStatusListenerTransportClass.getMethod("onNmeaReceived", long.class, String.class).invoke(transport, System.currentTimeMillis(), $GPRMC);
                                gpsStatusListenerTransportClass.getMethod("onNmeaReceived", long.class, String.class).invoke(transport, System.currentTimeMillis(), "$GPGSA,A,2,12,15,19,31,,,,,,,,,604,712,986,*27");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }
            return method.invoke(who, args);
        }
    }

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
            if (BLocationManager.isFakeLocationEnable()) {
                return BLocationManager.get().getLocation(BActivityThread.getUserId(), BActivityThread.getAppPackageName()).convert2SystemLocation();
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getLastKnownLocation")
    public static class GetLastKnownLocation extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "GetLastKnownLocation");
            if (BLocationManager.isFakeLocationEnable()) {
                return BLocationManager.get().getLocation(BActivityThread.getUserId(), BActivityThread.getAppPackageName()).convert2SystemLocation();
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getCurrentLocation")
    public static class GetCurrentLocation extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "GetCurrentLocation");
            if (BLocationManager.isFakeLocationEnable()) {
                return BLocationManager.get().getLocation(BActivityThread.getUserId(), BActivityThread.getAppPackageName()).convert2SystemLocation();
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("requestLocationUpdates")
    public static class RequestLocationUpdates extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            //Log.d(TAG, "RequestLocationUpdates");
            if (BLocationManager.isFakeLocationEnable()) {
                Log.d(TAG, "isFakeLocationEnable RequestLocationUpdates");
                if (args[1] instanceof IInterface) {
                    IInterface listener = (IInterface) args[1];
                    BLocationManager.get().requestLocationUpdates(listener.asBinder());
                    return 0;
                }
            }
            return method.invoke(who, args);
        }
    }

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

    @ProxyMethod("removeGpsStatusListener")
    public static class RemoveGpsStatusListener extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            if (BLocationManager.isFakeLocationEnable()) {
                return 0;
            }
            return method.invoke(who, args);
        }
    }

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

    @ProxyMethod("setExtraLocationControllerPackageEnabled")
    public static class setExtraLocationControllerPackageEnabled extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }
}
