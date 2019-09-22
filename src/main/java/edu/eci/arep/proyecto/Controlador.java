package edu.eci.arep.proyecto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.eci.arep.proyecto.socket.SerSocket;
import edu.eci.arep.proyecto.socket.CliSocket;

/**
 * Esta clase es el controlador la cual nos permite poder incializar todo el proceso
 *
 */


public class Controlador 
{
    final static ExecutorService service = Executors.newCachedThreadPool();
    public static void main( String[] args ) throws IOException
    {
        ServerSocket serverSocket = SerSocket.create();
        AppServer.inicializar();
        while (true){
            Socket cliente = CliSocket.getClient(serverSocket);
            service.execute(new AppServer(cliente));
        }
       
    }
}