package com.idohayun.bracelethackathon;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private  static int READ=0;
    private  static int WRITE =1;
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
            }else if(STATE== WRITE){
               JSONObject object = new JSONObject();
                try {
                    object.put(Const.ID_KEY, "123456789");
                    object.put(Const.NAME_KEY, "edan");
                    object.put(Const.PHONE_KEY, "1234567890");
                    nfcManager.write(object);
                }catch (Exception e){

                }
            }
        }


    }
}
