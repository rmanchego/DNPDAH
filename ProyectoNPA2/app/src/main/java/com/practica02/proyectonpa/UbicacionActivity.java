package com.practica02.proyectonpa;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.practica02.proyectonpa.Controller.ObtenerDatosUbicacion;
import com.practica02.proyectonpa.Fragments.Frag1;
import com.practica02.proyectonpa.Model.LocationBroadcastReceiver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UbicacionActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private LocationBroadcastReceiver broadcastReceiver;
    private TextView txtlatitud;
    private TextView txtlongitud;
    private TextView txtDireccion;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Button btnirFoto;
    private String direccionCalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        broadcastReceiver = new LocationBroadcastReceiver(mainActivityInf2);
        checkLocationPermission();

        txtlatitud = (TextView) findViewById(R.id.id_latitud);
        txtlongitud = (TextView) findViewById(R.id.id_longitud);
        txtDireccion = (TextView) findViewById(R.id.id_direccion);

        btnirFoto = (Button) findViewById(R.id.btnirFoto);
        /*btnirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UbicacionActivity.this, CamaraActivity.class);
                startActivity(intent);
                //  Toast.makeText(getApplicationContext(),"Su dirección es: " + txtDireccion.getText(),Toast.LENGTH_LONG).show();
            }
        });*/
    }

    private ObtenerDatosUbicacion mainActivityInf2 = new ObtenerDatosUbicacion() {
        @Override
        public void DisplayLocationChange(String latitud, String longitud) {
            //  Log.d(TAG,"Location: "+location);
            //Log.d(TAG,"Latitud: " + latitud);
            txtlatitud.setText(latitud);
            txtlongitud.setText(longitud);
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(Double.parseDouble(latitud), Double.parseDouble(longitud), 1);
                direccionCalle = addressList.get(0).getAddressLine(0);
                txtDireccion.setText(direccionCalle);
                System.out.println(direccionCalle);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

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
        btnirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UbicacionActivity.this, CamaraActivity.class);
                intent.putExtra("latitud", txtlatitud.getText().toString());
                intent.putExtra("longitud", txtlongitud.getText().toString());
                intent.putExtra("direccion", txtDireccion.getText().toString());
                startActivity(intent);
            }
        });

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
}
