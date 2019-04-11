package com.idohayun.bracelethackathon;

import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.util.Log;
import android.util.Printer;

import org.json.JSONObject;

public class NfcManager {
    private  byte[] PASS=new byte[]{(byte)0x65 ,(byte)0x64,(byte)0x61,(byte)0x6E};
    private  byte[] pack=new byte[]{(byte)0x55 ,(byte)0x44,(byte)0x33,(byte)0x22};
    private  byte WRITE=(byte)0xA2;
    private  byte READ=(byte)0x30;
    private  byte FAST_READ=(byte)0x3A;
    private  byte PWD_AUTH=(byte)0xB1;

    byte[] response;
    MifareUltralight mul;
    public void connect(Tag tag){
        mul = MifareUltralight.get(tag);
    }

    public JSONObject read(){
        JSONObject dataJson= new JSONObject();
        try {
            mul.connect();

            response= mul.transceive(new byte[]{
                     READ,(byte)0x2A
            });
            if(response!=null&&response.length>=16){
                boolean prot = false;
                int authLim = 0;
                mul.transceive(new byte[]{WRITE,(byte)0x2A,(byte)((response[0]&0x078)| (prot?0x080:0x000)|(authLim &0x007)),0,0,0});

            }

            response = mul.transceive(new byte[]{READ,(byte)0x29});
            if(response!=null&&response.length>=16){
                int auth0 =0;
                mul.transceive(new byte[]{WRITE,(byte)0x29,response[0],0,response[2],(byte)(auth0&0x0FF)});
            }
            mul.transceive(new byte[]{WRITE,(byte)0x2C,pack[0],pack[1],pack[2],pack[3]});
        }catch (Exception e){
            Log.d("NfcManager","cant connect to nfc exeption is:"+e.getMessage());
        }
        return  dataJson;
    }


}
