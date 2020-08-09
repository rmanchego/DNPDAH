package com.practica02.proyectonpa.Controller.Sensores.SensorLuz;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;

public class LightSensor {

    private Context contextss;

    private ListenerLightSensor listenerSensorLuz;


    public void setListenerSensorLuz(ListenerLightSensor listenerSensorLuz) {
        this.listenerSensorLuz = listenerSensorLuz;
    }

    private SensorManager sensorManager;
    private Sensor sensordeLuz;
    private SensorEventListener sensorEventListener;
    private float valorMaximodeLuz;
    private float valorLuz;

    public LightSensor(Context context){
        contextss = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensordeLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        valorMaximodeLuz = sensordeLuz.getMaximumRange();
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(listenerSensorLuz!=null){
                    listenerSensorLuz.valorSensorLuz(event.values[0]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }
    private boolean flashLightStatus = false;




//método que se encargar de encender el flash, utiliza CameraManager para obtener el servicio de la Cámara
    //luego obtiene el ID de la cámara que se está utilizando para luego encender el flash llamando al
    //método setTorchMode y estableciéndolo como true.
    public void flashLightOn() {
        CameraManager cameraManager = (CameraManager) contextss.getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            flashLightStatus = true;
        } catch (CameraAccessException e) {
        }
    }
//Misma lógica del método flashLightOn, la diferencia es que setTorchMode lo establece en false con el objetivo de apagar
    //la cámara.
    public void flashLightOff() {
        CameraManager cameraManager = (CameraManager) contextss.getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            flashLightStatus = false;
        } catch (CameraAccessException e) {
        }
    }



    public void register(){
        sensorManager.registerListener(sensorEventListener,sensordeLuz,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(){
        sensorManager.unregisterListener(sensorEventListener);
    }
}
