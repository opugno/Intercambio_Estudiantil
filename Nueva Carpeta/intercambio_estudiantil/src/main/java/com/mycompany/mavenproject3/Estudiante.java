package com.mycompany.mavenproject3;
import java.util.*;

/**
 *
 * @author sonailslove
 */
public class Estudiante 
{
    //ATRIBUTOS
    private String rut;
    private String nombre;
    private String carrera;
    private int anioIngreso;
    private String estadoProceso; //"Postulación", "Aceptado", "Rechazado", "En curso"
    private Convenio convenio;
    
    //CONSTRUCTOR
    public Estudiante(String rut, String nombre, String carrera, int anioIngreso, String estadoProceso, Convenio convenio)
    {
        this.rut = rut;
        this.nombre = nombre;
        this.carrera = carrera;
        this.anioIngreso = anioIngreso;
        this.estadoProceso = estadoProceso;
        this.convenio = convenio;
    }
}
