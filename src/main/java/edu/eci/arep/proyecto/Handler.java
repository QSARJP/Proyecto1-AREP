package edu.eci.arep.proyecto;

import java.lang.reflect.InvocationTargetException;

public interface Handler {

    String procesar(Object[] parametros) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
    String procesar() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException ;
}