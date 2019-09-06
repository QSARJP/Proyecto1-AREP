package apps;

import anotaciones.Aweb;


/**
 * Clase encargada de tener todas las aplicaciones
 */

public class APP {



    @Aweb(value = "prueba")
    public static String pruebaWeb (){
        return "prueba";
    }

    @Aweb("suma")
	public static String suma(String num1, String num2) {
        //System.out.println(num1 +""+num2);
		return "La suma de "+ num1 +"+"+ num2 +"=" + Integer.toString(Integer.parseInt(num1)+Integer.parseInt(num2));
    }

    @Aweb("resta")
	public static String resta(String num1, String num2) {
   
		return "La suma de "+ num1 +"-"+ num2 +"=" + Integer.toString(Integer.parseInt(num1)-Integer.parseInt(num2));
    }
    
    @Aweb("multi")
	public static String multi(String num1, String num2) {
        //System.out.println(num1 +""+num2);
		return "La multiplicacion de "+ num1 +"*"+ num2 +"=" + Integer.toString(Integer.parseInt(num1)*Integer.parseInt(num2));
    }
    
    @Aweb("div")
	public static String division(String num1, String num2) {
        //System.out.println(num1 +""+num2);
		return "La division de "+ num1 +"/"+ num2 +"=" + Float.toString(Integer.parseInt(num1)/Integer.parseInt(num2));
    }
    
    @Aweb("potencia")
	public static String potencia(String num1,String num2) {
        //System.out.println(num1 +""+num2);
		return "el numero  "+ num1 +"al" + num2 +"es =" + Integer.toString((int ) Math.pow(Integer.parseInt(num1), Integer.parseInt(num2)));
	}



}