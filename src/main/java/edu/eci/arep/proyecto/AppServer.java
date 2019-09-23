package edu.eci.arep.proyecto;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import anotaciones.Aweb;
import edu.eci.arep.proyecto.socket.SerSocket;

/**
 * Clase principal en donde estan dos metodos fundamentales el incializar y el
 * escuchar
 */

public class AppServer implements Runnable {

    private static HashMap<String, Handler> listaUrl = new HashMap<String, Handler>();
    private Socket cliente;
    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private ServerSocket serverSocket;

    public AppServer(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        String path = controlReq(cliente);
        requests(cliente, path);
        try {
            cliente.close();
            //serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String controlReq(Socket cliente) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        String inputLine;
        String request = "";
        try {
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.matches("(GET)+.*"))
                    request = inputLine.split(" ")[1];
                if (!in.ready())
                    break;
            }
            if (request == null) {
                request = "/error.html";
            } else if (request.equals("/")) {
                request = "/index.html";

            }
        } catch (IOException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return request;

    }

    /**
     * Este metodo nos permite ver todos lo metodos que tiene la clase APP y pueden
     * ejecutarse dichos metodos con respecto a una clase creada por el
     * desarrollador
     */
    public static void inicializar() {
        try {
            File f = new File(System.getProperty("user.dir") + "/src/main/java/apps");
            File[] ficheros = f.listFiles();
            // Reflections reflections = new Reflections("apps", new
            // SubTypesScanner(false));
            // Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
            for (File fs : ficheros) {
                String name = fs.getName();
                name = "apps." + name.substring(0, name.indexOf("."));
                Class<?> c = Class.forName(name);
                for (Method m : c.getMethods()) {
                    if (m.getAnnotations().length > 0) {
                        Handler handler = new StaticMethodHandler(m);
                        load("/apps/" + m.getDeclaredAnnotation(Aweb.class).value(), handler);
                    }

                }
            }

            // System.out.println(c.getDeclaredAnnotations().length);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void requests(Socket clientSocket, String request) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (request.matches("(/apps/).*")) {
            // System.out.println(request);
            Object[] parametros = extParams(request);
            request = request.subSequence(0, request.indexOf("?")).toString();
            // System.out.println(listaUrl.values().toString());
            if (listaUrl.containsKey(request)) {
                out.print("HTTP/1.1 200 OK \r");
                out.print("Content-Type: text/html \r\n");
                out.print("\r\n");
                try {

                    out.println(parametros == null ? listaUrl.get(request).procesar()
                            : listaUrl.get(request).procesar(parametros));

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
        } else {
            if (request.endsWith(".png")) {
                readImage(out, clientSocket, request);

            } else if (request.endsWith("/linkin")) {
                linkin(out);

            } else if (request.endsWith(".html")) {
                readHTML(out, request);
            } else {
                readHTML(out, "/error.html");
            }

        }

    }

    private static void readImage(PrintWriter out, Socket cliente, String request) {
        File graphicResource = new File("resource/" + request);

        try {
            FileInputStream inputImage = new FileInputStream(graphicResource);
            byte[] bytes = new byte[(int) graphicResource.length()];
            inputImage.read(bytes);

            DataOutputStream binaryOut;
            binaryOut = new DataOutputStream(cliente.getOutputStream());
            binaryOut.writeBytes("HTTP/1.1 200 OK \r\n");
            binaryOut.writeBytes("Content-Type: image/png\r\n");
            binaryOut.writeBytes("Content-Length: " + bytes.length);
            binaryOut.writeBytes("\r\n\r\n");
            binaryOut.write(bytes);
            binaryOut.close();
        } catch (IOException e) {
            
            e.printStackTrace();
        }

    }

    private static void readHTML(PrintWriter out, String request) {
        BufferedReader bf;
        try {
            bf = new BufferedReader(new FileReader("resource" + request));
            out.print("HTTP/1.1 200 OK \r");
            out.print("Content-Type: text/html \r\n");
            out.print("\r\n");
            String line;
            while ((line = bf.readLine()) != null) {
                out.print(line);
            }
        } catch (IOException e) {
           
            e.printStackTrace();
        }
        
    }



   
	


    private static void load (String classPath,Handler han){
        listaUrl.put(classPath, han);
    }


    private static void linkin(PrintWriter out) {
        out.print("HTTP/1.1 200 OK \r");
        out.print("Content-Type: text/html \r\n");
        out.print("\r\n");
        URL url = null;
        try {
            url = new URL("https://www.linkinpark.com/");
        } catch (IOException ex) {
            
                System.err.println(ex);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                out.println(inputLine + "\r\n");
            }

        } catch (IOException x) {
        }
    }

    private static Object[] extParams(String request) {
        Object[] params = null;
        
        if (request.matches("[/apps/]+[a-z]+[?]+[a-z,=,&,0-9]*")) {
            String[] preParams = request.split("\\?")[1].split("&");
            System.out.println(preParams.length);
            params = new Object[preParams.length];
            for (int i = 0; i < preParams.length; i++) {
                System.out.println(preParams[i]);
                String str = preParams[i].split("=")[1];
                System.out.println(str);
                params[i] = str;
            }
        }
        return params;
    }
}