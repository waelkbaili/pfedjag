package com.sohrab.obd.reader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        IntentFilter intentFilterGps = new IntentFilter();
        intentFilterGps.addAction("location_update");
        intentFilterGps.addAction(ACTION_OBD_CONNECTION_STATUS);
        SampleActivity sampleActivity=new SampleActivity();
        //sampleActivity.setModeVeille(true);
        SharedPreferences.Editor editor=context.getSharedPreferences("modePlus",MODE_PRIVATE).edit();
        editor.putString("acces","1");
        editor.apply();
        context.getApplicationContext().registerReceiver(sampleActivity.mGpsReaderReceiver, intentFilterGps);
        context.startService(new Intent(context, ObdReaderService.class));
        context.startService(new Intent(context, GPS_Service.class));
        Log.i("kkkk","from here");
    }
}
