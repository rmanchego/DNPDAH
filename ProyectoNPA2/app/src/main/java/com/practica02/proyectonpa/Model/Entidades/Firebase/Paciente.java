package com.practica02.proyectonpa.Model.Entidades.Firebase;

public class Paciente {

    private String fotoPerfilURL;
    private String fotoCodigoQR;
    private String nombre;
    private long fechaDeNacimiento;
    private String genero;
    private float altura;
    private float peso;
    private double presion;

    public Paciente(){

    }

    //Logica de BD


    public String getFotoPerfilURL() {
        return fotoPerfilURL;
    }

    public void setFotoPerfilURL(String fotoPerfilURL) {
        this.fotoPerfilURL = fotoPerfilURL;
    }

    public String getFotoCodigoQR() {
        return fotoCodigoQR;
    }

    public void setFotoCodigoQR(String fotoCodigoQR) {
        this.fotoCodigoQR = fotoCodigoQR;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public void setFechaDeNacimiento(long fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public double getPresion() {
        return presion;
    }

    public void setPresion(double presion) {
        this.presion = presion;
    }
}
