package com.practica02.proyectonpa.Fragments;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.practica02.proyectonpa.R;

import static android.content.Context.SENSOR_SERVICE;

public class Frag3 extends Fragment {
    //Contador de Pasos V 1.0
    private double valoranterior=0;
    private int contarPasos=0;
    TextView viewPasos;

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag3_layout,container,false);

        viewPasos = v.findViewById(R.id.id_viewpasosFrag);
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        final Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener contadorPasos = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event!=null){
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    double valor = Math.sqrt(x*x + y*y + z*z);
                    double val2 = valor-valoranterior;
                    valoranterior = valor;

                    if(val2 >6){
                        contarPasos++;
                    }
                    viewPasos.setText(String.valueOf(contarPasos));

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(contadorPasos,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        return v;
    }
}
