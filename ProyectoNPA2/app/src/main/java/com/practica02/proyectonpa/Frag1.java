package com.practica02.proyectonpa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.practica02.proyectonpa.Controller.UbicacionActivity;
import com.practica02.proyectonpa.Model.LocationBroadcastReceiver;

public class Frag1 extends Fragment {

    private TextView latitud,longitud,status,provider;
    Bundle datos;
    private LocationBroadcastReceiver broadcastReceiver;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99; //Variable para el método checkloactionpermission

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag1_layout,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        latitud = getView().findViewById(R.id.latitudViewFrag);
        longitud = getView().findViewById(R.id.longitudViewFrag);
        recibirDatos();
        checkLocationPermission();
        broadcastReceiver = new LocationBroadcastReceiver();
    }

    public void recibirDatos(){
        Intent intent = getActivity().getIntent();
        datos = intent.getExtras();
        if(datos!=null){
            String valorLatitud = datos.getString("LATITUDE");
            latitud.setText(valorLatitud);
            String valorLongitud = datos.getString("LONGITUDE");
            longitud.setText(valorLongitud);
            Log.d("Latitud: ", valorLatitud);
            Log.d("Longitud: ", valorLongitud);


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(broadcastReceiver != null){
            IntentFilter filtro = new IntentFilter();
            filtro.addAction(LocationManager.KEY_LOCATION_CHANGED);
            filtro.addAction(LocationManager.KEY_PROVIDER_ENABLED);
            filtro.addAction(LocationManager.KEY_STATUS_CHANGED);
            filtro.addAction(LocationManager.KEY_PROXIMITY_ENTERING);
            requireActivity().registerReceiver(broadcastReceiver,filtro);

//                IntentFilter filtro2 = new IntentFilter();
//                filtro.addAction(LocationManager.GP);
//                registerReceiver(broadcastReceiver,filtro2);
        }
        else{
            Log.d("MainActivity","broadcastReceiver es nulo");
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(broadcastReceiver);
    }



    public boolean checkLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
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
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)//Si la app tiene permisos para utilizar el servicio de localizacion, entonces voy a inicializar el GPS(initGPS())
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
        Intent intent = new Intent(getActivity(), LocationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast( //Objetos mensajes o acciones que se van a ejecutar mas adelante
                getActivity(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, pendingIntent); //Se utiliza el pending en esta linea porque cuando exista un cambio de ubicacion(LocationUpdates), este pending será ejecutado.  (Esta asociado con el Location porque asi sabe que se cambio de ubicacion: L.NETWORK o L.GPS u otros) Network es más rapido

    }
}
