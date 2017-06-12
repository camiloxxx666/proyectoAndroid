package com.example.camilo.prueba0.modelo;

/**
 * Created by User on 12/06/2017.
 */

public class TipoEspectaculo
{
    private String id;
    private String nombre;

    public TipoEspectaculo(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
}
