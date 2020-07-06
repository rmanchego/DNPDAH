package com.practica02.proyectonpa.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button btnComenzar, btnDetener, btnReiniciar,btnContinuar;
    boolean caminando = false;

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag3_layout,container,false);

        viewPasos = v.findViewById(R.id.id_viewpasosFrag);
        btnComenzar = v.findViewById(R.id.btnComenzarFrag);
        btnDetener = v.findViewById(R.id.btnDetenerFrag);
        btnReiniciar = v.findViewById(R.id.btnReiniciarFrag);
        btnContinuar = v.findViewById(R.id.btnContinuarFrag);

        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        final Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caminando = true;
                contarPasos = 0;
                btnComenzar.setEnabled(false);
                btnContinuar.setEnabled(false);
                btnDetener.setEnabled(true);
                btnReiniciar.setEnabled(true);
            }
        });
        btnDetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caminando = false;
                btnContinuar.setEnabled(true);
                btnReiniciar.setEnabled(true);
            }
        });

        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caminando = false;
                contarPasos = 0;
                viewPasos.setText(0);
                btnComenzar.setEnabled(true);
                btnContinuar.setEnabled(false);
                btnDetener.setEnabled(false);
                btnReiniciar.setEnabled(false);
            }
        });
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caminando = true;
                btnComenzar.setEnabled(false);
                btnContinuar.setEnabled(false);
                btnDetener.setEnabled(true);
                btnReiniciar.setEnabled(true);
            }
        });

        SensorEventListener contadorPasos = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (caminando){
                    if (event != null) {
                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        double valor = Math.sqrt(x * x + y * y + z * z);
                        double val2 = valor - valoranterior;
                        valoranterior = valor;

                        if (val2 > 6) {
                            contarPasos++;
                        }
                        viewPasos.setText(String.valueOf(contarPasos));

                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(contadorPasos,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", contarPasos);
        editor.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", contarPasos);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        contarPasos = sharedPreferences.getInt("stepCount",0);
    }
}
