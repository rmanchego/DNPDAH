package com.practica02.proyectonpa.Model.Sensores.GPS;

import java.io.Serializable;

public interface ObtenerDatosUbicacion extends Serializable {
    void DisplayLocationChange(String latitud,String longitud);
}