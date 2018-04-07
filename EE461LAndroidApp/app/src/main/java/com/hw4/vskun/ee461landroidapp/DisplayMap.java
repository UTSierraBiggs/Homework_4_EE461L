package com.hw4.vskun.ee461landroidapp;

/**
 * Created by vskun on 4/3/2018.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.location.Geocoder;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class DisplayMap extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    public List<Double> res = new ArrayList<Double>();
    public String addr = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Log.d("response", "here");
        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String[] msg = message.split(" ");
        for(int i = 0; i < msg.length; i++){


            if (i == msg.length -1 ){
                addr += msg[i];
            } else{
                addr +=  msg[i] + "+";
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        AsyncTask<String,String,String> myTask = new AsyncTask<String,String,String>() {
            ProgressDialog d = new ProgressDialog(DisplayMap.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected String doInBackground(String... strings) {

                String faddr = getaddr();
                addr = faddr;
                URL placeUrl;
                HttpURLConnection connection = null;

                String response = "here";
                try {
                    placeUrl = new URL("https://maps.googleapis.com/maps/api/geocode/json?address="+ faddr + "&key=AIzaSyBpUGDroKoP48EXodcsbbJRq2Z0QJZKA5k");
                    //placeUrl = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyBpUGDroKoP48EXodcsbbJRq2Z0QJZKA5k");
                    //placeUrl = new URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + addr +"&language=pt_BR&key=AIzaSyBpUGDroKoP48EXodcsbbJRq2Z0QJZKA5k");
                    connection = (HttpURLConnection) placeUrl.openConnection();
                    connection.setRequestMethod("GET");


                    int responseCode = connection.getResponseCode();
                    response = Integer.toString(responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        BufferedReader reader = null;

                        InputStream inputStream = connection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            // Nothing to do.

                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String line;
                        while ((line = reader.readLine()) != null) {

                            buffer.append(line + "\n");
                        }

                        if (buffer.length() == 0) {

                        }
                        response = buffer.toString();

                    } else {
                        System.out.print(Integer.toString(responseCode));
                    }
                    Log.d("response", "here");



                } catch (Exception e) {
                    response = (e).toString();

                }
                try{
                    JSONObject j = new JSONObject(response);
                    String lat = ((JSONArray)j.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
                    String lng = ((JSONArray)j.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
                    res.add(Double.valueOf(lat));
                    res.add(Double.valueOf(lng));
                }
                catch (JSONException e){

                }

                return null;
            }
        };// ... your AsyncTask code goes here
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
            myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            myTask.execute();



        while(res.size() == 0){

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(res.get(0), res.get(1));

        // Add a marker in Sydney, Australia, and move the camera.

        mMap.addMarker(new MarkerOptions().position(sydney).title(addr));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // Enable vibrate
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // Disable vibrate
        }
    }

public String getaddr(){
    URL placeUrl;
    HttpURLConnection connection = null;
        String response = "here";
        try {
           // placeUrl = new URL("https://maps.googleapis.com/maps/api/geocode/json?address="+ addr + "&key=AIzaSyBpUGDroKoP48EXodcsbbJRq2Z0QJZKA5k");
            //placeUrl = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyBpUGDroKoP48EXodcsbbJRq2Z0QJZKA5k");
            placeUrl = new URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + addr +"&language=pt_BR&key=AIzaSyBpUGDroKoP48EXodcsbbJRq2Z0QJZKA5k");
            connection = (HttpURLConnection) placeUrl.openConnection();
            connection.setRequestMethod("GET");


            int responseCode = connection.getResponseCode();
            response = Integer.toString(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = null;

                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.

                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                }
                response = buffer.toString();

            } else {
                System.out.print(Integer.toString(responseCode));
            }
            Log.d("response", "here");



        } catch (Exception e) {
            response = (e).toString();

        }
        String addr = "";
        try{
            JSONObject j = new JSONObject(response);
            addr = ((JSONArray)j.get("predictions")).getJSONObject(0).get("description").toString();
        }
        catch (JSONException e){

        }

        return addr;
    }

}

