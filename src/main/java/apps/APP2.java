package apps;

import anotaciones.Aweb;


/**
 * Clase encargada de tener todas las aplicaciones
 */

public class APP2 {



  

    @Aweb("porfa")
	public static String prueba2(String num1, String num2) {
        System.out.println(num1 +""+num2);
		return "La suma de "+ num1 +"+"+ num2 +"=" + Integer.toString(Integer.parseInt(num1)+Integer.parseInt(num2)*2);
    }
}