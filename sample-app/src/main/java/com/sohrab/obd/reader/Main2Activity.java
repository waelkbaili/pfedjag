package com.sohrab.obd.reader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferencess=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        final String language=sharedPreferencess.getString("My_Lang","0");
        setContentView(R.layout.activity_main2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(language.equals("0")){
                    showChangeLanguageDialog();
                }
                else{
                    loadLocal();
                    Intent intent=new Intent(Main2Activity.this,login.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 500);
    }
    private void showChangeLanguageDialog() {
        final String[] listItem={"française","english","عربى"};
        AlertDialog.Builder builde=new AlertDialog.Builder(Main2Activity.this);
        builde.setTitle("choose language....");
        builde.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    setLocale("fr");
                    recreate();
                }
                if(which==1){
                    setLocale("en");
                    recreate();
                }
                if(which==2){
                    setLocale("ar");
                    recreate();
                }

                dialog.dismiss();
            }
        });

        AlertDialog alertDialog=builde.create();
        alertDialog.show();
    }

    private void setLocale(String en) {

        Locale locale=new Locale(en);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",en);
        editor.apply();
    }

    public void loadLocal(){
        SharedPreferences sharedPreferences=getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language=sharedPreferences.getString("My_Lang","");
        setLocale(language);
    }


}
