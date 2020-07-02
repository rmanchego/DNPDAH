package com.practica02.proyectonpa.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.practica02.proyectonpa.R;

public class ContadorPasos extends AppCompatActivity {
    //Contador de Pasos V 1.0
    private double valoranterior=0;
    private int contarPasos=0;
    TextView viewPasos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador_pasos);
        viewPasos = (TextView) findViewById(R.id.id_viewpasos);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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
    }
}
