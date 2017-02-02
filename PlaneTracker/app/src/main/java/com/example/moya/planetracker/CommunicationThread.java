package com.example.moya.planetracker;

import android.os.AsyncTask;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by moya on 30/12/2016.
 */

public class CommunicationThread extends AsyncTask<String, String, String> {

    private Socket socket;
    private BufferedReader br;
    private String inputString="";
    private JSONObject jsonString;

    public static String aviones[][]=new String[10][13]; //10 aviones como maximo con 13 caracteristicas
    public static int numAvion=-1;


    public CommunicationThread(Socket socket, BufferedReader br){

        this.socket=socket;
        this.br=br;

    }

    /*
    [
{"hex":"3443d3", "squawk":"4673", "flight":"VLG3014 ", "lat":37.716344, "lon":-2.913646, "validposition":1, "altitude":35000,  "vert_rate":0,"track":225, "validtrack":1,"speed":427, "messages":74, "seen":0},
{"hex":"345313", "squawk":"3767", "flight":"VLG2422 ", "lat":37.573568, "lon":-2.912750, "validposition":1, "altitude":38000,  "vert_rate":0,"track":72, "validtrack":1,"speed":420, "messages":1217, "seen":0}
]
     */

    @Override
    protected String doInBackground(String... strings) {

        try {
            socket=new Socket("172.24.1.1",9000);
            System.out.println("CONECTED");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("BufferedReader READY");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int i=0;

        System.out.println("Esperando a recibir el primer mensaje");

        // Tenemos en cuenta q podemos recibir mas de un avion en cada json, por lo q agregamos un avion al vector
        // de aviones si el tiempo transcurrido es menor de 5, si es mayor que 3 reseteamos

        int resta=3;
        long tReferencia=-3000;



        while(i==0) {
            try {
                //Recibimos datos
                inputString=br.readLine();

                if(!inputString.equals("[") && !inputString.equals("]")) {
                    if (System.currentTimeMillis() - tReferencia >= 3000) {
                        System.out.println("Recibimos JSON");
                        tReferencia = System.currentTimeMillis();
                        aviones = new String[10][13];
                        numAvion = 1;
                    } else {
                        System.out.println("Hay un avion más en este JSON");
                        numAvion++;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(inputString!=null && !inputString.equals("[") && !inputString.equals("]")) {
                    System.out.println(inputString);
                    jsonString = new JSONObject(inputString); // llegan 2 jsonString si esqe hay 2 aviones
                    procesarJSON(jsonString);

                }
            } catch (JSONException e) {
                //e.printStackTrace();
            }

        }

        return null;
    }

    private void procesarJSON(JSONObject jsonObject) throws JSONException {

        String hex = jsonObject.getString("hex");
        String squawk = jsonObject.getString("squawk");
        String flight = jsonObject.getString("flight");
        String lat = jsonObject.getString("lat");
        String lon = jsonObject.getString("lon");
        String validposition = jsonObject.getString("validposition");
        String altitude = jsonObject.getString("altitude");
        String vert_rate = jsonObject.getString("vert_rate");
        String track= jsonObject.getString("track");
        String validtrack = jsonObject.getString("validtrack");
        String speed = jsonObject.getString("speed");
        String messages = jsonObject.getString("messages");
        String seen = jsonObject.getString("seen");

        aviones[numAvion-1][0]=hex;
        aviones[numAvion-1][1]=squawk;
        aviones[numAvion-1][2]=flight;
        aviones[numAvion-1][3]=lat;
        aviones[numAvion-1][4]=lon;
        aviones[numAvion-1][5]=validposition;
        aviones[numAvion-1][6]=altitude;
        aviones[numAvion-1][7]=vert_rate;
        aviones[numAvion-1][8]=track;
        aviones[numAvion-1][9]=validtrack;
        aviones[numAvion-1][10]=speed;
        aviones[numAvion-1][11]=messages;
        aviones[numAvion-1][12]=seen;


    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
