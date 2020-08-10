package com.practica02.proyectonpa.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.practica02.proyectonpa.Controller.FotosUbicacion.HistorialFotosActivity;
import com.practica02.proyectonpa.Model.Utilidades.LocationBroadcastReceiver;
import com.practica02.proyectonpa.R;
import com.practica02.proyectonpa.Controller.FotosUbicacion.UbicacionActivity;

public class Fragment_Fotos extends Fragment {

    Bundle datos;
    private Button btnir;
    private Button btnHistorialFotos;
    private float lat, longi;
    private LocationBroadcastReceiver broadcastReceiver;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99; //Variable para el método checkloactionpermission
    private TextView posicionConocida;

    Button btniralMapa;
    TextView tvUbicacionLatitud, tvUbicacionLongitud;

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_fotos_layout, container, false);

      /*  tvUbicacionLatitud = v.findViewById(R.id.txtLatitud);
        tvUbicacionLongitud = v.findViewById(R.id.txtLongitud);*/

        btnir = v.findViewById(R.id.idUbicacionbutton);
        btnHistorialFotos = v.findViewById(R.id.idHistorialFotos);

        btnir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UbicacionActivity.class);
                startActivity(intent);
            }
        });

        btnHistorialFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistorialFotosActivity.class);
                startActivity(intent);
            }
        });



        return v;


    }





}
