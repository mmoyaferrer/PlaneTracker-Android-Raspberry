/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
 * This text describes a simple multi threaded server. 
 * 
 * The main difference between a Single Server is:
 * Rather than processing the incoming requests in the same thread that accepts 
 * the client connection, the connection is handed off to a worker thread that 
 * will process the request.
 */
public class MultiThreadedServer implements Runnable{
    
    protected int           serverPort      = 8080;
    protected boolean       isStopped       = false;
    protected ServerSocket  serverSocket    = null;
    protected Thread        runningThread   = null;
    
    /**
     * Initialise server port
     * @param port
     */
    public MultiThreadedServer(int port){
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
            throw new RuntimeException("Error closing server", e);
        }
    }

    /**
     * Open the Server Socket in the specified port
     */
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port specified, sorry", e);
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
            } catch(IOException e){
                
                if(isStopped()){
                    System.out.println("Server Stopped");
                    return;
                }
                throw new RuntimeException("Error accepting client connection",e);
            }
            
            new Thread(
                new WorkerRunnable(
                    clientSocket, "Multithreaded Server")
            ).start();   
        }
        System.out.println("Server Stopped.") ;
    }
    
    public static void main(String[] args) {
        MultiThreadedServer server = new MultiThreadedServer(9000);     // Puerto donde atiende el servidor
        new Thread(server).start();

        try {
            Thread.sleep(480 * 1000);    // The thread sleep after 8 minutes to prevent DDOS Attack
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }
    
}
