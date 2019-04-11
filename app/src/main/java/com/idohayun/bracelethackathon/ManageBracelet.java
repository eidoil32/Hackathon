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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ManageBracelet extends Fragment {
    private static final String TAG = "ManageBracelet";
    private List<String> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_bracelet,container,false);

        ListView list = view.findViewById(R.id.list_of_parametres);
        GetInformationFromServer updateData = new GetInformationFromServer();
        updateData.getInfo(list,view.getContext());
        Button btnSave = view.findViewById(R.id.btn_save_all_data);
        Button addNew = view.findViewById(R.id.btn_add_new);

        Map<String,String> basicData = new HashMap<>();
        if(getArguments() != null) {
            basicData.put("ID", Objects.requireNonNull(getArguments().getString("ID")));
            basicData.put("FULL_NAME", Objects.requireNonNull(getArguments().getString("FULL_NAME")));
            basicData.put("PHONE_NUMBER", Objects.requireNonNull(getArguments().getString("PHONE_NUMBER")));
        }

        getUserBasicData(view,basicData);

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

    private void getUserBasicData(final View view, Map<String,String> basicData) {
        EditText fullName, phoneNumber;
        TextView ID;

        fullName = view.findViewById(R.id.edit_text_full_name);
        phoneNumber = view.findViewById(R.id.edit_text_phone);
        ID = view.findViewById(R.id.text_id);

        fullName.setText(basicData.get("FULL_NAME"));
        phoneNumber.setText(basicData.get("PHONE_NUMBER"));
        ID.setText(basicData.get("ID"));
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


    public class GetInformationFromServer extends AsyncTask<Void, Void, String> {
        private ListView list;
        private Context context;
        private StringBuilder sb = new StringBuilder();

        public void getInfo(ListView list, Context context) {
            this.list = list;
            this.context = context;
            this.execute();
        }

        private void loadIntoList(String json) throws JSONException {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                data.add(obj.getString(Integer.toString(i)));
            }
            AdapterParam adapter = new AdapterParam(context,R.layout.adapter_paramter,data);
            list.setAdapter(adapter);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (!s.isEmpty()) {
                    loadIntoList(s);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(ServerManager.information);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json);
                    sb.append("\n");
                }
                con.disconnect();
                return sb.toString().trim();
            } catch (
                    Exception e) {
                return null;
            }
        }
    }
}
