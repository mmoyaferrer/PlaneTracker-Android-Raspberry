package com.example.moya.planetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Manuel on 10/4/2016.
 */
public class CustomAdapterMenu extends ArrayAdapter<String> {


    public CustomAdapterMenu(Context context, String vectorNombresArchivos[]) {
        super(context, R.layout.customrowmenu, vectorNombresArchivos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vectorInflater = LayoutInflater.from(getContext());
        View customView=vectorInflater.inflate(R.layout.customrowmenu, parent, false);

        String itemVector=getItem(position);
        TextView vectorTextNombreArchivo=(TextView)customView.findViewById((R.id.textViewCustomRowMenu));

        //ImageView vectorImage=(ImageView)customView.findViewById(R.id.imageViewCustomVectorCloud);
        vectorTextNombreArchivo.setText(itemVector);
        //vectorImage.setImageResource(R.mipmap.iconperson);

        return customView;
    }
}
