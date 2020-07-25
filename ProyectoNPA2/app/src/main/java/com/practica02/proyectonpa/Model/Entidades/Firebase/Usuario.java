package com.practica02.proyectonpa.Model.Entidades.Firebase;

public class Usuario {
    private String fotoPerfilURL;
    private String nombre;
    private String correo;
    private long fechaDeNacimiento;
    private String genero;
    private float Altura;
    private float Peso;
    private float Presion;

    public Usuario() {
    }

    //Logica de BD


    public String getFotoPerfilURL() {
        return fotoPerfilURL;
    }

    public void setFotoPerfilURL(String fotoPerfilURL) {
        this.fotoPerfilURL = fotoPerfilURL;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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
        return Altura;
    }

    public void setAltura(float altura) {
        Altura = altura;
    }

    public float getPeso() {
        return Peso;
    }

    public void setPeso(float peso) {
        Peso = peso;
    }

    public float getPresion() {
        return Presion;
    }

    public void setPresion(float presion) {
        Presion = presion;
    }
}
