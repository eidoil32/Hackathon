package com.idohayun.bracelethackathon;

public class MainActivity {

    int encPrime0=541,encPrime1=523,encPrime2=521;
    byte[] encriptor(String data)
    {
        byte[] dataByte = data.getBytes();
        int priS = encPrime0*encPrime1*encPrime2;
        if(dataByte.length<140){
            for(int i=0;i<dataByte.length;i++){
                dataByte[i]+=((priS/10 - ((i%10) +1)) % 10);
            }
        }
        return dataByte;
    }
    String descriptor(byte[] data){
        int priS = encPrime0*encPrime1*encPrime2;
        if(data.length<140) {
            for(int i=0;i<data.length;i++)
            {
                data[i] -= (priS/10*((i%10)+1))%10;
            }
        }
        String stringData = new String(data);
        return stringData;
    }
}
