package com.practica02.proyectonpa.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    private UbicacionActivityInf ubicacionActivityInf;
    public LocationBroadcastReceiver(UbicacionActivityInf ubicacionActivityInf) {
        this.ubicacionActivityInf = ubicacionActivityInf;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) {
            String locationChanged = LocationManager.KEY_LOCATION_CHANGED;
            Location location = (Location) intent.getExtras().get(locationChanged);
            if (bundle!=null){

            }
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            ubicacionActivityInf.DisplayLocationChange(latitude,longitude);
        }
    }

}