package edu.eci.arep.proyecto;


import edu.eci.arep.proyecto.StaticMethodHandler;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class AppServer implements Runnable {

    private static HashMap<String, StaticMethodHandler> dic = new HashMap<String, StaticMethodHandler>();
    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private Socket clientSocket;
    private ServerSocket serverSocket;
    
    public AppServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
    
    @Override
    public void run() {
        String path = controlRequests(clientSocket);
        System.out.println(path);
        write(clientSocket, path);
        try {
            clientSocket.close();
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   
    public static void inicializar() {
        try {
            File f = new File(System.getProperty("user.dir") + "/src/main/java/apps");
            File[] ficheros = f.listFiles();
            for (File fs : ficheros) {
                String name = fs.getName();
                name = "apps." + name.substring(0, name.indexOf("."));
                Class<?> c = Class.forName(name);
                for (Method m : c.getMethods()) {
                    if (m.getAnnotations().length > 0) {
                        StaticMethodHandler handler = new StaticMethodHandler(m);
                        dic.put(m.getName(), handler);
                    }
                }
            }
        } catch (Exception ex) {
        }
    }


    public static String controlRequests(Socket clientSocket) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        String inputLine;
        String path = "";
        try {
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
                if (inputLine.contains("GET")) {
                    String[] get = inputLine.split(" ");
                    path = get[1];
                } else if (inputLine.contains("POST")) {
                    break;
                }
            }
            if (path == null){
                path = "/error.html";
            }else if (path.equals("/")){
                path = "/index.html";
    
            }
        } catch (IOException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return path;
    }

 
    private void write(Socket clientSocket, String path) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (path.contains("html")) {
            pages(out, path);
        } else if (path.contains("jpg")) {
            images(out, path, clientSocket);
        } else if (path.contains("/app")) {
            services(out, path);
        }
        else {
            pages(out, "/error.html");
        }
    }


    private void images(PrintWriter out, String path, Socket clientSocket) {
        String urlInputLine = "";
        int img = path.indexOf('/') + 1;
        while (!urlInputLine.endsWith(".jpg") && img < path.length()) {
            urlInputLine += (path.charAt(img++));
        }
        try {
            File image = new File(classLoader.getResource(urlInputLine).getFile());
            BufferedImage bImage = ImageIO.read(image);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos);
            byte[] imagen = bos.toByteArray();
            DataOutputStream outImg = new DataOutputStream(clientSocket.getOutputStream());
            outImg.writeBytes("HTTP/1.1 200 OK \r\n");
            outImg.writeBytes("Content-Type: image/jpg\r\n");
            outImg.writeBytes("Content-Length: " + imagen.length);
            outImg.writeBytes("\r\n\r\n");
            outImg.write(imagen);
            outImg.close();
            out.println(outImg.toString());
        } catch (Exception e) {
            //notFound();
        }
    }


    private static void pages(PrintWriter out, String path) {
        out.print("HTTP/1.1 200 OK \r");
        out.print("Content-Type: text/html \r\n");
        out.print("\r\n");
        int pag = path.indexOf('/') + 1;
        String urlInputLine = "";
        while (!urlInputLine.endsWith(".html") && pag < path.length()) {
            urlInputLine += (path.charAt(pag++));
        }
        try {
            BufferedReader readerFile = new BufferedReader(new FileReader(classLoader.getResource(urlInputLine).getFile()));
            while (readerFile.ready()) {
                out.println(readerFile.readLine());
            }
            out.close();
        } catch (Exception e) {
            // notFound();
        }
    }


    private void services(PrintWriter out, String path) {
        out.print("HTTP/1.1 200 OK \r");
        out.print("Content-Type: text/html \r\n");
        out.print("\r\n");
        String[] url = path.split("/");
        String cad = url[2];
        String metodo = "";
        String param = "";
        System.out.println(cad);
        if (cad.contains("?")) {
            System.out.println("entraa");
            url = cad.split("\\?");
            metodo = url[0];
            cad = url[1];
            url = cad.split("=");
            param = url[1];
            int temp = Integer.parseInt(param);
            Object[] atributo = new Object[]{temp};
            try {
                out.print(dic.get(metodo).procesar(atributo));
                out.close();
            } catch (Exception ex) {
                Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("entra2");
            metodo = cad;
            try {
                out.print(dic.get(metodo).procesar());
                out.close();
            } catch (Exception ex) {
                Logger.getLogger(AppServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void notFound(PrintWriter out) {
        out.print("HTTP/1.1 200 OK \r");
        out.print("Content-Type: text/html \r\n");
        out.print("\r\n");
        out.print("<!DOCTYPE html>");
        out.print("<html>");
        out.print("<head>");
        out.print("<meta charset=\"UTF-8\">");
        out.print("<title>Proyecto</title> ");
        out.print("</head>");
        out.print("<body>");
        out.print("<h1>Page not found</h1>");
        out.print("</body>");
        out.print("</html>");
        out.flush();
    }
}