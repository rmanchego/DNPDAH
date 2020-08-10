package com.practica02.proyectonpa.Model.Entidades.Logica;

import com.practica02.proyectonpa.Model.Entidades.Firebase.Pasos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LPasos {

    private String key; //Nombre del Nodo en la BD
    private Pasos pasos; //Referencia a la clase Pasos

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

    //Metodo que transforma un objeto long a un String con formato Dia/Mes/Año
    public static String obtenerFechaDeRegistro(long fecha){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(fecha);
        return simpleDateFormat.format(date);
    }

    //Metodo que transforma un objeto long a un String con formato Hora:Minuto:Segundo
    public static String obtenerHoraDeRegistro(long fecha){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date(fecha);
        return simpleDateFormat.format(date);
    }

}
