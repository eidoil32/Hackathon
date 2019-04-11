package com.idohayun.bracelethackathon;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditDisease extends Activity {
    private static final String TAG = "EditDisease";
    private Patient patient;
    private ListView list;
    public EditDisease() {

    }

    EditDisease (Patient patient, ListView list) {
        this.patient = patient;
        this.list = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_diseases);

        final EditText newDiseasesText = (EditText) findViewById(R.id.newDisease);
        Button saveBtn = findViewById(R.id.done_With_The_Page);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDisease = newDiseasesText.toString();
                boolean isExist = false;
                final List<String> patientList = patient.getDiseases();
                if(patientList != null) {
                    for (int i = 0; i < patientList.size(); i++) {
                        if (newDisease.equals(patientList.get(i))) {
                            Toast.makeText(getApplicationContext(), "disease allready exists", Toast.LENGTH_LONG).show();
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        patientList.add(newDisease);
                        patient.setDiseases(patientList);
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        Map<String, String> map = new HashMap<>();
                        String id = patient.getId();
                        if(id != null) {
                            map.put("ID", id);
                            map.put("Description",newDisease);
                        } else {
                            return;
                        }
                        Log.d(TAG, "onDateSet: " + map.toString());
                        final JSONObject jsonObject = new JSONObject(map);
                        Log.d(TAG, "updateListOfData: jsonObject: " + jsonObject.toString());
                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.POST, // the request method
                                ServerManager.AddDiseaseToUser, jsonObject,
                                new Response.Listener<JSONObject>() { // the response listener
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d(TAG, "onResponse: " + response.toString());
                                        try {
                                            if(response.getString("status").equals("true")) {
                                                AdapterParam datesListAdapter = new AdapterParam(getApplicationContext(), R.layout.adapter_paramter, patientList);
                                                list.setVisibility(View.VISIBLE);
                                                list.setAdapter(datesListAdapter);
                                                finish();
                                            } else {
                                                Log.d(TAG, "onResponse: " + response.getString("data"));
                                                Toast.makeText(getApplicationContext(), "error from server!", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() { // the error listener
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "onErrorResponse: error " + error.toString());
                                        Toast.makeText(getApplicationContext(), "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        queue.add(request);
                    }
                }
            }
        });


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.3));

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.x = 0;
        layoutParams.y = 100;

        getWindow().setAttributes(layoutParams);

    }

}
