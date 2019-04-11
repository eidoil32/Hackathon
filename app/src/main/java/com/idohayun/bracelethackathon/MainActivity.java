package com.idohayun.bracelethackathon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static int READ = 0;
    private static int SAVE = 1;
    private int STATE = -1;

    private String dataSting;
    NfcManager nfcManager;
    NfcAdapter nfcAdapter;

    public static void ErrorToast(Context context) {
        Toast.makeText(context, "wrong password or username", Toast.LENGTH_LONG).show();
    }

 //my name is haim
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
