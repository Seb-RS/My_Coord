package com.example.mycoord;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    double x, y, x2, y2, xMe, yMe;
    int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        obtenerDato();


    }

    private void obtenerDato() {
        Bundle datos = this.getIntent().getExtras();
        x = Double.parseDouble(datos.getString("x"));
        y = Double.parseDouble(datos.getString("y"));
        type = Integer.parseInt(datos.getString("type"));

        if(type == 2)
        {
            x2 = Double.parseDouble(datos.getString("x2"));
            y2 = Double.parseDouble(datos.getString("y2"));
        }
        else if(type == 3)
        {
            xMe = Double.parseDouble(datos.getString("xuser"));
            yMe = Double.parseDouble(datos.getString("yuser"));
        }
    }
    

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng indicacion = new LatLng(x, y);
        String origen;
        origen = (type==1)? "Tu indicación" : (type==2) ? "Punto 1" : "Destino";
        mMap.addMarker(new MarkerOptions().position(indicacion).title(origen));

        if(type < 3)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indicacion));

        if(type == 2)
        {
            LatLng indicacion2 = new LatLng(x2, y2);
            mMap.addMarker(new MarkerOptions().position(indicacion2).title("Punto 2"));

            String url = getRequestUrl(indicacion, indicacion2);

            TaskResquestDirections taskResquestDirections = new TaskResquestDirections();
            taskResquestDirections.execute(url);
        }
        else if(type == 3)
        {
            LatLng indicacion2 = new LatLng(xMe, yMe);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(indicacion2));
            mMap.addMarker(new MarkerOptions().position(indicacion2).title("Tu ubicación"));

            String url = getRequestUrl(indicacion2, indicacion);

            TaskResquestDirections taskResquestDirections = new TaskResquestDirections();
            taskResquestDirections.execute(url);
        }


    }



    private String getRequestUrl(LatLng origen, LatLng destino) {
        String resultado = "";

        String string_origen = "origin="+origen.latitude+","+origen.longitude;
        String string_destino = "destination="+destino.latitude+","+destino.longitude;

        String sensor = "sensor=false";
        String modo = "mode=driving";

        String param = string_origen+"&"+string_destino+"&"+sensor+"&"+modo;
        String salida = "json";
        String llaveapi = "key=AQUÍ-UNA-API-KEY-CON-FACTURA-PARA-QUE-ESTO-FUNCIONE";

        resultado = "https://maps.googleapis.com/maps/api/directions/"+salida+"?"+param+"&"+llaveapi;

        return resultado;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String linea = "";

            while ((linea = bufferedReader.readLine())!=null){
                stringBuffer.append(linea);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null)
                inputStream.close();

            httpURLConnection.disconnect();
        }

        return responseString;
    }

    public class TaskResquestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";

            try{
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for(List<HashMap<String, String>> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for(HashMap<String, String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if(polylineOptions!= null){
                mMap.addPolyline(polylineOptions);
            } else{
                Toast.makeText(getApplicationContext(), "Dirección no encontrada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
