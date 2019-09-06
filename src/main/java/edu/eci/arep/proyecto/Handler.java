package edu.eci.arep.proyecto;

import java.lang.reflect.InvocationTargetException;

/**
 * Interfaz necesaria para poder realizar el invoke de los metodos
 */

public interface Handler {

    String procesar(Object[] parametros) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
    String procesar() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException ;
}