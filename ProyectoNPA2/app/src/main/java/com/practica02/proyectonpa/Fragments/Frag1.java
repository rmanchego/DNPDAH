package com.practica02.proyectonpa.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.practica02.proyectonpa.Controller.QrScanner;
import com.practica02.proyectonpa.Controller.UbicacionActivity;
import com.practica02.proyectonpa.Model.LocationBroadcastReceiver;
import com.practica02.proyectonpa.R;

public class Frag1 extends Fragment {

    private TextView latitud, longitud, status, provider;
    Bundle datos;
    Button btnregresar;
    private float lat, longi;
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
        Intent intenttt = new Intent(getActivity(), UbicacionActivity.class);
        startActivity(intenttt);

    }




}
