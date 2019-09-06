package apps;

import anotaciones.aWeb;;

public class APP {



    @aWeb(value = "prueba")
    public static String pruebaWeb (){
        return "prueba";
    }

    @aWeb("suma")
	public static String suma(String num1, String num2) {
        //System.out.println(num1 +""+num2);
		return "La suma de "+ num1 +"+"+ num2 +"=" + Integer.toString(Integer.parseInt(num1)+Integer.parseInt(num2));
    }

    @aWeb("resta")
	public static String resta(String num1, String num2) {
   
		return "La suma de "+ num1 +"-"+ num2 +"=" + Integer.toString(Integer.parseInt(num1)-Integer.parseInt(num2));
    }
    
    @aWeb("multi")
	public static String multi(String num1, String num2) {
        //System.out.println(num1 +""+num2);
		return "La multiplicacion de "+ num1 +"*"+ num2 +"=" + Integer.toString(Integer.parseInt(num1)*Integer.parseInt(num2));
    }
    
    @aWeb("div")
	public static String division(String num1, String num2) {
        //System.out.println(num1 +""+num2);
		return "La division de "+ num1 +"/"+ num2 +"=" + Float.toString(Integer.parseInt(num1)/Integer.parseInt(num2));
    }
    
    @aWeb("potencia")
	public static String potencia(String num1,String num2) {
        //System.out.println(num1 +""+num2);
		return "el numero  "+ num1 +"al" + num2 +"es =" + Integer.toString((int ) Math.pow(Integer.parseInt(num1), Integer.parseInt(num2)));
	}



}