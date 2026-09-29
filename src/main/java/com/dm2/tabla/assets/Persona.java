package com.dm2.tabla.assets;

import java.util.Date;

/**
 * Representa una persona almacenada en la base de datos.
 * <p>
 * Cada instancia de esta clase se corresponde con una fila de la tabla
 * {@code personas} y contiene los datos básicos: identificador, nombre,
 * apellido y fecha de nacimiento.
 * </p>
 *
 * @author Kenneth
 * @version 1.0
 * @since 1.0
 */
public class Persona {

    /** Identificador único de la persona (clave primaria en la BBDD). */
    private int id;

    /** Nombre de la persona. */
    private String nombre;

    /** Apellido de la persona. */
    private String apellido;

    /** Fecha de nacimiento de la persona. */
    private Date f_nac;

    /**
     * Crea una nueva instancia de {@code Persona} con todos sus atributos.
     *
     * @param id      identificador único de la persona
     * @param nombre  nombre de la persona
     * @param apellido apellido de la persona
     * @param f_nac   fecha de nacimiento de la persona
     */
    public Persona (int id, String nombre, String apellido, Date f_nac){
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.f_nac = f_nac;
    }

    /**
     * Devuelve el identificador único de la persona.
     *
     * @return el {@code id} de la persona
     */
    public int getId() {
        return id;
    }

    /**
     * Devuelve el nombre de la persona.
     *
     * @return el {@code nombre} de la persona
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el apellido de la persona.
     *
     * @return el {@code apellido} de la persona
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Devuelve la fecha de nacimiento de la persona.
     *
     * @return la {@code f_nac} de la persona
     */
    public Date getF_nac() {
        return f_nac;
    }

    /**
     * Establece el identificador único de la persona.
     *
     * @param id nuevo identificador de la persona
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Establece el nombre de la persona.
     *
     * @param nombre nuevo nombre de la persona
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Establece el apellido de la persona.
     *
     * @param apellido nuevo apellido de la persona
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Establece la fecha de nacimiento de la persona.
     *
     * @param f_nac nueva fecha de nacimiento de la persona
     */
    public void setF_nac(Date f_nac) {
        this.f_nac = f_nac;
    }
}