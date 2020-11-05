package com.example.cropprediction.ui.predict;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.cropprediction.R;

import static android.content.Context.MODE_PRIVATE;

public class LocationFragment extends Fragment {
//
//    Button goNext;
//
//    Spinner state, district;
//
//    String PREF_NAME = "LOCATION_FRAGMENT";
//    SharedPreferences saveInstance;

    public LocationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        goNext = view.findViewById(R.id.button_next_page);
//        state = view.findViewById(R.id.spinner_state);
//        district = view.findViewById(R.id.spinner_district);
//
//        saveInstance = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        state.setSelection(saveInstance.getInt("state", 0));
//        district.setSelection(saveInstance.getInt("district", 0));
//
//        goNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences.Editor editor = saveInstance.edit();
//                editor.putInt("state", state.getSelectedItemPosition());
//                editor.putInt("district", district.getSelectedItemPosition());
//                editor.putString("string_state", String.valueOf(state.getItemAtPosition(state.getSelectedItemPosition())));
//                editor.putString("string_district", String.valueOf(district.getItemAtPosition(district.getSelectedItemPosition())));
//                editor.commit();
//
//                PredictFragment myFragment = new PredictFragment();
//                myFragment.setArguments(getActivity().getIntent().getExtras());
//                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup)getView().getParent()).getId(), myFragment).addToBackStack(null).commit();
//            }
//        });
//
//        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                String selectedState = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getActivity().getApplicationContext(), "selected state: " + selectedState, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


    }
}