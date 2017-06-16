package com.example.camilo.prueba0.modelo;

import java.util.List;

/**
 * Created by Camilo on 04-jun-17.
 */

public class Espectaculo
{
    private String id;
    private String nombre;
    private String descripcion;
    private List<TipoEspectaculo> tipoEspectaculo;
    private List<Realizacion> realizacionEspectaculo;

    public Espectaculo(String id, String nombre, String descripcion, List<TipoEspectaculo> tipoEspectaculo, List<Realizacion> realizacionEspectaculo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoEspectaculo = tipoEspectaculo;
        this.realizacionEspectaculo = realizacionEspectaculo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<Realizacion> getRealizacionEspectaculo() {
        return realizacionEspectaculo;
    }

    public void setRealizacionEspectaculo(List<Realizacion> realizacionEspectaculo) {
        this.realizacionEspectaculo = realizacionEspectaculo;
    }


    public List<TipoEspectaculo> getTipoEspectaculo() {
        return tipoEspectaculo;
    }

    public void setTipoEspectaculo(List<TipoEspectaculo> tipoEspectaculo) {
        this.tipoEspectaculo = tipoEspectaculo;
    }
}
