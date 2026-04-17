package com.mycompany.mavenproject3;
import java.util.*;

/**
 *
 * @author sonailslove
 */
public class Tramite.java 
{
    public enum Estado {EN_PROCESO, COMPLETO}
    
    //ATRIBUTOS
    private String idTramite;
    private Estudiante estudiante;
    private Map<TipoDocumento, DocumentoSubido> documento = new HashMap<>();
    private Estado estado =  Estado.EN_PROCESO;
    private Convenio convenio;
    
    //CONSTRUCTOR
    public Tramite(String idTramite, Estudiante estudiante)
    {
        this.idTramite = idTramite;
        this.estudiante = estudiante;
    }
    
    public Tramite(String idTramite, Estudiante estudiante, Convenio convenio) {
        this.idTramite = idTramite;
        this.estudiante = estudiante;
        this.estado = Estado.EN_PROCESO;
        this.documento = new HashMap<>();
        this.convenio = convenio;
    }
}
