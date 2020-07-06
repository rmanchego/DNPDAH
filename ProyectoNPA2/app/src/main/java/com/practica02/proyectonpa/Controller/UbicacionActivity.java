package com.practica02.proyectonpa.Controller;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.practica02.proyectonpa.MainActivity;
import com.practica02.proyectonpa.Model.LocationBroadcastReceiver;
import com.practica02.proyectonpa.Model.UbicacionActivityInf;
import com.practica02.proyectonpa.R;

public class UbicacionActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "MainActivity";
    private LocationBroadcastReceiver broadcastReceiver;
    TextView txtLatitud;
    TextView txtLongitud;
    private double valorLatitud,valorLongitud;
    Button regresarMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        broadcastReceiver = new LocationBroadcastReceiver(mainActivityInf2);

        checkLocationPermission();

        txtLatitud = (TextView) findViewById(R.id.txtLatitud);
        txtLongitud = (TextView) findViewById(R.id.txtLongitud);
        regresarMenu = findViewById(R.id.btnRegresarInterfPrincipal);
        regresarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UbicacionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        LatLng latLng = new LatLng(valorLatitud,valorLongitud);
        float zoom=17;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);

      /*  googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                googleMap.addMarker(markerOptions);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(LocationManager.KEY_LOCATION_CHANGED);
            registerReceiver(broadcastReceiver, intentFilter);
        } else {
            Log.d(TAG, "broadcastReceiver is null");
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        initGPS();

                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "Location not allowed");
                }
                return;
            }

        }
    }
//a
    public void initGPS() {

        //Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        Intent intent = new Intent(LocationManager.KEY_LOCATION_CHANGED);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(//sendBroadcast(...)
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0,
                0,
                pendingIntent);

    }

    private UbicacionActivityInf mainActivityInf2=new UbicacionActivityInf() {
        @Override
        public void DisplayLocationChange(double latitud,double longitud) {

            valorLatitud = latitud;
            valorLongitud = longitud;
            Log.d(TAG, "Latitud: " + latitud + " Longitud: " + longitud);

            txtLatitud.setText(String.valueOf(latitud));
            txtLongitud.setText(String.valueOf(longitud));
        }
    };


}

