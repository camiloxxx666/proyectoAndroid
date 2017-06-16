package com.example.camilo.prueba0.modelo;

import java.util.Date;

/**
 * Created by User on 15/06/2017.
 */

public class Fecha_realizacion
{
    private String fecha;
    private String idRealizacion;

    public Fecha_realizacion(String fecha, String idRealizacion) {
        this.fecha = fecha;
        this.idRealizacion = idRealizacion;
    }

    public String getIdRealizacion() {
        return idRealizacion;
    }

    public void setIdRealizacion(String idRealizacion) {
        this.idRealizacion = idRealizacion;
    }


    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}