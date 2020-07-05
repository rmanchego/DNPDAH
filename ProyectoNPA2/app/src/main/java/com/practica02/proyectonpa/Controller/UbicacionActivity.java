package com.practica02.proyectonpa.Controller;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.practica02.proyectonpa.Model.LocationBroadcastReceiver;
import com.practica02.proyectonpa.R;

public class UbicacionActivity extends AppCompatActivity {
    private TextView latitud,longitud,status,provider;
    Bundle datos;
    private LocationBroadcastReceiver broadcastReceiver;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99; //Variable para el método checkloactionpermission
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        latitud = findViewById(R.id.latitudView);
        longitud = findViewById(R.id.longitudView);
        recibirDatos();
        checkLocationPermission();
        broadcastReceiver = new LocationBroadcastReceiver();
    }

    public void recibirDatos(){
        Intent intent = getIntent();
        datos = intent.getExtras();
        if(datos!=null){
            String valorLatitud = datos.getString("LATITUDE");
            latitud.setText(valorLatitud);
            String valorLongitud = datos.getString("LONGITUDE");
            longitud.setText(valorLongitud);
            Log.d("Latitud: ", valorLatitud);
            Log.d("Longitud: ", valorLongitud);


        }}


    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver != null){
            IntentFilter filtro = new IntentFilter();
            filtro.addAction(LocationManager.KEY_LOCATION_CHANGED);
            filtro.addAction(LocationManager.KEY_PROVIDER_ENABLED);
            filtro.addAction(LocationManager.KEY_STATUS_CHANGED);
            filtro.addAction(LocationManager.KEY_PROXIMITY_ENTERING);
            registerReceiver(broadcastReceiver,filtro);
//                IntentFilter filtro2 = new IntentFilter();
//                filtro.addAction(LocationManager.GP);
//                registerReceiver(broadcastReceiver,filtro2);
        }
        else{
            Log.d("MainActivity","broadcastReceiver es nulo");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }



    public boolean checkLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                //Si la petición es cancelada, resula en un arreglo vacío
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Permiso dado, ya se puede hacer la operacion de localizacion que deseamos
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)//Si la app tiene permisos para utilizar el servicio de localizacion, entonces voy a inicializar el GPS(initGPS())
                            == PackageManager.PERMISSION_GRANTED) {
                        initGPS();
                    }
                } else { //Permiso denegado
                    Log.d("MainActivity", "Location not allowed");
                }
                return;
            }
        }


    }

    private void initGPS() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast( //Objetos mensajes o acciones que se van a ejecutar mas adelante
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, pendingIntent); //Se utiliza el pending en esta linea porque cuando exista un cambio de ubicacion(LocationUpdates), este pending será ejecutado.  (Esta asociado con el Location porque asi sabe que se cambio de ubicacion: L.NETWORK o L.GPS u otros) Network es más rapido

    }
}