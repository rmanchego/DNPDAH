package com.practica02.proyectonpa.Model.Entidades.Logica;


import com.practica02.proyectonpa.Model.Entidades.Firebase.Foto;

public class LFoto {

    private String key; //Nombre del Nodo en la BD
    private Foto foto; //Referencia a la clase Foto

    public LFoto(String key, Foto foto) {
        this.key = key;
        this.foto = foto;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Foto getFoto() {
        return foto;
    }

    public void setFoto(Foto foto) {
        this.foto = foto;
    }



}
