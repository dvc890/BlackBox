package android.location;

import android.location.Location;

oneway interface IGpsStatusListener
{
    void onGpsStarted();
    void onGpsStopped();
    void onFirstFix(int ttff);
    void onSvStatusChanged(int svCount, in int[] prns, in float[] snrs, 
            in float[] elevations, in float[] azimuths, 
            int ephemerisMask, int almanacMask, int usedInFixMask);
    void onNmeaReceived(long timestamp, String nmea);
}
