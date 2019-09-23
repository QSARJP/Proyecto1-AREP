package edu.eci.arep.proyecto.socket;


import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class CliSocket {
    
    /**
    * Este metodo se encarga de esperar a que un cliente realice una peticion.
    * y conectarse a este.
    *
    * @param serverSocket el servidor.
    * @return Socket cliente que realizo la peticion.
    */
   public static  Socket getClient(ServerSocket serverSocket) {
       try {
           System.out.println("Listo para recibir ...");
           Socket clientSocket = serverSocket.accept();
           return clientSocket;
       } catch (IOException e) {
           getClient(serverSocket);
           
       }
       return null;
       
   }
   
}