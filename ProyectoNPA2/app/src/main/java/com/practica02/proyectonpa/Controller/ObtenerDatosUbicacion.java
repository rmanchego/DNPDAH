package com.practica02.proyectonpa.Controller;

import java.io.Serializable;

public interface ObtenerDatosUbicacion extends Serializable {
    //  void DisplayLocationChange(String location);
    void DisplayLocationChange(String latitud,String longitud);
}