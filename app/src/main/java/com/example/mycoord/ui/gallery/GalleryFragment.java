package com.example.mycoord.ui.gallery;

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

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    EditText x, y, x2, y2;
    double latUsuario, lonUsuario;
    Button aceptar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });



        x = (EditText) root.findViewById(R.id.editTextXpoint1);
        y = (EditText) root.findViewById(R.id.editTextYpoint1);
        x2 = (EditText) root.findViewById(R.id.editTextXpoint2);
        y2 = (EditText) root.findViewById(R.id.editTextYpoint2);
        aceptar = (Button) root.findViewById(R.id.boton);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordX = x.getText().toString();
                String coordY = y.getText().toString();
                String coordX2 = x2.getText().toString();
                String coordY2 = y2.getText().toString();
                String type = "2";

                try {
                    Double.parseDouble(coordX);
                    Double.parseDouble(coordY);

                    Intent i = new Intent(getActivity(), MapsActivity.class);
                    i.putExtra("x", coordX);
                    i.putExtra("y", coordY);
                    i.putExtra("x2", coordX2);
                    i.putExtra("y2", coordY2);
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
