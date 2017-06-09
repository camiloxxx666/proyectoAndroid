package com.example.camilo.prueba0.modelo;

/**
 * Created by Camilo on 04-jun-17.
 */

public class Espectaculo
{
    private String nombre;
    private String descripcion;
    private String id;
    private String idTipoEspectaculo;
    //private String nombreTipoEspectaculo;
    //private List<Realizacion> realizaciones;
    //private List<Sala> salas;

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

    public String getIdTipoEspectaculo() {
        return idTipoEspectaculo;
    }

    public void setIdTipoEspectaculo(String idTipoEspectaculo) {
        this.idTipoEspectaculo = idTipoEspectaculo;
    }


}
