package edu.eci.arep.proyecto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StaticMethodHandler implements Handler{


    private Method metodo;
    public StaticMethodHandler(Method newMetodo){
        this.metodo = newMetodo;
    }


    public String procesar() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        return (String) metodo.invoke(null, null );
    }
    public String procesar(Object[] parametros) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        return (String) metodo.invoke(null, parametros);
    }

}