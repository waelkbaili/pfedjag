package com.sohrab.obd.reader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class Parametres extends MyActivity {

    String modeDark;
    String userId;
    //private CheckBox chkObd, chkGps;
    String gps,obd;
    TextView language,data;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocal();
        SharedPreferences sharedPreferencess=getSharedPreferences("modeDark", Activity.MODE_PRIVATE);
        modeDark=sharedPreferencess.getString("choixDark","0");
        if(modeDark.equals("1")){
            setTheme(R.style.DarkTheme); }
        if(modeDark.equals("0")){
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_parametres);
        Switch swDark = (Switch) findViewById(R.id.switch2);
        language=findViewById(R.id.langue);
        data=findViewById(R.id.data);
        userId = getIntent().getStringExtra("userId");
        toolbar=findViewById(R.id.myToolBarBack);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.parametre));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SampleActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
                finish();
            }
        });

        if (modeDark.equals("0")){
            swDark.setChecked(false);
        }
        else{
            swDark.setChecked(true);
        }

        swDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SharedPreferences.Editor editor=getSharedPreferences("modeDark",MODE_PRIVATE).edit();
                    editor.putString("choixDark","1");
                    editor.apply();
                    recreate();
                }
                else{
                    SharedPreferences.Editor editor=getSharedPreferences("modeDark",MODE_PRIVATE).edit();
                    editor.putString("choixDark","0");
                    editor.apply();
                    recreate();
                }
            }
        });


        SharedPreferences sharedPreferencesParam=getSharedPreferences("param", Activity.MODE_PRIVATE);
        gps=sharedPreferencesParam.getString("gps","1");
        obd=sharedPreferencesParam.getString("obd","0");

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recupData();
            }
        });
    }

    private void showChangeLanguageDialog() {
        View ButtonView = View.inflate(this, R.layout.threebutton, null);
        Button en = (Button) ButtonView.findViewById(R.id.button_en);
        Button fr = (Button) ButtonView.findViewById(R.id.button_fr);
        Button ar = (Button) ButtonView.findViewById(R.id.button_ar);

        en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en");
                recreate();
            }
        });

        fr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("fr");
                recreate();
            }
        });

        ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ar");
                recreate();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        builder.setView(ButtonView).show();
    }

    public void recupData(){
        View checkBoxView = View.inflate(this, R.layout.checkbox, null);
        final CheckBox checkBoxOBD = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        final CheckBox checkBoxGPS = (CheckBox) checkBoxView.findViewById(R.id.checkbox2);
        if(gps.equals("1"))
            checkBoxGPS.setChecked(true);
        else
            checkBoxGPS.setChecked(false);

        if(obd.equals("1"))
            checkBoxOBD.setChecked(true);
        else
            checkBoxOBD.setChecked(false);

        checkBoxGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBoxOBD.isChecked()==false)
                    checkBoxGPS.setChecked(true);
                if(checkBoxGPS.isChecked()){
                    SharedPreferences.Editor editorAcces=getSharedPreferences("param",MODE_PRIVATE).edit();
                    editorAcces.putString("gps","1");
                    editorAcces.apply();
                }
                else{
                    SharedPreferences.Editor editorAcces=getSharedPreferences("param",MODE_PRIVATE).edit();
                    editorAcces.putString("gps","0");
                    editorAcces.apply();
                }
            }
        });
        checkBoxOBD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkBoxGPS.isChecked()==false)
                    checkBoxOBD.setChecked(true);
                if(checkBoxOBD.isChecked()){
                    SharedPreferences.Editor editorAcces=getSharedPreferences("param",MODE_PRIVATE).edit();
                    editorAcces.putString("obd","1");
                    editorAcces.apply();
                }
                else{
                    SharedPreferences.Editor editorAcces=getSharedPreferences("param",MODE_PRIVATE).edit();
                    editorAcces.putString("obd","0");
                    editorAcces.apply();
                }
            }
        });

        checkBoxGPS.setText("GPS");
        checkBoxOBD.setText("OBD");


        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        builder.setView(checkBoxView).show();
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

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(),SampleActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
        finish();
    }


}
