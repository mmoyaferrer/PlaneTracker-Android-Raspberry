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

import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements  FragmentInicio.OnFragmentInteractionListener
        ,FragmentConfig.OnFragmentInteractionListener, FragmentAcercaDe.OnFragmentInteractionListener
        ,FragmentAyuda.OnFragmentInteractionListener{


    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private String arrayMenu[];

    public Socket socketConnection;
    public BufferedReader bufferedReader;

    public static String IP="172.24.1.1";
    public static int Puerto=9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.layoutprincipal);
        mActivityTitle = getTitle().toString();

        mDrawerList = (ListView) findViewById(R.id.navList);
        this.addDrawerItems();
        this.setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportFragmentManager().beginTransaction().add(R.id.contentframe, new FragmentInicio()).commit();


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(arrayMenu[position].equals("Tracking")){
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.contentframe)).commit();
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    getSupportFragmentManager().beginTransaction().add(R.id.contentframe, new FragmentInicio()).commit();
                }
                else if(arrayMenu[position].equals("Configuración")){
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.contentframe)).commit();
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    getSupportFragmentManager().beginTransaction().add(R.id.contentframe, new FragmentConfig()).commit();
                }
                else if(arrayMenu[position].equals("Ayuda")){
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.contentframe)).commit();
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    getSupportFragmentManager().beginTransaction().add(R.id.contentframe, new FragmentAyuda()).commit();
                }
                else if(arrayMenu[position].equals("Acerca De")){
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.contentframe)).commit();
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    getSupportFragmentManager().beginTransaction().add(R.id.contentframe, new FragmentAcercaDe()).commit();
                }
            }
        });

        /** ///////////// */

    }

    private void addDrawerItems() {
        arrayMenu=new String[4];
        arrayMenu[0]="Tracking";
        arrayMenu[1]="Configuración";
        arrayMenu[2]="Ayuda";
        arrayMenu[3]="Acerca De";

        //mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayMenu);
        mAdapter = new CustomAdapterMenu(getApplicationContext(), arrayMenu);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                //getSupportActionBar().setLogo(R.drawable.ic_menu_manage);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            System.out.println("app terminada");
            socketConnection.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

