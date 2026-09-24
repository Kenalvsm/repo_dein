package com.dm2.tabla;

import java.util.Date;

public class Persona {
    private int id;
    private String nombre;
    private String apellido;
    private Date f_nac;

    public Persona (int id, String nombre, String apellido, Date f_nac){
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.f_nac = f_nac;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Date getF_nac() {
        return f_nac;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setF_nac(Date f_nac) {
        this.f_nac = f_nac;
    }
}
