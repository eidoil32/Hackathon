package com.idohayun.bracelethackathon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ManageBracelet extends Fragment {
    private static final String TAG = "ManageBracelet";
    private List<String> data = new ArrayList<>();
    private Button btnSave, btnBack;
    private ImageView addNew;
    private static ListView list;
    private Context context;
    private Map<String,String> basicData = new HashMap<>();
    private StringBuilder sb = new StringBuilder();
    private EditText editTextFullName, editTextPhoneNumber,textViewID;
    private Patient patient;
    private boolean user_doest_exist = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in");
    }

    public static ListView getListView() {
        return list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_manage_bracelet,container,false);
        view.setBackgroundColor(getResources().getColor(R.color.backgroundWhite,null));
        context = view.getContext();
        list = view.findViewById(R.id.list_of_parametres);
        btnSave = view.findViewById(R.id.btn_save_all_data);
        addNew = view.findViewById(R.id.float_add_btn);
        editTextFullName = view.findViewById(R.id.edit_text_full_name);
        editTextPhoneNumber = view.findViewById(R.id.edit_text_phone);
        textViewID = view.findViewById(R.id.text_id);
        btnBack = view.findViewById(R.id.manage_bracelet_back_btn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patient = null;
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EditDisease.class);
                Bundle bundle = new Bundle();
                ArrayList<String> arrayList;
                if(patient.getDiseases() == null) {
                    arrayList = new ArrayList<>();
                } else {
                    arrayList = new ArrayList<>(patient.getDiseases().size());
                    arrayList.addAll(patient.getDiseases());
                }
                bundle.putSerializable("List",arrayList);
                bundle.putString("ID",patient.getId());
                intent.putExtra("bundle",bundle);

                startActivity(intent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSintax = true;
                if (textViewID.getText().length() > 9) {
                    isSintax=false;
                    textViewID.setError("input is too long");
                }
                if (editTextFullName.getText().length()> 24) {
                    isSintax = false;
                    editTextFullName.setError("input is too long");
                }
                if (editTextPhoneNumber.getText().length() > 10) {
                    isSintax=false;
                    editTextPhoneNumber.setError("input is too long");
                }

                if (textViewID.getText().length() != 0 && editTextFullName.getText().length() != 0 && editTextPhoneNumber.getText().length() != 0) {

                    if (isSintax) {
                        //updateDataToServer(data,getContext());
                        String t_ID, t_NAME, t_PHONE;
                        t_ID = textViewID.getText().toString();
                        t_NAME = editTextFullName.getText().toString();
                        t_PHONE = editTextPhoneNumber.getText().toString();
                        if (everyThingIsOk(t_ID, t_NAME, t_PHONE)) {
                            if (!t_ID.isEmpty() && !t_NAME.isEmpty() && !t_PHONE.isEmpty()) {
                                Bundle bundle = new Bundle();
                                patient.setFullName(t_NAME);
                                patient.setPhone(t_PHONE);
                                patient.setId(t_ID);
                                bundle.putString(Const.ID_KEY, patient.getId());
                                bundle.putString(Const.NAME_KEY, patient.getFullName());
                                bundle.putString(Const.EMREGNCY_PHONE_KEY, patient.getPhone());
                                setArguments(bundle);
                                Log.d(TAG, "onClick: here!");
                                MainActivity.setSTATE(1);

                            if (user_doest_exist) {
                                addNewUserToDB();
                            } else {
                                updateDataToServer(data, getContext());
                            }

                            patient = null;
                            assert getFragmentManager() != null;
                            getFragmentManager().popBackStack();
                            MainActivity.setMainText("Wait for saving data");
                        } else {
                            Log.d(TAG, "onClick: input incorrect!");
                        }
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    //builder.setNeutralButton(R.string.data_is_empty);
                }
            }
        });

        return view;
    }

    private boolean everyThingIsOk(String i_id, String i_name, String i_phone) {
        if(i_id.isEmpty() || i_id.length() != 9) {
            return false;
        }

        return !i_phone.isEmpty() && i_phone.length() == 10;
    }

    private void addNewUserToDB() {
        final JsonObjectRequest request;
        RequestQueue queue;
        queue = Volley.newRequestQueue(context);
        Log.d(TAG, "onClick: update data to server!");
        Map<String,String> map = new HashMap<>();
        map.put("FULLNAME",patient.getFullName());
        map.put("PHONE",patient.getPhone());
        map.put("ID",patient.getId());
        final JSONObject jsonObject = new JSONObject(map);
        request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerManager.AddNewUser, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            if(response.getString("status").equals("true")) {
                                Toast.makeText(context,"success!",Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "onResponse: failed adding new user" + response.getString("data"));
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
                            if(response.getString("user").equals("exist")) {
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
                                AdapterParam datesListAdapter = new AdapterParam(context, R.layout.adapter_paramter, data,Integer.parseInt(patient.getId()));
                                patient.setDiseases(data);
                                list.setVisibility(View.VISIBLE);
                                btnSave.setVisibility(View.VISIBLE);
                                list.setAdapter(datesListAdapter);
                            } else {
                                user_doest_exist = true;
                                Log.d(TAG, "onResponse: " + response.getString("data"));
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(getString(R.string.user_dosent_exist));
                                builder.setCancelable(false);
                                builder.setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        textViewID.setFocusable(true);
                                        textViewID.setFocusableInTouchMode(true);
                                        textViewID.setClickable(true);
                                        textViewID.setText("");
                                        editTextFullName.setText("");
                                        editTextPhoneNumber.setText("");
                                        btnSave.setVisibility(View.VISIBLE);
                                    }
                                });
                                builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        patient = null;
                                        assert getFragmentManager() != null;
                                        getFragmentManager().popBackStack();
                                    }
                                });
                                builder.show();
                                Log.d(TAG, "onResponse: user doesn't exist!");
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
                        Toast.makeText(context, "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getArguments() != null) {
            Log.d(TAG, "onCreateView: " + basicData.size());
            basicData.put(Const.ID_KEY, Objects.requireNonNull(getArguments().getString(Const.ID_KEY)));
            basicData.put(Const.NAME_KEY, Objects.requireNonNull(getArguments().getString(Const.NAME_KEY)));
            basicData.put(Const.EMREGNCY_PHONE_KEY, Objects.requireNonNull(getArguments().getString(Const.EMREGNCY_PHONE_KEY)));
            Log.d(TAG, "onCreateView: " + basicData.get(Const.ID_KEY));
            String b_id = basicData.get(Const.ID_KEY), b_name = basicData.get(Const.NAME_KEY), b_phone = basicData.get(Const.EMREGNCY_PHONE_KEY);
            patient = new Patient(b_name,b_id,b_phone);
            Log.d(TAG, "onCreateView: " + basicData.get(Const.ID_KEY) + basicData.get(Const.NAME_KEY) + basicData.get(Const.EMREGNCY_PHONE_KEY));
            updateListOfData();
            getUserBasicData(basicData);
        }
    }

    private void getUserBasicData(Map<String,String> basicData) {
        editTextFullName.setText(basicData.get(Const.NAME_KEY));
        StringBuilder stringBuilder = new StringBuilder();
        String temp = basicData.get(Const.EMREGNCY_PHONE_KEY);
        if(temp == null) {
            editTextPhoneNumber.setText(temp);
        } else {
            for (int i = 0; i < temp.length() && i < 10; i++) {
                stringBuilder.append(temp.charAt(i));
            }
        }
        editTextPhoneNumber.setText(stringBuilder);
        textViewID.setText(basicData.get(Const.ID_KEY));
    }

    private void updateDataToServer(List<String> data, final Context context) {
        JsonObjectRequest request;
        RequestQueue queue;
        queue = Volley.newRequestQueue(context);
        Log.d(TAG, "onClick: update data to server!");
        Map<String,String> map = new HashMap<>();
        map.put("ID",patient.getId());
        map.put("PHONE",patient.getPhone());
        map.put("FULLNAME",patient.getFullName());
//        for (int i = 0 ; i < data.size(); i++) {
//            map.put("Name",data.get(i));
//        }
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
