/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkerRunnable implements Runnable{
    
    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected String json = null;
    protected OutputStreamWriter jsonOutput = null;    

    /**
     * Initialise WorkerRunnable members
     * @param clientSocket
     * @param serverText
     */
    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }
    
    
    /**
     * It reads the content of an URL and save it into a Java String.
     * @param urlString
     */
    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
        URL url;
            url = new URL(urlString);
        reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuffer buffer = new StringBuffer();
        int read;
        char[] chars = new char[1024];
        while ((read = reader.read(chars)) != -1)
            buffer.append(chars, 0, read); 

        return buffer.toString();
        } finally {
        if (reader != null)
            reader.close();
    }
}

    /**
     * This function DOWNLOAD the json object from 8080 port at localhost
     * ADSB json is served in localhost:8080/data.json
     */
    private void jsonDownloader(){
        try {
            this.json = readUrl("localhost:8080/data.json");
        } catch (Exception ex) {
            Logger.getLogger(WorkerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /**
     * This function SEND the json object
     */
    private void sendingJson(){
        try {
            jsonOutput.write(json);
        } catch (IOException ex) {
            System.out.println("Se ve que no ha pillao el json");
            Logger.getLogger(WorkerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void run() {
        long time;
        Timer timer=new Timer();
        
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            this.jsonOutput = new OutputStreamWriter(output, StandardCharsets.UTF_8);
            
            time = System.currentTimeMillis();
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " + this.serverText + " - " + time + "").getBytes());
            

            TimerTask task = new TimerTask() {        // TimerTask crea un objeto runnable "task" que se va ejecutará cada cierto tiempo por medio del objeto Timer
                @Override
                public void run() {
                    jsonDownloader();
                    sendingJson();
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }  
            };
            timer.schedule(task, -1, 1000);   // El timer ejecuta la tarea especificada, se inicia en el instante de tiempo que se le diga (-1 es ya !), en intervalos de 1000 ms
            
            //tengo que ver como se para esto, o no
            
            output.close();
            this.jsonOutput.close();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}
   