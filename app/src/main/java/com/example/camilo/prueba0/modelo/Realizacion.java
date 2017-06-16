package com.example.camilo.prueba0.modelo;

import java.util.Date;
import java.util.List;

/**
 * Created by Camilo on 04-jun-17.
 */

public class Realizacion
{
    private String id;
    private Sala sala;
    private String fecha;
    private String idEspectaculo;
    private List<Sector> sectores;

    public Realizacion(String id, Sala sala, String fecha, String idEspectaculo, List<Sector> sectores) {
        this.id = id;
        this.sala = sala;
        this.fecha = fecha;
        this.idEspectaculo = idEspectaculo;
        this.sectores = sectores;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public String getIdEspectaculo() {
        return idEspectaculo;
    }

    public void setIdEspectaculo(String idEspectaculo) {
        this.idEspectaculo = idEspectaculo;
    }

    public List<Sector> getSectores() {
        return sectores;
    }

    public void setSectores(List<Sector> sectores) {
        this.sectores = sectores;
    }


    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
