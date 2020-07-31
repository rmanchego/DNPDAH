package com.practica02.proyectonpa.Model.Entidades.Logica;

import com.practica02.proyectonpa.Model.Entidades.Firebase.Pasos;

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
}
