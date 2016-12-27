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
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkerRunnable implements Runnable{
    
    protected Socket clientSocket = null;
    protected String serverText   = null;
    protected String json = null;



    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }
    
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

    @Override
    public void run() {
        try {
            this.json = readUrl("localhost:8080/data.json");
        } catch (Exception ex) {
            Logger.getLogger(WorkerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            
            
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            
            OutputStreamWriter jsonOutput = new OutputStreamWriter(output, StandardCharsets.UTF_8);
            
            long time = System.currentTimeMillis();
            
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " + this.serverText + " - " + time + "").getBytes());
            jsonOutput.write(json);
 
            output.close();
            jsonOutput.close();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}
   