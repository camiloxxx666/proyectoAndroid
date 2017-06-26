package com.example.camilo.prueba0.modelo;

import java.util.List;

/**
 * Created by User on 20/06/2017.
 */

public class TicketUsuario
{
    private String id;
    private String nombre;
    private String descripcion;
    private RealizacionUsuario realizacionEspectaculo;
    private String idAsiento;

    public TicketUsuario(String id, String nombre, String descripcion, RealizacionUsuario realizacionEspectaculo, String idAsiento) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.realizacionEspectaculo = realizacionEspectaculo;
        this.idAsiento = idAsiento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(String idAsiento) {
        this.idAsiento = idAsiento;
    }

    public RealizacionUsuario getRealizacionEspectaculo() {
        return realizacionEspectaculo;
    }

    public void setRealizacionEspectaculo(RealizacionUsuario realizacionEspectaculo) {
        this.realizacionEspectaculo = realizacionEspectaculo;
    }
}
