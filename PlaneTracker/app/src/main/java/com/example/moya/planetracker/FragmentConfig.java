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

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FragmentConfig extends Fragment{


    public Button buttonCambiarPuerto;
    public Button buttonCambiarIP;
    public EditText editTextPuerto;
    public EditText editTextIP;


    public FragmentConfig() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_config, container, false);

        buttonCambiarIP=(Button)view.findViewById(R.id.buttonIP);
        buttonCambiarPuerto=(Button)view.findViewById(R.id.buttonPuerto);
        editTextIP =(EditText) view.findViewById(R.id.textViewIP);
        editTextPuerto =(EditText) view.findViewById(R.id.textViewPuerto);

        buttonCambiarIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.IP= String.valueOf(editTextIP.getText());
                Toast.makeText(getContext(),"IP cambiada a: "+MainActivity.IP,Toast.LENGTH_SHORT).show();
            }
        });

        buttonCambiarPuerto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.Puerto= Integer.parseInt(String.valueOf(editTextPuerto.getText()));
                Toast.makeText(getContext(),"Puerto cambiado a: "+MainActivity.Puerto,Toast.LENGTH_SHORT).show();
            }
        });




        return view;
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
