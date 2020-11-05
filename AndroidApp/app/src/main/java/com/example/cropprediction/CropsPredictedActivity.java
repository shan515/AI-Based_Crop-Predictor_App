package com.example.cropprediction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CropsPredictedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;
    private ProgressBar loadPredicted;
    private List<com.example.cropprediction.CropDetails> crop_details;

    String PREF_NAME_IOT = "IOT_FRAGMENT";
    String PREF_NAME_PREDICT = "PREDICT_FRAGMENT";
    SharedPreferences saveInstance, farmSaveInstance, locationSaveInstance;
    String crop_image;

    boolean isNewRequest = true;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mGetReference = mDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crops_predicted);

        recyclerView = (RecyclerView) findViewById(R.id.crop_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);

        crop_details = new ArrayList<>();

        //displaying progress dialog while fetching images
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.dismiss();

        saveInstance = getSharedPreferences(PREF_NAME_IOT, MODE_PRIVATE);
        farmSaveInstance = getSharedPreferences(PREF_NAME_PREDICT, MODE_PRIVATE);

        try {

            Toast.makeText(this, "Locn: " + farmSaveInstance.getString("latitude", null) + " " +
                    farmSaveInstance.getString("longitude", null), Toast.LENGTH_SHORT).show();
            Log.i("Tag", "" +farmSaveInstance.getString("latitude", null) + " " +
                    farmSaveInstance.getString("longitude", null));

            callPreprocessor(this,saveInstance.getFloat("pH", -1),
                                saveInstance.getFloat("nit", -1) ,
                                saveInstance.getFloat("pho", -1),
                                saveInstance.getFloat("pot", -1),
                                farmSaveInstance.getFloat("area", -1),
                                farmSaveInstance.getString("string_predict_month",null),
                                farmSaveInstance.getString("string_current_month",null),
                                farmSaveInstance.getString("string_current_crop",null),
                                farmSaveInstance.getString("string_crop_season",null),
                                farmSaveInstance.getString("string_soil_type",null),
                                farmSaveInstance.getString("latitude", null),
                                farmSaveInstance.getString("longitude", null),
                                farmSaveInstance.getBoolean("isCurrent",false),
                                farmSaveInstance.getString("district",""),
                                farmSaveInstance.getString("state",""));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Service unable. Please try again in some time.", Toast.LENGTH_SHORT).show();
            Log.e("CropPredictedActivity", "Error sending http request");
        }
    }

    public void callPreprocessor(Context ctx, float ph, float nit, float pho, float pot, float area, String predict_month, String current_crop_month, String current_planted_crop, String crop_season, String soil_type, String lat, String lng, Boolean isCurrent, String district, String state) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url ="http://152.67.0.149:5000/predict";

        // Post params to be sent to the server
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ph", ph);
        params.put("nitrogen", nit);
        params.put("phosphorous", pho);
        params.put("potassium", pot);
        params.put("area", area);
        params.put("predict_month", predict_month);
        params.put("current_crop_month", current_crop_month);
        params.put("current_planted_crop", current_planted_crop);
        params.put("crop_season", crop_season);
        params.put("lat", lat);
        params.put("lng", lng);
        params.put("soil_type",soil_type);
        params.put("is_current",isCurrent);
        params.put("state",state);
        params.put("district", district);

        JSONArray array = new JSONArray();
        array.put(new JSONObject(params));

        Toast.makeText(ctx, "this:" + array, Toast.LENGTH_SHORT).show();
        Log.i("Tag", "" + array);

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, url, array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        JSONObject getObject = null;
                        Toast.makeText(ctx, "Response rd: " + response, Toast.LENGTH_SHORT).show();

                        try {
                            getObject = response.getJSONObject(0);
                            JSONArray respArray = (JSONArray) getObject.get("predict");
                            isNewRequest = true;
                            crop_details.clear();

                            for (int i = 0; i < respArray.length(); i++){

                                JSONObject crop = respArray.getJSONObject(i);
                                mGetReference.addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(isNewRequest == true){
                                            crop_details.clear();
                                        }
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            isNewRequest = false;
                                            try {
                                                if (crop.getString("crop").equals(child.getKey().toString())) {
                                                    com.example.cropprediction.CropDetails temp = new com.example.cropprediction.CropDetails(crop.getString("crop"), child.child("image").getValue().toString(), crop.getInt("yield_percent"), child.child("season").getValue().toString(), child.child("pdf").getValue().toString(), (float) crop.getDouble("price"));
                                                    crop_details.add(temp);
                                                    updateUI(crop_details);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                       }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.e("Error: ", volleyError);
            }
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                // Add headers
                return headers;
            }

            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                String responseString;
                JSONArray array = new JSONArray();
                if (response != null) {

                    try {
                        responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject obj = new JSONObject(responseString);
                        (array).put(obj);
                    } catch (Exception ex) {
                        Toast.makeText(com.example.cropprediction.CropsPredictedActivity.this, "Exception occured on line 164", Toast.LENGTH_SHORT).show();
                    }
                }
                //return array;
                return Response.success(array, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(req);
    }

    public void updateUI (List<com.example.cropprediction.CropDetails> crop_details){
        adapter = new com.example.cropprediction.CropAdapter(getApplicationContext(), crop_details);
        //adding adapter to recyclerview
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


}


