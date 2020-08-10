package com.practica02.proyectonpa.Model.Entidades.Firebase;

public class Pasos {
    private int pasos;
    private long fecha;
    private String duracion;


    public Pasos() {

    }

    //Logica de BD

    public int getPasos() {
        return pasos;
    }

    public void setPasos(int pasos) {
        this.pasos = pasos;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }
}

