package android.location;

oneway interface IGnssNmeaListener
{
    void onNmeaReceived(long timestamp, String nmea);
}
