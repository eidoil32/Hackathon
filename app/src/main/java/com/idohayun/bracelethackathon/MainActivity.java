package com.idohayun.bracelethackathon;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private  static int READ=0;
    private  static int SAVE=1;
    private  int STATE=0;

    private String dataSting;
    NfcManager nfcManager;
    NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcManager = new NfcManager();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null||nfcAdapter.isEnabled()==false) {


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag =intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcManager.connect(tag);

        if(tag!=null) {
            if (STATE == READ) {
                nfcManager.read();
            }else if(STATE==SAVE){

            }
        }


    }
}
