package com.idohayun.bracelethackathon;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ManageBracelet extends Fragment {
    private static final String TAG = "ManageBracelet";
    private List<String> data = new ArrayList<>();
    private Button btnSave, addNew;
    private ListView list;
    private Context context;
    private Map<String,String> basicData = new HashMap<>();
    private StringBuilder sb = new StringBuilder();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_bracelet,container,false);
        view.setBackgroundColor(getResources().getColor(R.color.backgroundWhite,null));
        context = view.getContext();
        list = view.findViewById(R.id.list_of_parametres);
        btnSave = view.findViewById(R.id.btn_save_all_data);
        addNew = view.findViewById(R.id.btn_add_new);

        if(getArguments() != null) {
            basicData.put(Const.ID_KEY, Objects.requireNonNull(getArguments().getString(Const.ID_KEY)));
            basicData.put(Const.NAME_KEY, Objects.requireNonNull(getArguments().getString(Const.NAME_KEY)));
            basicData.put(Const.EMREGNCY_PHONE_KEY, Objects.requireNonNull(getArguments().getString(Const.EMREGNCY_PHONE_KEY)));
            Log.d(TAG, "onCreateView: " + basicData.get(Const.ID_KEY) + basicData.get(Const.NAME_KEY) + basicData.get(Const.EMREGNCY_PHONE_KEY));
            updateListOfData();
            getUserBasicData(view,basicData);
        }

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add new disease");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataToServer(data,getContext());
            }
        });

        return view;
    }

    private void updateListOfData() {
        RequestQueue queue = Volley.newRequestQueue(context);
        Map<String, String> map = new HashMap<>();
        String id = basicData.get(Const.ID_KEY);
        if(id != null) {
            map.put("ID", id);
        } else {
            return;
        }
        Log.d(TAG, "onDateSet: " + map.toString());
        final JSONObject jsonObject = new JSONObject(map);
        Log.d(TAG, "updateListOfData: jsonObject: " + jsonObject.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerManager.information, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        try {
                            data.clear();
                            String s = response.getString("data");
                            Log.d(TAG, "onResponse: " + s);
                            JSONArray jsonArray = new JSONArray(s);
                            Log.d(TAG, "onResponse: " + jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                data.add(obj.getString("Name"));
                                Log.d(TAG, "onResponse: " + obj.toString());
                            }
                            AdapterParam datesListAdapter = new AdapterParam(context, R.layout.adapter_paramter, data);
                            list.setVisibility(View.VISIBLE);
                            list.setAdapter(datesListAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: error " + error.toString());
                        Toast.makeText(context, "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(request);
    }

    private void getUserBasicData(final View view, Map<String,String> basicData) {
        EditText fullName, phoneNumber;
        TextView ID;

        fullName = view.findViewById(R.id.edit_text_full_name);
        phoneNumber = view.findViewById(R.id.edit_text_phone);
        ID = view.findViewById(R.id.text_id);

        fullName.setText(basicData.get(Const.NAME_KEY));
        phoneNumber.setText(basicData.get(Const.EMREGNCY_PHONE_KEY));
        ID.setText(basicData.get(Const.ID_KEY));
    }

    private void updateDataToServer(List<String> data, final Context context) {
        JsonObjectRequest request;
        RequestQueue queue;
        queue = Volley.newRequestQueue(context);
        Log.d(TAG, "onClick: update data to server!");
        Map<String,String> map = new HashMap<>();
        for (int i = 0 ; i < data.size(); i++) {
            map.put(Integer.toString(i),data.get(0));
        }
        final JSONObject jsonObject = new JSONObject(map);
        request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerManager.UpdatePersonData, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            if(response.getString("status").equals("true")) {
                                Toast.makeText(context,"success!",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Oops! Got error from server!",Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }


}