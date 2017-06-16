package com.example.camilo.prueba0.modelo;

/**
 * Created by User on 15/06/2017.
 */

public class Sector
{
    private String id;
    private String nombre;
    private String capacidad;
    private String precio;

    public Sector() {
    }

    public Sector(String id, String nombre, String capacidad, String precio) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.precio = precio;
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

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
