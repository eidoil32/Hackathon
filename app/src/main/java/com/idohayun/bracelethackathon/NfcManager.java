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
    private  byte[] pass=new byte[]{(byte)0x65 ,(byte)0x64,(byte)0x61,(byte)0x6E};
    private  byte[] pack=new byte[]{(byte)0x55 ,(byte)0x44,(byte)0x33,(byte)0x22};
    private  int ID_MAX_SIZE=12;
    private  int NAME_MAX_SIZE=24+ID_MAX_SIZE;
    private  int PHONE_MAX_SIZE=12+NAME_MAX_SIZE;
    private  byte WRITE=(byte)0xA2;
    private  byte READ=(byte)0x30;
    private  byte FAST_READ=(byte)0x3A;
    private  byte PWD_AUTH=(byte)0xB1;

    byte[] response;
    MifareUltralight mul;
    public void connect(Tag tag){
        mul = MifareUltralight.get(tag);
    }
    public  void  write(JSONObject object){
        try {
            byte[] ID = object.getString(Const.ID_KEY).getBytes();
            byte[] fullName = object.getString(Const.NAME_KEY).getBytes();
            byte[] phone = object.getString(Const.PHONE_KEY).getBytes();
            int idBuffer = ID_MAX_SIZE;
            int nameBuffer=fullName.length+ID_MAX_SIZE;
            int phoneBuffer=phone.length+NAME_MAX_SIZE;
            try {
                mul.connect();
                //protectChip(mul);
                int i = 0;
                for (int j; i < 3; i++) {
                    mul.transceive(new byte[]{WRITE, (byte) (0x04 + i)
                            ,(ID.length>(i*4))?ID[i*4]:0
                            ,(ID.length>(i*4)+1)?ID[(i*4)+1]:0
                            ,(ID.length>(i*4)+2)?ID[(i*4)+2]:0
                            ,(ID.length>(i*4)+3)?ID[(i*4)+3]:0});
                }
                for (int j; i < 9; i++) {
                    mul.transceive(new byte[]{WRITE, (byte) (0x04 + i)
                            ,(nameBuffer>(i*4)  )?fullName[(i*4)  -ID_MAX_SIZE]:0
                            ,(nameBuffer>(i*4)+1)?fullName[(i*4)+1-ID_MAX_SIZE]:0
                            ,(nameBuffer>(i*4)+2)?fullName[(i*4)+2-ID_MAX_SIZE]:0
                            ,(nameBuffer>(i*4)+3)?fullName[(i*4)+3-ID_MAX_SIZE]:0});
                }
                for (int j; i < 12; i++) {
                    mul.transceive(new byte[]{WRITE, (byte) (0x04 + i)
                            ,(phoneBuffer>(i*4)  )?phone[(i*4)  -NAME_MAX_SIZE]:0
                            ,(phoneBuffer>(i*4)+1)?phone[(i*4)+1-NAME_MAX_SIZE]:0
                            ,(phoneBuffer>(i*4)+2)?phone[(i*4)+2-NAME_MAX_SIZE]:0
                            ,(phoneBuffer>(i*4)+3)?phone[(i*4)+3-NAME_MAX_SIZE]:0});                }

            } catch (Exception e) {
                Log.d("NfcManager", "cant connect to nfc exeption is: " + e.getMessage());
            }
        }catch (Exception e) {
            Log.d("NfcManager", "cant get data from Json with error: " + e.getMessage());
        }
    }
//302390542   123456789123456789123456 9384992283
    public JSONObject read(){
        JSONObject dataJson= new JSONObject();
        try {
            mul.connect();
            //protectChip(mul);
            byte[] byteID = mul.transceive(new byte[]{FAST_READ,(byte)0x04,(byte)(0x04+(0x02))});
            byte[] byteFullName = mul.transceive(new byte[]{FAST_READ,(byte)(0x07),(byte)(0x0C)});

            byte[] byteENumber = mul.transceive(new byte[]{FAST_READ,(byte)0x0D,(byte)(0x10)});
            String charID = parseByteToString(byteID);
            String charFN = parseByteToString(byteFullName);
            String charP = parseByteToString(byteENumber);
            dataJson.put(Const.ID_KEY,charID);
            dataJson.put(Const.NAME_KEY,charFN);
            dataJson.put(Const.PHONE_KEY,charP);
            return dataJson;
        }catch (Exception e){
            Log.d("NfcManager","cant connect to nfc exeption is: "+e.getMessage());
        }
        return  dataJson;
    }
    private String parseByteToString(byte[] bytes){
        int i=0;
        String text = new String();
        while(bytes.length>0&&bytes.length>i&&bytes[i]!=0){
            text+= (char)bytes[i++];
        }
        return  text;
    }
    private void protectChip(MifareUltralight mul){
       try {
           response = mul.transceive(new byte[]{READ, 41});
           try {
               if (response[3] == 0xFF) {
                   response = mul.transceive(new byte[]{
                           READ, (byte) 0x2A
                   });
                   if (response != null && response.length >= 16) {
                       boolean prot = false;
                       int authLim = 0;
                       mul.transceive(new byte[]{WRITE, (byte) 0x2A, (byte) ((response[0] & 0x078) | (prot ? 0x080 : 0x000) | (authLim & 0x007)), 0, 0, 0});

                   }

                   response = mul.transceive(new byte[]{READ, (byte) 0x29});
                   if (response != null && response.length >= 16) {
                       int auth0 = 0;
                       mul.transceive(new byte[]{WRITE, (byte) 0x29, response[0], 0, response[2], (byte) (auth0 & 0x0FF)});
                   }
                   mul.transceive(new byte[]{WRITE, (byte) 0x2C, pack[0], pack[1], pack[2], pack[3]});

                   mul.transceive(new byte[]{WRITE, (byte) 0x2A, pass[0], pass[1], pass[2], pass[3]});
               }
           } catch (Exception e) {
               Log.d("NfcManager", "cant protcet nfc error: " + e.getMessage());
           }
       }catch (Exception e){
           Log.d("NfcManager","cant read nfc tag to check protection with error: "+e.getMessage());
       }
    }
}
