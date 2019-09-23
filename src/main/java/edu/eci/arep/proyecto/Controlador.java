package edu.eci.arep.proyecto;


import edu.eci.arep.proyecto.socket.ClientSocket;
import edu.eci.arep.proyecto.socket.SocketServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Controlador {
    
    
    final static ExecutorService service = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        
        ServerSocket serverSocket = SocketServer.createServer();
        AppServer.inicializar();
        while (true) {
            Socket clientSocket = ClientSocket.getClient(serverSocket);
            service.execute(new AppServer(clientSocket));
        }
    }
}