package com.example.camilo.prueba0.modelo;

import java.util.List;

/**
 * Created by User on 15/06/2017.
 */

public class Sala_fecha
{
    private Sala sala;
    private List<Fecha_realizacion> fechas;
    private List<Sector> sectores;

    public Sala_fecha(){

    }

    public Sala_fecha(Sala sala, List<Fecha_realizacion> fechas, List<Sector> sectores) {
        this.sala = sala;
        this.fechas = fechas;
        this.sectores = sectores;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public List<Fecha_realizacion> getFechas() {
        return fechas;
    }

    public void setFechas(List<Fecha_realizacion> fechas) {
        this.fechas = fechas;
    }

    public List<Sector> getSectores() {
        return sectores;
    }

    public void setSectores(List<Sector> sectores) {
        this.sectores = sectores;
    }
}



