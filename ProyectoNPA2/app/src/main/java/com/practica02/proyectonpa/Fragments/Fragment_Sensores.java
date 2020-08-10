package com.practica02.proyectonpa.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.practica02.proyectonpa.Controller.PruebaSensores.PruebasSensoresActivity;
import com.practica02.proyectonpa.R;

public class Fragment_Sensores extends Fragment {

    Button btnOrientacion;
    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag4_layout,container,false);

        btnOrientacion = v.findViewById(R.id.btnOrientacionFrag);

        btnOrientacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PruebasSensoresActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }




}
