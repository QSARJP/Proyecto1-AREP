package edu.eci.arep.proyecto.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
     
    public static ServerSocket createServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(getPort());
            return serverSocket;
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4567.");
            System.exit(1);
            return null;
        }

    }
    
    
    
    private static int getPort() {

        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set (i.e.on localhost)
    }
    
    

}