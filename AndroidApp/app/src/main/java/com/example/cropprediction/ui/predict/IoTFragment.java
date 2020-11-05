package com.example.cropprediction.ui.predict;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cropprediction.R;
import com.example.cropprediction.CropsPredictedActivity;

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class IoTFragment extends Fragment {

    Button goBack;
    Button search, fetch_data;
    EditText pH, nit, pot, pho;

    String savedPH, savedNit, savedPot, savedPho;

    String PREF_NAME = "IOT_FRAGMENT";
    SharedPreferences saveInstance;

    public IoTFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goBack = view.findViewById(R.id.button_prev_page);
        search = view.findViewById(R.id.button_predict);
        pH = view.findViewById(R.id.iot_ph);
        nit = view.findViewById(R.id.iot_nitrogen);
        pho = view.findViewById(R.id.iot_phos);
        pot = view.findViewById(R.id.iot_potassium);

        saveInstance = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        savedPH = Float.toString(saveInstance.getFloat("pH", 7));
        savedNit = Float.toString(saveInstance.getFloat("nit", 20));
        savedPho = Float.toString(saveInstance.getFloat("pho", 15));
        savedPot = Float.toString(saveInstance.getFloat("pot", 8));

        fetch_data = view.findViewById(R.id.button_get_data);
        pH.setText(savedPH);
        nit.setText(savedNit);
        pho.setText(savedPho);
        pot.setText(savedPot);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = saveInstance.edit();
                editor.putFloat("pH",Float.parseFloat(pH.getText().toString()));
                editor.putFloat("nit",Float.parseFloat(nit.getText().toString()));
                editor.putFloat("pho",Float.parseFloat(pho.getText().toString()));
                editor.putFloat("pot",Float.parseFloat(pot.getText().toString()));
                editor.commit();

                PredictFragment myFragment = new PredictFragment();
                myFragment.setArguments(getActivity().getIntent().getExtras());
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup)getView().getParent()).getId(), myFragment).addToBackStack(null).commit();
            }
        });

        fetch_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String random = getRandomValue(5, 10, 1);
                pH.setText(random);
                random = getRandomValue(5, 90, 0);
                nit.setText(random);
                random = getRandomValue(8, 25, 0);
                pho.setText(random);
                random = getRandomValue(0, 25, 0);
                pot.setText(random);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = saveInstance.edit();
                editor.putFloat("pH",Float.parseFloat(pH.getText().toString()));
                editor.putFloat("nit",Float.parseFloat(nit.getText().toString()));
                editor.putFloat("pho",Float.parseFloat(pho.getText().toString()));
                editor.putFloat("pot",Float.parseFloat(pot.getText().toString()));
                editor.commit();

                Intent letstart = new Intent(getActivity().getApplicationContext(), CropsPredictedActivity.class);
                startActivity(letstart);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView():
                inflater.inflate(R.layout.sensor_input_layout, container, false);
        return view;
    }

    public static String getRandomValue(final int lowerBound,
                                        final int upperBound,
                                        final int decimalPlaces){

        Random random = new Random();

        if(lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0){
            throw new IllegalArgumentException("Put error message here");
        }

        final double dbl =
                ((random == null ? new Random() : random).nextDouble() //
                        * (upperBound - lowerBound))
                        + lowerBound;
        return String.format("%." + decimalPlaces + "f", dbl);
    }
}