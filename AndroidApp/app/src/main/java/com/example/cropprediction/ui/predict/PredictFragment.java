package com.example.cropprediction.ui.predict;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.cropprediction.R;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.sql.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class PredictFragment extends Fragment implements LocationListener {

    EditText farm_area;
    Button next, lang_switch;
    TextView location, label_current_planted_crop;;
    Spinner current_planted_crop, month_crop_predict, season_to_predict_crop, soil_type;

    String PREF_NAME = "PREDICT_FRAGMENT";
    SharedPreferences saveInstance;

    String area;
    int spinner_predict_month, spinner_crop_season, spinner_current_crop, spinner_soil_type;

    private LocationManager mLocationManager;
    private static final String TAG = "LocationFragment";
    Double latitude,longitude;
    String district = "";
    String state = "";

    Boolean isCurrent = false;
    Boolean firstTime = true;

    int[] kharif_months = {6,7,8,9};
    int[] rabi_months = {10,11,0,1};
    int[] zaid_months = {2,3,4,5};

    public PredictFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);super.onAttach(context);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "ERROR");
            ActivityCompat.requestPermissions(getActivity(), new
                    String[]{Manifest.permission.ACCESS_FINE_LOCATION},22);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_predict, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentMonth = calendar.get(Calendar.MONTH);

        month_crop_predict = view.findViewById(R.id.spinner_month_to_predict_crop);
        label_current_planted_crop = view.findViewById(R.id.label_current_crop);
        current_planted_crop = view.findViewById(R.id.spinner_current_crop);
        season_to_predict_crop = view.findViewById(R.id.spinner_season_to_predict_crop);
        soil_type = view.findViewById(R.id.spinner_soil_type);
        farm_area = view.findViewById(R.id.edit_farm_area);
        location = view.findViewById(R.id.label_location);
        lang_switch = view.findViewById(R.id.button_language_switch);

        next = view.findViewById(R.id.button_next_page_1);

        if (firstTime == true){
            month_crop_predict.setSelection(currentMonth);
        }
        else{
            month_crop_predict.setSelection(spinner_predict_month);
        }

        saveInstance = getActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        area = Float.toString(saveInstance.getFloat("area", 10));
        spinner_predict_month = saveInstance.getInt("predict_month",currentMonth);
        spinner_current_crop = saveInstance.getInt("current_crop",0);
        spinner_crop_season = saveInstance.getInt("crop_season",0);
        spinner_soil_type = saveInstance.getInt("soil_type", 0);

        current_planted_crop.setSelection(spinner_current_crop);
        season_to_predict_crop.setSelection(spinner_crop_season);
        soil_type.setSelection(spinner_soil_type);
        farm_area.setText(area);
        location.setText(saveInstance.getString("district", "") + "," + saveInstance.getString("state", ""));

        month_crop_predict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == currentMonth){
                    isCurrent = true;
                    label_current_planted_crop.setVisibility(View.GONE);
                    current_planted_crop.setVisibility(view.GONE);
                }
                else{
                    label_current_planted_crop.setVisibility(View.VISIBLE);
                    current_planted_crop.setVisibility(view.VISIBLE);
                }
                setSeasonSpinner(position);
        }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        season_to_predict_crop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    if (ArrayUtils.contains(kharif_months, position)) {
                        month_crop_predict.setSelection(currentMonth);
                    }
                    else{
                        month_crop_predict.setSelection(kharif_months[0]);
                    }
                }
                else if (position == 1){
                    if (ArrayUtils.contains(rabi_months, position)) {
                        month_crop_predict.setSelection(currentMonth);
                    }
                    else{
                        month_crop_predict.setSelection(rabi_months[0]);
                    }
                }
                else{
                    if (ArrayUtils.contains(zaid_months, position)) {
                        month_crop_predict.setSelection(currentMonth);
                    }
                    else{
                        month_crop_predict.setSelection(zaid_months[0]);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lang_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PredictFragmentHindi myFragment = new PredictFragmentHindi();
                myFragment.setArguments(getActivity().getIntent().getExtras());
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup)getView().getParent()).getId(), myFragment).addToBackStack(null).commit();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndCommit();
                IoTFragment myFragment = new IoTFragment();
                myFragment.setArguments(getActivity().getIntent().getExtras());
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup)getView().getParent()).getId(), myFragment).addToBackStack(null).commit();
            }
        });
    }

    public void setSeasonSpinner(int position){
        if(ArrayUtils.contains(kharif_months, position)){
            season_to_predict_crop.setSelection(0);
        }else if(ArrayUtils.contains(rabi_months, position)){
            season_to_predict_crop.setSelection(1);
        }else{
            season_to_predict_crop.setSelection(2);
        }
    }

    public void saveAndCommit(){
        SharedPreferences.Editor editor = saveInstance.edit();
        editor.putFloat("area",Float.parseFloat(farm_area.getText().toString()));
        editor.putInt("predict_month",month_crop_predict.getSelectedItemPosition());
        editor.putInt("current_crop",current_planted_crop.getSelectedItemPosition());
        editor.putInt("crop_season",season_to_predict_crop.getSelectedItemPosition());
        editor.putBoolean("isCurrent", isCurrent);
        editor.putInt("soil_type", soil_type.getSelectedItemPosition());
        editor.putString("string_predict_month", String.valueOf(month_crop_predict.getItemAtPosition(month_crop_predict.getSelectedItemPosition())));
        editor.putString("string_current_crop", String.valueOf(current_planted_crop.getItemAtPosition(current_planted_crop.getSelectedItemPosition())));
        editor.putString("string_crop_season", String.valueOf(season_to_predict_crop.getItemAtPosition(season_to_predict_crop.getSelectedItemPosition())));
        editor.putString("string_soil_type", String.valueOf(soil_type.getItemAtPosition(soil_type.getSelectedItemPosition())));
        editor.commit();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, String.valueOf(location.getLatitude()));
        Log.i(TAG, String.valueOf(location.getLongitude()));

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            district = addresses.get(0).getSubAdminArea();
            if (district == null){
                addresses.get(0).getLocality();
            }
            state = addresses.get(0).getAdminArea();
            Log.i(TAG, district);
            Log.i(TAG, state);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = saveInstance.edit();
        editor.putString("state",state);
        editor.putString("district",district);
        editor.putString("latitude",latitude.toString());
        editor.putString("longitude",longitude.toString());
        editor.commit();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "Provider " + provider + " has now status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}