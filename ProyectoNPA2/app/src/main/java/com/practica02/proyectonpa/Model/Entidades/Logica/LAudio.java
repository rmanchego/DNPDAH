package com.practica02.proyectonpa.Model.Entidades.Logica;

import com.practica02.proyectonpa.Model.Entidades.Firebase.Audio;

public class LAudio {

    private String key;
    private Audio audio;

    public LAudio(String key, Audio audio){
        this.key = key;
        this.audio = audio;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

}
