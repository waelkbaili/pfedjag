package com.sohrab.obd.reader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class login extends MyActivity {

    private static final String TAG = "CustomAccessibility";

    Button _btnLog,register;
    EditText _txtUserCin, _txtPassword;
    String userId;
    ProgressBar simpleProgressBar;
    AlertDialog.Builder builder;
    SharedPreferences sharedPreferencesLogin;
    boolean conexion;
    ConstraintLayout first,second,progressLayout;
    String color;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferencess=getSharedPreferences("modeDark", Activity.MODE_PRIVATE);
        String modeDark=sharedPreferencess.getString("choixDark","0");
        if(modeDark.equals("1")){
            setTheme(R.style.DarkTheme);
            color="#FFFFFF";
        }
        if(modeDark.equals("0")){
            setTheme(R.style.AppTheme);
            color="#000000";
        }
        //loadLanguage();
        setContentView(R.layout.activity_login);
        first=findViewById(R.id.first_back);
        second=findViewById(R.id.second_back);
        if(modeDark.equals("1")){
            first.setBackgroundResource(R.drawable.first_back_dark);
            second.setBackgroundResource(R.drawable.second_back_dark);
        }
        if(modeDark.equals("0")){
            first.setBackgroundResource(R.drawable.first_back_light);
            second.setBackgroundResource(R.drawable.second_back_light);
        }
        progressLayout=findViewById(R.id.progresslayout);
        _btnLog = findViewById(R.id.btnLog);
        _txtUserCin = findViewById(R.id.txtUserCin);
        _txtPassword = findViewById(R.id.txtPassword);
        register = findViewById(R.id.register);
        simpleProgressBar = findViewById(R.id.progLog);
        simpleProgressBar.setVisibility(View.INVISIBLE);
        builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        sharedPreferencesLogin = getSharedPreferences("login", MODE_PRIVATE);
        if(! isAccessServiceEnabled(getApplicationContext(),CustomAccessibilityService.class))
            if(modeDark.equals("1"))
                 showAlertProfil(getResources().getString(R.string.accessibility_service_description),getResources().getString(R.string.auth));
            else
                showAlertProfil(getResources().getString(R.string.accessibility_service_description),getResources().getString(R.string.auth));

        userId = sharedPreferencesLogin.getString("userId", "");
        if(!userId.equals("")){
            Intent i = new Intent(login.this, SampleActivity.class);
            i.putExtra("userId", userId+"");
            startActivity(i);
            finish();
        }

        _btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline())
                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                else {
                    Intent i = new Intent(login.this, DriveWin.class);
                    startActivity(i);
                }
            }
        });
        runtime_permissions();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                runtime_permissions();
                break;
            default:
                break;
        }
    }

    void runtime_permissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , 10);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String packageName = getApplicationContext().getPackageName();
                PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("package:" + packageName));
                    getApplicationContext().startActivity(intent);
                }
            }

            return;
        }
    }

    public void loadLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString("My_Lang", "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

    }

    public void showAlertProfil(String text, String title) {
        //Setting message manually and performing action on button click
        builder.setMessage(Html.fromHtml("<font color="+color+">"+text+"</font>")).
                setCancelable(false).setPositiveButton(Html.fromHtml("<font color=#4D7D8D>"+
                getResources().getString(R.string.valider)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+title+"</font>"));
        alert.show();
    }

    public boolean isAccessServiceEnabled(Context context, Class accessibilityServiceClass)
    {
        String prefString = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

        return prefString!= null && prefString.contains(context.getPackageName() + "/" + accessibilityServiceClass.getName());
    }
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public void tryAgain(){
        //Setting message manually and performing action on button click
        builder.setMessage(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.requested_data_could_not_be_loaded)+"</font>")).
                setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#4D7D8D'>"+
                getResources().getString(R.string.try_again)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                login();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
    }

    public void login(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //progressLayout.setVisibility(View.VISIBLE);
        /*_txtPassword.setEnabled(false);
        _txtUserCin.setEnabled(false);
        _btnLog.setEnabled(false);
        register.setEnabled(false);*/
        simpleProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                conexion=isOnline();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!conexion){
                            NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                            simpleProgressBar.setVisibility(View.INVISIBLE);
                            /*_txtPassword.setEnabled(true);
                            _txtUserCin.setEnabled(true);
                            _btnLog.setEnabled(true);
                            register.setEnabled(true);*/
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            //progressLayout.setVisibility(View.INVISIBLE);
                        }
                        else {
                            String email = _txtUserCin.getText().toString();
                            String pass_word = _txtPassword.getText().toString();
                            if (email.equals("") || pass_word.equals("")) {
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.please), Toast.LENGTH_LONG).show();
                                /*_txtPassword.setEnabled(true);
                                _txtUserCin.setEnabled(true);
                                _btnLog.setEnabled(true);
                                register.setEnabled(true);*/
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                //progressLayout.setVisibility(View.INVISIBLE);
                            } else {
                                JSONObject params=new JSONObject();
                                try {
                                    params.put("email",email);
                                    params.put("password",pass_word);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String data=params.toString();
                                final String url=Url.url_api+"/login";
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Http http=new Http(login.this,url);
                                        http.setMethod("POST");
                                        http.setData(data);
                                        http.sendData(null);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    if(http.getResponse()!=null){
                                                        JSONObject response=new JSONObject(http.getResponse());
                                                        String status=response.getString("status");
                                                        if(status.equals("succes")){
                                                            int id=response.getInt("id");
                                                            String token=response.getString("token");
                                                            simpleProgressBar.setVisibility(View.INVISIBLE);
                                                            SharedPreferences.Editor myEdit = sharedPreferencesLogin.edit();
                                                            myEdit.putString("userId", id+"");
                                                            myEdit.putString("token", token);
                                                            myEdit.apply();
                                                            Intent i = new Intent(login.this, SampleActivity.class);
                                                            i.putExtra("userId", id+"");
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                        else {
                                                            simpleProgressBar.setVisibility(View.INVISIBLE);
                                                            if(status.equals("user not exist"))
                                                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.exist),Toast.LENGTH_LONG).show();
                                                            else if(status.equals("false password"))
                                                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.false_pass),Toast.LENGTH_LONG).show();
                                                           /* _txtPassword.setEnabled(true);
                                                            _txtUserCin.setEnabled(true);
                                                            _btnLog.setEnabled(true);
                                                            register.setEnabled(true);*/
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            //progressLayout.setVisibility(View.INVISIBLE);
                                                        }
                                                    }
                                                    else{
                                                        //Toast.makeText(getApplicationContext(),getResources().getString(R.string.server),Toast.LENGTH_LONG).show();
                                                        simpleProgressBar.setVisibility(View.INVISIBLE);
                                                        /*_txtPassword.setEnabled(true);
                                                        _txtUserCin.setEnabled(true);
                                                        _btnLog.setEnabled(true);
                                                        register.setEnabled(true);*/
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        //progressLayout.setVisibility(View.INVISIBLE);
                                                        tryAgain();
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }).start();


                            }
                        }
                    }
                });
            }
        }).start();
    }



}
