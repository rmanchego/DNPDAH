package com.practica02.proyectonpa.Model.Entidades.Logica;

import com.practica02.proyectonpa.Model.Entidades.Firebase.Pasos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LPasos {

    private String key;
    private Pasos pasos;

    public LPasos(String key, Pasos pasos){
        this.key = key;
        this.pasos = pasos;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Pasos getPasos() {
        return pasos;
    }

    public void setPasos(Pasos pasos) {
        this.pasos = pasos;
    }

    public static String obtenerFechaDeRegistro(long fecha){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(fecha);
        return simpleDateFormat.format(date);
    }

    public static String obtenerHoraDeRegistro(long fecha){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date(fecha);
        return simpleDateFormat.format(date);
    }

}
