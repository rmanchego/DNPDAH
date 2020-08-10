package com.practica02.proyectonpa.Model.Sensores.Giroscopio;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

// Se utiliza para saber la orientación del dispositivo

public class Giroscopio {
    private ListenerGiroscopio listenerGiroscopio;
    float a = 0.2f;
    float ejez;
    float mLastZ,mHighPassZ;
    public void setListenerGiroscopio(ListenerGiroscopio listenerGiroscopio) {
        this.listenerGiroscopio = listenerGiroscopio;
    }
    private SensorManager sensorManager;
    private Sensor sensorgiroscopio;
    private SensorEventListener sensorEventListener;

    public Giroscopio(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorgiroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(listenerGiroscopio!=null){
                    ejez =event.values[2];
                    mHighPassZ = highPassA1(ejez,mLastZ,mHighPassZ);
                    mLastZ = ejez;
                    listenerGiroscopio.rotando(event.values[0],event.values[1],ejez);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }
    float highPassA1(float current, float last, float filtered){
        return a * (filtered+current-last);
    }
    public void register(){
        sensorManager.registerListener(sensorEventListener,sensorgiroscopio,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(){
        sensorManager.unregisterListener(sensorEventListener);
    }

}