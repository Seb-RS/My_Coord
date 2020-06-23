package com.example.mycoord.ui.slideshow;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mycoord.MainActivity;
import com.example.mycoord.MapsActivity;
import com.example.mycoord.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    EditText x, y;
    double latUsuario, lonUsuario;
    Button aceptar;
    Boolean Estado = true;
    TextView mensaje1, mensaje2;
    MainActivity mainActivity;
    int count = 1, cc;
    Handler handler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        x = (EditText) root.findViewById(R.id.editTextX);
        y = (EditText) root.findViewById(R.id.editTextY);
        x.setEnabled(false);
        y.setEnabled(false);
        this.mensaje1 = (TextView) root.findViewById(R.id.mensaje1);
        this.mensaje2 = (TextView) root.findViewById(R.id.mensaje2);
        mensaje1.setText("Obteniendo su ubicación, por favor espere.");
        mensaje2.setText("");

        aceptar = (Button) root.findViewById(R.id.boton);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    getLocation(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }



        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Estado) {
                    String coordX = x.getText().toString();
                    String coordY = y.getText().toString();
                    String coordUsuarioX = String.valueOf(latUsuario);
                    String coordUsuarioY = String.valueOf(lonUsuario);
                    String type = "3";

                    try {
                        Double.parseDouble(coordX);
                        Double.parseDouble(coordY);
                        Double.parseDouble(coordUsuarioX);
                        Double.parseDouble(coordUsuarioY);


                        Intent i = new Intent(getActivity(), MapsActivity.class);
                        i.putExtra("x", coordX);
                        i.putExtra("y", coordY);
                        i.putExtra("xuser", coordUsuarioX);
                        i.putExtra("yuser", coordUsuarioY);
                        i.putExtra("type", type);
                        startActivity(i);

                    } catch (Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Coordenada incorrecta", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        return root;
    }

    public void getLocation(Location loc) {
        latUsuario = loc.getLatitude();
        lonUsuario = loc.getLongitude();


        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            Estado = false;
            x.setEnabled(true);
            y.setEnabled(true);
            if (count > 0) {
                mensaje1.setText("¡Localizado! LAT: " + latUsuario + " | LON: " + lonUsuario);
                count--;
            }

            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    //mensaje1.setText("Localización exitosa.");
                    mensaje2.setText("Tu dirección es: \n"
                            + DirCalle.getAddressLine(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }



        }
    }

}
