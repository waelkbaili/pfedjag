package com.sohrab.obd.reader;


import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    SharedPreferences sharedPreferencesLogin;
    String userId;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sharedPreferencesLogin = getSharedPreferences("login", MODE_PRIVATE);
        userId = sharedPreferencesLogin.getString("userId", "");


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");
                i.putExtra("longitude", location.getLongitude());
                i.putExtra("latitude", location.getLatitude());
                i.putExtra("user_Id", userId);
                i.putExtra("time", getDateTime());
                sendBroadcast(i);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
        SharedPreferences sharedPreferences=getSharedPreferences("mode", Activity.MODE_PRIVATE);
        String modeAuto=sharedPreferences.getString("choix","0");
        //GlobalClass globalClass = GlobalClass.getInstance();
        /*if (modeAuto.equals("1")) {
            Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
            sendBroadcast(broadcastIntent);
        }*/
    }
    public String recupCin() {
        String cinlog="";
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        String privateDcimDirPath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File file = new File(privateDcimDirPath, "dataUser.txt");
        if (file.exists()) {
            try {
                in = new BufferedReader(new FileReader(file));
                while ((line = in.readLine()) != null) {

                    String[] arrayString = line.split("/");
                    cinlog = arrayString[0];
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cinlog;
    }
    public String getDateTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String datetime = dateformat.format(c.getTime());
        return datetime;
    }
}

