package com.idohayun.bracelethackathon;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ManageBracelet extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_bracelet,container,false);

        ListView list = view.findViewById(R.id.list_of_parametres);
        GetInformationFromServer updateData = new GetInformationFromServer();
        updateData.getInfo(list,view.getContext());

        Button btnSave = view.findViewById(R.id.btn_save_all_data);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
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
            List<String> data_list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj =jsonArray.getJSONObject(i);
                data_list.add(obj.getString("Description"));
            }

            AdapterParam adapter = new AdapterParam(context,R.layout.adapter_paramter,data_list);
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
