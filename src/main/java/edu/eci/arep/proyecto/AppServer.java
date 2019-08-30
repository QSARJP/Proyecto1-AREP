package edu.eci.arep.proyecto;

import java.lang.reflect.Method;
import java.util.HashMap;



public class AppServer {

    private HashMap<String,Handler> listaUrl = new HashMap<String,Handler>();


    public  void inicializar() {
        try {
            Class<?> c = Class.forName("apps.APP");
            for( Method m : c.getDeclaredMethods()){
                if (m.getAnnotations().length > 0){
                    System.out.println(m.getAnnotations().length);
                    System.out.println(m.getName());
                }
                
            }
            //System.out.println(c.getDeclaredAnnotations().length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void load (String classPath){
        listaUrl.put(classPath, new StaticMethodHandler());
    }
}