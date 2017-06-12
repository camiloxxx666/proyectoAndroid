package com.example.camilo.prueba0.modelo;

/**
 * Created by Camilo on 04-jun-17.
 */

public class Sala
{
    private String id;
    private String nombre;
    private String descripcion;
    private String totalLocalidad;

    public Sala(String id, String nombre, String descripcion, String totalLocalidad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.totalLocalidad = totalLocalidad;
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

    public String getTotalLocalidad() {
        return totalLocalidad;
    }

    public void setTotalLocalidad(String totalLocalidad) {
        this.totalLocalidad = totalLocalidad;
    }
}
