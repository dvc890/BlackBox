package android.location;

import android.location.Location;

oneway interface IGnssStatusListener
{
    void onGnssStarted();
    void onGnssStopped();
    void onFirstFix(int ttff);
    void onSvStatusChanged(int svCount, in int[] svidWithFlags, in float[] cn0s,
            in float[] elevations, in float[] azimuths);
    void onNmeaReceived(long timestamp, String nmea);
}
