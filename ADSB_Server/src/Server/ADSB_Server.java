///////////////////////////////////////////////////////////////////
///////////  ADS-B PlaneTracker Raspberry-Android /////////////////
///////////////////////////////////////////////////////////////////
///////////               Authors:                      ///////////  
//       Manuel Moya Ferrer - Juan Manuel López Torralba         //
//                                                               //
//  OpenSource with Creative Commons Attribution-NonCommercial-  //
//  ShareAlike license ubicated in GitHub throughout:            //
// https://github.com/mmoyaferrer/PlaneTracker-Android-Raspberry //
//                                                               //
//  This work is licensed under the Creative Commons             //
//  Attribution-NonCommercial-ShareAlike CC BY-NC-SA License.    //
//  To view a copy of the license, visit                         //
//  https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode  //
///////////                                             ///////////
///////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////



package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * A thread is responsible for accepting connections with a client, then 
 * the connection is handed off to a worker thread that should process 
 * the request (download and send ADS-B data).
 */
public class ADSB_Server implements Runnable{
    
    protected int           serverPort      = 8080;
    protected boolean       isStopped       = false;
    protected ServerSocket  serverSocket    = null;
    protected Thread        runningThread   = null;
    
    /**
     * Initialise server port
     * @param port where the server is listening to.
     */
    public ADSB_Server(int port){
        this.serverPort = port;
    }
    
    /**
     * Return the state of isStopped flag
     * True or False.
     */
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    /**
     * Close the socket with the client Stopping the server.
     */
    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server, please check the code again", e);
        }
    }

    /**
     * Open the Server Socket in the specified port
     */
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port specified, please find a free port", e);
        }
    }

    @Override
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();   // It creates a thread to proccess the incoming request
        }
        openServerSocket();                              // It open a socket


// isStopped==false  (El flag dice que el server está activo) , se crea un socket con el cliente que se le manda a una hebra trabajadora 
        // que se encarga de descargarse el json y mandarlo por dicho socket.
        while(! isStopped()){
            Socket clientSocket = null;
            
            try {
                clientSocket = this.serverSocket.accept();
                
                
                InputStream input  = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();

            } catch(IOException e){
                
                if(isStopped()){
                    System.out.println("Server is Stopped");
                    return;
                }
                throw new RuntimeException("Error accepting client connection, please check the code",e);
            }
            
            new Thread(
                new WorkerRunnable(clientSocket)
            ).start();   
        }
        System.out.println("Server Stopped.") ;
    }
    
    public static void main(String[] args) {
        ADSB_Server server = new ADSB_Server(9000);     // Puerto donde atiende el servidor
        new Thread(server).start();

        try {
            Thread.sleep(18000 * 1000);    // The thread sleep after 5 hours to prevent DDOS Attack
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }
    
}