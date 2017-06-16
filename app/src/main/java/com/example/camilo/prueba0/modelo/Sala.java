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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sala sala = (Sala) o;

        return id.equals(sala.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
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
