package edu.eci.arep.proyecto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * Clase donde nos permite invocar los metodos con o sin parametros
 */
public class StaticMethodHandler implements Handler{

    
    private Method metodo;
    public StaticMethodHandler(Method newMetodo){
        this.metodo = newMetodo;
    }

    /**
     * metodo ecargado de invocar los metodos pero sin parametros
     */
    public String procesar() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        return (String) metodo.invoke(null, null );
    }

    /**
     * metodo encargado de invocar los emtodos con paramteros
     */
    public String procesar(Object[] parametros) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        return (String) metodo.invoke(null, parametros);
    }

}