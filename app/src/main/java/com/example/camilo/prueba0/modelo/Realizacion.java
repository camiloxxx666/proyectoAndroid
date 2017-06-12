package com.example.camilo.prueba0.modelo;

/**
 * Created by Camilo on 04-jun-17.
 */

public class Realizacion
{
    private String id;
    private Sala sala;
    private String fecha;
    private String idEspectaculo;

    public Realizacion(String id, Sala sala, String fecha, String idEspectaculo) {
        this.id = id;
        this.sala = sala;
        this.fecha = fecha;
        this.idEspectaculo = idEspectaculo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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
}
