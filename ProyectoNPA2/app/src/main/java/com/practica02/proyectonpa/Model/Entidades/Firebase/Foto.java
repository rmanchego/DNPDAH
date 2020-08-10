package com.practica02.proyectonpa.Model.Entidades.Firebase;

public class Foto {
    private String fotoPerfilURL;
    private String nombre;
    private String direccion;
    private String latitud;
    private String longitud;

    private String valorLuz;

    public Foto(){

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }


    public String getValorLuz() {
        return valorLuz;
    }

    public void setValorLuz(String valorLuz) {
        this.valorLuz = valorLuz;
    }

}
