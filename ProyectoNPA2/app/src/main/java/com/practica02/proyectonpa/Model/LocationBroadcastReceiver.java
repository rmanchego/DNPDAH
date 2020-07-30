package com.practica02.proyectonpa.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.practica02.proyectonpa.Controller.ObtenerDatosUbicacion;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "LocationBroadcastReceiver";
    private ObtenerDatosUbicacion mainActivityInf;
    public static int UNIQUE_ID = 0;
    public static String LOCATION_CHANGE = "location_changed";
    public static String ACTION = "action";


    public LocationBroadcastReceiver(ObtenerDatosUbicacion mainActivityInf) {
        this.mainActivityInf = mainActivityInf;

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) {

            String locationChanged = LocationManager.KEY_LOCATION_CHANGED;
            Location location = (Location) intent.getExtras().get(locationChanged);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d(TAG, latitude + "," + longitude);
            mainActivityInf.DisplayLocationChange(String.valueOf(latitude),String.valueOf(longitude));
            //    mainActivityInf.DisplayLocationChange(latitude + "," + longitude);
        }
    }
}
