package com.practica02.proyectonpa.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.practica02.proyectonpa.Controller.UbicacionActivity;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    public double latitud,longitud;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)){ //Constante de cambio de ubicación
            String locationKey = LocationManager.KEY_LOCATION_CHANGED;
            Location ubicacion = (Location) intent.getExtras().get(locationKey);
            latitud = ubicacion.getLatitude();
            longitud = ubicacion.getLongitude();
        }

        else {
            Toast.makeText(context,"Null",Toast.LENGTH_SHORT).show();
        }
        enviarDatos(context);
    }
    public void enviarDatos(Context context){
        Intent intentActivity = new Intent(context, UbicacionActivity.class);
        intentActivity.putExtra("LATITUDE", String.valueOf(latitud));
        intentActivity.putExtra("LONGITUDE", String.valueOf(longitud));


        intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentActivity);

    }
}