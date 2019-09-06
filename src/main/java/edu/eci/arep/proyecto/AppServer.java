package edu.eci.arep.proyecto;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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



import anotaciones.Aweb;

/**
 * Clase principal en donde estan dos metodos fundamentales el incializar y el escuchar
 */

public class AppServer {

    private static HashMap<String,Handler> listaUrl = new HashMap<String,Handler>();

    public static int PORT = 4567;
    /**
     * Este metodo nos permite ver todos lo metodos que tiene la clase APP y pueden ejecutarse dichos metodos con respecto a una clase creada por el desarrollador
     */
    public static void inicializar() {
        try {
            File f = new File(System.getProperty("user.dir") + "/src/main/java/apps");
            File[] ficheros = f.listFiles();
            //Reflections reflections = new Reflections("apps", new SubTypesScanner(false));
            //Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
            for (File fs :ficheros){
                String name = fs.getName();
                name = "apps." + name.substring(0,name.indexOf("."));
                Class<?> c = Class.forName(name);
                for (Method m : c.getMethods()) {
                    if (m.getAnnotations().length > 0){
                        Handler handler = new StaticMethodHandler(m);
                        load("/apps/"+m.getDeclaredAnnotation(Aweb.class).value(),handler);
                    }
                    
                }
            }
            
            //System.out.println(c.getDeclaredAnnotations().length);
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
    /**
     * El metodo listen es el encargado de tener el servidor web funcionado y es el que escucha cualqueir peticion y a cada instante y retorna cada peticion con su respectivo 
     * recurso, si el servidor conoce la direccion 
     * @throws IOException
     */
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

            while ((inputLine = in.readLine())!= null) {
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
            clientSocket.close();

        }

    }

    private static void requests(String request, PrintWriter out, OutputStream outputStream) throws IOException {
        if (request.matches("(/apps/).*")){
            //System.out.println(request);
            Object[] parametros = extParams(request);
            request = request.subSequence(0,request.indexOf("?")).toString();
            //System.out.println(listaUrl.values().toString());
            if (listaUrl.containsKey(request)) {
                out.print("HTTP/1.1 200 OK \r");
                out.print("Content-Type: text/html \r\n");
                out.print("\r\n");
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
                readImage(out,outputStream,request);

            }else if (request.endsWith("/linkin")) {
                linkin(out);

            } else if (request.endsWith(".html")) {
                readHTML(out, request);
            } else {
                readHTML(out, "/error.html");
            }

        }

        
    }
    

    private static void readImage(PrintWriter out, OutputStream outStream, String request) throws IOException {
        File graphicResource= new File("resource/" +request);
        FileInputStream inputImage = new FileInputStream(graphicResource);
        byte[] bytes = new byte[(int) graphicResource.length()];
        inputImage.read(bytes);

        DataOutputStream binaryOut;
        binaryOut = new DataOutputStream(outStream);
        binaryOut.writeBytes("HTTP/1.1 200 OK \r\n");
        binaryOut.writeBytes("Content-Type: image/png\r\n");
        binaryOut.writeBytes("Content-Length: " + bytes.length);
        binaryOut.writeBytes("\r\n\r\n");
        binaryOut.write(bytes);
        binaryOut.close();
    }

    private static void readHTML(PrintWriter out, String request) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader( "resource" + request ));
        out.print("HTTP/1.1 200 OK \r");
        out.print("Content-Type: text/html \r\n");
        out.print("\r\n");
        String line;
        while ((line = bf.readLine()) != null) {
            out.print(line);
        }
    }



    private static int getPort() {

		if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set (i.e.on localhost)
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
            //System.out.println(preParams.length);
            params = new Object[preParams.length];
            for (int i = 0; i < preParams.length; i++) {
                String str = preParams[i].split("=")[1];
                //System.out.println(str);
                params[i] = str;
            }
        }
        return params;
    }
}