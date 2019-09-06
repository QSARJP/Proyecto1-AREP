package edu.eci.arep.proyecto;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.sound.midi.Patch;


import anotaciones.AWeb;



public class AppServer {

    private static HashMap<String,Handler> listaUrl = new HashMap<String,Handler>();

    public static int PORT = 4567;

    public static void inicializar() {
        try {
            Class<?> c = Class.forName("apps.APP");
            for( Method m : c.getDeclaredMethods()){
                if (m.getAnnotations().length > 0){
                    Handler handler = new StaticMethodHandler(m);
                    load("/apps/"+m.getDeclaredAnnotation(AWeb.class).value(),handler);
                }
                
            }
            //System.out.println(c.getDeclaredAnnotations().length);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void listen() throws IOException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4567.");
            System.exit(1);
        }
        while (true) {
            Socket clientSocket = null;
            try {
                System.out.println("Ready to listen ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request = "";
            String inputLine;

            while (!(inputLine = in.readLine()).equals("")) {
                if (inputLine.matches("(GET)+.*"))
                    request = inputLine.split(" ")[1];
                if (!in.ready())
                    break;
            }
            //System.out.println(request);
            if (request == null){
                request = "/error.html";
            }else if (request.equals("/")){
                request = "/index.html";
    
            }
            requests(request, out, clientSocket.getOutputStream());
            

            out.close();
            in.close();
        }

    }

    private static void requests(String request, PrintWriter out, OutputStream outputStream) throws IOException {
        if (request.matches("(/apps/).*")){
            //System.out.println(request);
            Object[] parametros = extParams(request);
            request = request.subSequence(0,request.indexOf("?")).toString();
            //System.out.println(listaUrl.values().toString());
            if (listaUrl.containsKey(request)) {
                out.println("HTTP/1.1 200 OK\r");
                out.println("Content-Type: text/html\r");
                out.println("\r\n");
                try {
                    
                    out.println(parametros == null ? listaUrl.get(request).procesar() : listaUrl.get(request).procesar(parametros));
                
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }else{
            if (request.endsWith(".png")) {
                readImage(  out,outputStream,request);
            } else if (request.endsWith(".html")) {
                readHTML(out, request);
            } else {
                readHTML(out, "/error.html");
            }

        }

        
    }
    

    private static void readImage(PrintWriter out, OutputStream outStream, String request) throws IOException {
        out.println("HTTP/1.1 200 OK\r");
        out.println("Content-Type: image/png\r");
        out.println("\r\n");
        BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir") + "/resource" + request));
        ImageIO.write(image, "PNG", outStream);
    }

    private static void readHTML(PrintWriter out, String request) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/resource" + request ));
        out.print("HTTP/1.1 200 OK \r\n");
        out.print("Content-Type: text/html \r\n");
        out.print("\r\n");
        String line;
        while ((line = bf.readLine()) != null) {
            out.print(line);
        }
    }



    private static int getPort() {

		ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return PORT;
	}


    private static void load (String classPath,Handler han){
        listaUrl.put(classPath, han);
    }

    private static Object[] extParams(String request) {
        Object[] params = null;
        
        if (request.matches("[/apps/]+[a-z]+[?]+[a-z,=,&,0-9]*")) {
            String[] preParams = request.split("\\?")[1].split("&");
            //System.out.println(preParams.length);
            params = new Object[preParams.length];
            for (int i = 0; i < preParams.length; i++) {
                System.out.println(preParams[i]);
                params[i] = preParams[i];
            }
        }
        return params;
    }
}