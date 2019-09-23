package edu.eci.arep.proyecto.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class ClientSocket {
    
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