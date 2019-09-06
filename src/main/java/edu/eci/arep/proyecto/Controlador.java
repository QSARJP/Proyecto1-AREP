package edu.eci.arep.proyecto;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class Controlador 
{
    public static void main( String[] args ) throws IOException
    {
        AppServer ap = new AppServer();
        ap.inicializar();
        ap.listen();
    }
}