package apps;

import anotaciones.Aweb;

public class APP {


	/**
	 *
	 */
	
	private static final String STRING = "/";

	public static String pagina(){
        return "HTTP/1.1 200 OK\r\n"
        + "Content-Type: text/html\r\n"
         + "\r\n"
         + "<!DOCTYPE html>\n"
         + "<html>\n"
         + "<head>\n"
         + "<meta charset=\"UTF-8\">\n"
         + "<title>Title of the document</title>\n"
         + "</head>\n"
         + "<body>\n"
         + "<h1>Mi propio mensaje</h1>\n"
         + "</body>\n"
         + "</html>\n";
    }

    @Aweb(value = STRING)
    public static String pruebaWeb (){
        return "prueba";
    }



}