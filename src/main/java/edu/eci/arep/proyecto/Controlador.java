package edu.eci.arep.proyecto;

import java.io.IOException;

/**
 * Esta clase es el controlador la cual nos permite poder incializar todo el proceso
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