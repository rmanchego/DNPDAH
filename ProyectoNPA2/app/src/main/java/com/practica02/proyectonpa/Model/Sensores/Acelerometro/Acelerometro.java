package com.practica02.proyectonpa.Model.Sensores.Acelerometro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Acelerometro {
    private ListenerAcelerometro listenerAcelerometro;

    public void setListenerAcelerometro(ListenerAcelerometro listenerAcelerometro) {
        this.listenerAcelerometro = listenerAcelerometro;
    }
    private SensorManager sensorManager;
    private Sensor sensorAcelerometro;
    private SensorEventListener sensorEventListener;

    public Acelerometro(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(listenerAcelerometro!=null){
                    listenerAcelerometro.ejes(event.values[0],event.values[1],event.values[2]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    public void register(){
        sensorManager.registerListener(sensorEventListener,sensorAcelerometro,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(){
        sensorManager.unregisterListener(sensorEventListener);
    }

}
