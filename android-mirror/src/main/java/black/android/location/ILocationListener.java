package black.android.location;

import android.location.Location;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IRemoteCallback;
import java.util.List;
import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

@BClassName("android.location.ILocationListener")
public interface ILocationListener {
    // <= android 12
    @BMethod
    void onLocationChanged(Location Location0);

    // <= android 12
    @BMethod
    void onProviderEnabled(String provider);

    // <= android 12
    @BMethod
    void onProviderDisabled(String provider);

    // >= android 12
    @BMethod
    void onLocationChanged(List<Location> locations, IRemoteCallback onCompleteCallback);

    // >= android 12
    @BMethod
    void onProviderEnabledChanged(String provider, boolean enabled);

    // >= android 12
    @BMethod
    void onFlushComplete(int requestCode);

    @BClassName("android.location.ILocationListener$Stub")
    interface Stub {
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
