/*
 *  ADSB_Server is a simple multithreaded server which accepts a client
 *  connection and then is proccesed by a worker thread.
 *
 *  2017  Juan Manuel López Torralba & Manuel Moya Ferrer
 *
 *  This work is licensed under the Creative Commons
 *  Attribution-NonCommercial-ShareAlike 
 *  CC BY-NC-SA
 *  License 
 *  
 * This license lets others remix, tweak, and build upon your work
 * non-commercially, as long as they credit you and license their new 
 * creations under the identical terms.
 *
 * To view a copy of the license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 */



package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This thread is responsible for downloading json, which contains our ADS-B
 * data, later json is sent it to an Android Application for many purposes
 * @author Juanma
 */
public class WorkerRunnable implements Runnable{
    
    protected Socket clientSocket = null;
    protected String json = null;
    protected OutputStreamWriter jsonOutput = null;   
    protected InputStream input = null;
    protected OutputStream output = null;

    /**
     * Initialise WorkerRunnable members
     * @param clientSocket client socket 
     */
    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
    /**
     * It reads the content of an URL and save it into a Java String.
     * @param urlString URL address of our json
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
            this.json = readUrl("http://192.168.0.190:8080/data.json");
        } catch (Exception ex) {
            Logger.getLogger(WorkerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /**
     * This function SEND the json object
     */
    private void sendingJson() throws IOException{
        try {
            jsonOutput.write(json);
            jsonOutput.flush();
        } catch (IOException ex) {
            jsonOutput.close();
            clientSocket.close();
            input.close();
            output.close();
            Logger.getLogger(WorkerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } 
            
        
    }
    
    
    @Override
    public void run() {

        try {
            this.input  = clientSocket.getInputStream();
            this.output = clientSocket.getOutputStream();
            this.jsonOutput = new OutputStreamWriter(output);
            
           int i=0;
           while(i==0){ 
               jsonDownloader();
               sendingJson();
               
                try {
                    Thread.sleep(5000); // 5 seconds
                } catch (InterruptedException ex) {
                    Logger.getLogger(WorkerRunnable.class.getName()).log(Level.SEVERE, null, ex);
                }
           }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}