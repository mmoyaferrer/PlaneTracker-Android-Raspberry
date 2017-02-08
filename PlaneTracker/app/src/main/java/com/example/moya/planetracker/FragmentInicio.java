///////////////////////////////////////////////////////////////////
/////////// A DS-B PlaneTracker Raspberry-Android /////////////////
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

package com.example.moya.planetracker;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.net.Socket;


public class FragmentInicio extends Fragment implements OnMapReadyCallback{

    private OnFragmentInteractionListener mListener;

    SupportMapFragment supportMapFragment;


    public Socket socketConnection;
    public BufferedReader bufferedReader;
    public static Button buttonConnect;
    private CommunicationThread communicationThread;

    private GoogleMap mGoogleMap;

    private TextView textViewAviones;

    double latitud=-1;
    double longitud=-1;

    public FragmentInicio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_inicio, container, false);

        supportMapFragment=SupportMapFragment.newInstance();

        /** ///////////// */

        buttonConnect=(Button)view.findViewById(R.id.button_Conectar);
        textViewAviones=(TextView)view.findViewById(R.id.textViewAviones);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicationThread =new CommunicationThread(socketConnection,bufferedReader);
                communicationThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                new AsyncTask<String, String, String>() {

                    @Override
                    protected String doInBackground(String... strings) {
                        int x = 0;
                        boolean noData=true;

                        while (x == 0){

                            System.out.println("ACTUALIZA TEXT");
                            String datos="";
                            publishProgress("borrarmarcadores");



                            for (int i = 0; i < CommunicationThread.numAvion; i++) {
                                System.out.println("numero aviones: "+CommunicationThread.numAvion);
                                if(CommunicationThread.aviones[i][0]!=null) {
                                    datos=datos+"Hex Avión "+(i+1)+" : "+CommunicationThread.aviones[i][0]+"\n";
                                    datos=datos+"Squawk Avión "+(i+1)+" : "+CommunicationThread.aviones[i][1]+"\n";
                                    datos=datos+"Flight Avión "+(i+1)+" : "+CommunicationThread.aviones[i][2]+"\n";
                                    datos=datos+"Latitud Avión "+(i+1)+" : "+CommunicationThread.aviones[i][3]+"\n";
                                    datos=datos+"Longitud Avión "+(i+1)+" : "+CommunicationThread.aviones[i][4]+"\n";
                                    datos=datos+"Valid Position Avión "+(i+1)+" : "+CommunicationThread.aviones[i][5]+"\n";
                                    datos=datos+"Altitud Avión "+(i+1)+" : "+CommunicationThread.aviones[i][6]+"\n";
                                    datos=datos+"Vertical Rate Avión "+(i+1)+" : "+CommunicationThread.aviones[i][7]+"\n";
                                    datos=datos+"Track Avión "+(i+1)+" : "+CommunicationThread.aviones[i][8]+"\n";
                                    datos=datos+"ValidTrack Avión "+(i+1)+" : "+CommunicationThread.aviones[i][9]+"\n";
                                    datos=datos+"Speed Avión "+(i+1)+" : "+CommunicationThread.aviones[i][10]+"\n";
                                    datos=datos+"Messages Avión "+(i+1)+" : "+CommunicationThread.aviones[i][11]+"\n";
                                    datos=datos+"Seen Avión "+(i+1)+" : "+CommunicationThread.aviones[i][12]+"\n";

                                    datos=datos+"\n";

                                    publishProgress(datos);
                                    publishProgress("la"+CommunicationThread.aviones[i][3]);
                                    publishProgress("lo"+CommunicationThread.aviones[i][4]);

                                    noData=false;


                                }
                            }

                            if(!noData){
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                publishProgress("cargando");
                            }

                        }

                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        super.onProgressUpdate(values);
                        String texto = "";

                        for (int j = 0; j < values.length; j++) {
                            texto = texto + values[j];
                        }

                        if (texto != "") {
                            if (texto.startsWith("la")) {
                                latitud = Double.parseDouble(texto.substring(2, texto.length()));
                            } else if (texto.startsWith("lo")) {
                                longitud = Double.parseDouble(texto.substring(2, texto.length()));
                                if (mGoogleMap != null && latitud != -1 && longitud != -1 && latitud != 0 && longitud != 0) {
                                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud))

                                            .anchor(0.5f, 0.5f)
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.planeimag)));
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitud, longitud), 8));
                                }
                            } else if (texto.startsWith("borrarmarcadores")) {
                                mGoogleMap.clear();
                            } else if (texto.startsWith("cargando")) {
                                textViewAviones.setText("Esperando información de aviones cercanos...");
                                FragmentInicio.buttonConnect.setVisibility(View.GONE);
                            } else {
                                textViewAviones.setText(texto);
                            }
                        }
                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });



        supportMapFragment.getMapAsync(this);
        android.support.v4.app.FragmentManager sFm=getChildFragmentManager();
        sFm.beginTransaction().add(R.id.mapsFragment,supportMapFragment).commit();


        /** ///////////// */


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.185297, -3.613093), 6));
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
