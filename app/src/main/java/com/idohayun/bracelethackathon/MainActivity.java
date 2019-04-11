package com.idohayun.bracelethackathon;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static int READ = 0;
    private static int WRITE = 1;
    private static int STATE = -1;
    private TextView mainText;
    private EditText userPassword;
    private ManageBracelet manageBracelet = new ManageBracelet();

    private String dataSting;
    NfcManager nfcManager;
    NfcAdapter nfcAdapter;

    public static void setSTATE(int input) {
        STATE = input;
    }

    public static void ErrorToast(Context context) {
        Toast.makeText(context, "wrong password or username", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcManager = new NfcManager();

        final EditText username = findViewById(R.id.login_page_user_name);
        userPassword = findViewById(R.id.login_page_password);
        mainText = findViewById(R.id.login_page_welcome_text);
        final Context context = this;
        final ImageView waiting = findViewById(R.id.image_animation_wating_for_device);
        final DataBaseManager db = new DataBaseManager(context);
        final Button loginBtn = findViewById(R.id.login_page_login_button);

        int i_id = 1;
        String s_username = null, s_password = null, s_premission = null;

        Cursor cursor = db.getData();
        if (cursor.moveToFirst()) {
            i_id = cursor.getInt(0);
            s_username = cursor.getString(1);
            s_password = cursor.getString(2);
            s_premission = cursor.getString(3);
            Tender.Permissions e_permissions;

            assert s_premission != null;
            switch (s_premission) {
                case "volunteer":
                    e_permissions = Tender.Permissions.volunteer;
                    break;
                case "Doctor":
                    e_permissions = Tender.Permissions.Doctor;
                    break;
                case "Magen David Adom":
                    e_permissions = Tender.Permissions.Magen_David_Adom;
                    break;
                default:
                    e_permissions = Tender.Permissions.Doctor;
                    break;
            }

            Tender user = new Tender(i_id, s_username, s_password, e_permissions);
            STATE = 0;
            username.setVisibility(View.INVISIBLE);
            userPassword.setVisibility(View.INVISIBLE);
            loginBtn.setVisibility(View.INVISIBLE);
            mainText.setText(getString(R.string.waiting_for_scanning_bracelet));
            waiting.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "getUserDetails: user doesn't exist!");
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameString = username.getText().toString();
                String userPasswordString = userPassword.getText().toString();

                if (userNameString.isEmpty() || userPasswordString.isEmpty()) {
                    ErrorToast(context);
                } else {
                    JsonObjectRequest request;
                    RequestQueue queue;
                    queue = Volley.newRequestQueue(context);
                    Log.d(TAG, "onClick: update data to server!");
                    Map<String, String> map = new HashMap<>();
                    map.put("UserName", userNameString);
                    map.put("Password", userPasswordString);
                    Log.d(TAG, "onClick: password: " + userPasswordString);
                    final JSONObject jsonObject = new JSONObject(map);
                    request = new JsonObjectRequest(
                            Request.Method.POST, // the request method
                            ServerManager.UserLogin, jsonObject,
                            new Response.Listener<JSONObject>() { // the response listener
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("true")) {
                                            Toast.makeText(context, "success!", Toast.LENGTH_SHORT).show();
                                            String s = response.getString("data");
                                            Log.d(TAG, "onResponse: " + response.getString("data"));
                                            StringBuilder sb = new StringBuilder();
                                            sb.append(s);
                                            int i_id = 1;
                                            String s_username = null, s_password = null, s_premission = null;
                                            JSONArray jsonArray = new JSONArray(s);
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject obj = jsonArray.getJSONObject(i);
                                                s_username = obj.getString("username");
                                                s_password = obj.getString("password");
                                                s_premission = obj.getString("permission");
                                            }
                                            Tender.Permissions e_permissions;

                                            assert s_premission != null;
                                            switch (s_premission) {
                                                case "volunteer":
                                                    e_permissions = Tender.Permissions.volunteer;
                                                    break;
                                                case "Doctor":
                                                    e_permissions = Tender.Permissions.Doctor;
                                                    break;
                                                case "Magen David Adom":
                                                    e_permissions = Tender.Permissions.Magen_David_Adom;
                                                    break;
                                                default:
                                                    e_permissions = Tender.Permissions.Doctor;
                                                    break;
                                            }

                                            Tender user = new Tender(i_id, s_username, s_password, e_permissions);
                                            if (db.addData(user)) {
                                                Log.d(TAG, "onResponse: success");
                                            } else {
                                                Log.d(TAG, "onResponse: failed");
                                            }
                                            STATE = 0;
                                            username.setVisibility(View.INVISIBLE);
                                            userPassword.setVisibility(View.INVISIBLE);
                                            loginBtn.setVisibility(View.INVISIBLE);
                                            mainText.setText(getString(R.string.waiting_for_scanning_bracelet));
                                            waiting.setVisibility(View.VISIBLE);
                                        } else {
                                            if (response.getString("message").equals("user_not_found")) {
                                                Toast.makeText(context, getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
                                            } else if (response.getString("message").equals("password_incorrect")) {
                                                Log.d(TAG, "onResponse: " + response.getString("data"));
                                                Toast.makeText(context, getString(R.string.password_wrong), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() { // the error listener
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                                    Toast.makeText(context, "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                                }
                            });

                    queue.add(request);
                }
            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null || nfcAdapter.isEnabled() == false) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcManager.connect(tag);

        if (tag != null) {
            if (STATE == READ) {
                JSONObject object = nfcManager.read();
                Bundle bundle = new Bundle();
                try {
                    bundle.putString(Const.ID_KEY, object.getString(Const.ID_KEY));
                    bundle.putString(Const.NAME_KEY, object.getString(Const.NAME_KEY));
                    bundle.putString(Const.EMREGNCY_PHONE_KEY, object.getString(Const.EMREGNCY_PHONE_KEY));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!bundle.isEmpty()) {
                    manageBracelet.setArguments(bundle);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_window, manageBracelet, "ManageBracelet").addToBackStack("ManageBracelet").commit();
                }
            } else if (STATE == WRITE) {
                JSONObject object = new JSONObject();
                try {
                    Bundle bundle = manageBracelet.getArguments();
                    if(bundle != null) {
                        Log.d(TAG, "onNewIntent: here!" + bundle.getString(Const.NAME_KEY));
                        object.put(Const.ID_KEY, bundle.getString(Const.ID_KEY));
                        object.put(Const.NAME_KEY, bundle.getString(Const.NAME_KEY));
                        object.put(Const.EMREGNCY_PHONE_KEY, bundle.getString(Const.EMREGNCY_PHONE_KEY));
                        nfcManager.write(object);
                    }
                } catch (Exception e) {
                    mainText.setText(getString(R.string.waiting_for_scanning_bracelet));
                }
            }
        }


    }
}
