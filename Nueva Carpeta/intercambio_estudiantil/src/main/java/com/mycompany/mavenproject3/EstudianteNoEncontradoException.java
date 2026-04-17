package com.mycompany.mavenproject3;
public class EstudianteNoEncontradoException extends Exception 
{
    public EstudianteNoEncontradoException(String rut) {
        super("No se encontró estudiante con RUT: " + rut);
    }
}
