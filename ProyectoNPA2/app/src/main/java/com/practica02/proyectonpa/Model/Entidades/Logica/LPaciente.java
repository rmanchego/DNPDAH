package com.practica02.proyectonpa.Model.Entidades.Logica;

import com.practica02.proyectonpa.Model.Entidades.Firebase.Paciente;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LPaciente {

    private String key; //Nombre del Nodo en la BD
    private Paciente paciente; //Referencia a la clase Paciente

    public LPaciente(String key, Paciente paciente){
        this.key = key;
        this.paciente = paciente;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    //Metodo que transforma un objeto long a un String con formato Dia/Mes/Año
    public static String obtenerFechaDeNacimiento(long fecha){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(fecha);
        return simpleDateFormat.format(date);
    }

    //Metodo que devuelve un String para generar el mensaje que se usara en el codigo QR
    public static String getReporte(Paciente paciente){
        return "Nombre: " + paciente.getNombre() + "\nFecha de Nacimiento: " +
                obtenerFechaDeNacimiento(paciente.getFechaDeNacimiento()) +
                "\nGenero" + paciente.getGenero() +"\nAltura: " + paciente.getAltura() + "m"
                + "\nPeso: " + paciente.getPeso() +"kg"+ "\nPresion: " + paciente.getPresion();
    }
}
