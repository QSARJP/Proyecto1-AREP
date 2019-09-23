package edu.eci.arep.proyecto;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Controlador {
    
    


    public static void main( String[] args ) throws IOException
    {
        AppServer ap = new AppServer();
        ap.inicializar();
        ap.listen();
    }
}