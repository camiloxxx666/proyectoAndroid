package com.example.camilo.prueba0.modelo;

import java.util.List;

/**
 * Created by User on 21/06/2017.
 */

public class RealizacionUsuario
{
    private String id;
    private Sala sala;
    private String fecha;
    private String idEspectaculo;
    private Sector sector;

    public RealizacionUsuario(String id, Sala sala, String fecha, String idEspectaculo, Sector sector) {
        this.id = id;
        this.sala = sala;
        this.fecha = fecha;
        this.idEspectaculo = idEspectaculo;
        this.sector = sector;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIdEspectaculo() {
        return idEspectaculo;
    }

    public void setIdEspectaculo(String idEspectaculo) {
        this.idEspectaculo = idEspectaculo;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }
}
