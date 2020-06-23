package com.example.mycoord.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mycoord.MapsActivity;
import com.example.mycoord.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    EditText x, y;
    double latUsuario, lonUsuario;
    Button aceptar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        
        x = (EditText) root.findViewById(R.id.editTextX);
        y = (EditText) root.findViewById(R.id.editTextY);

        aceptar = (Button) root.findViewById(R.id.boton);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordX = x.getText().toString();
                String coordY = y.getText().toString();
                String type = "1";

                try {
                    Double.parseDouble(coordX);
                    Double.parseDouble(coordY);

                    Intent i = new Intent(getActivity(), MapsActivity.class);
                    i.putExtra("x", coordX);
                    i.putExtra("y", coordY);
                    i.putExtra("type", type);

                    startActivity(i);


                }catch(Exception ex)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Coordenada incorrecta", Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }
}
