package apps;

import anotaciones.Aweb;


/**
 * Clase encargada de tener todas las aplicaciones
 */

public class APP3 {



  

    @Aweb("intento")
	public static String prueba2(String num1, String num2) {
        
		return "las dos palabras son "+ num1 +"+"+ num2 ;
    }
}