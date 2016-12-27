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
 *
 * @author Juanma
 */
public class MultiThreadedServer implements Runnable{
    
    protected int           serverPort      = 8080;
    protected boolean       isStopped       = false;
    protected ServerSocket  serverSocket    = null;
    protected Thread        runningThread   = null;
    
    public MultiThreadedServer(int port){
        this.serverPort = port;
    }
    
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }

    @Override
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        
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
        MultiThreadedServer server = new MultiThreadedServer(9000);
        new Thread(server).start();

        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        server.stop();
    }
    
}
