package com.sohrab.obd.reader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Dashboard extends MyActivity {
    CardView trip,histTrip,profile,logout,newtrip,parametre,controler,about;
    String userId;
    String dateDeb,dateFin;
    ArrayList<Double> pointX=new ArrayList<>();
    ArrayList<Double> pointY=new ArrayList<>();
    ArrayList<String> timeTab=new ArrayList<>();
    ArrayList<String> speedTab=new ArrayList<>();
    ArrayList<String> vinTab=new ArrayList<>();
    ArrayList<String> locationTab=new ArrayList<>();
    ArrayList<String> engineRpmTab=new ArrayList<>();
    ArrayList<String> engineLoadTab=new ArrayList<>();
    ArrayList<String> throttlePosTab=new ArrayList<>();
    ArrayList<String> fuelConsTab=new ArrayList<>();

    String date1,time1,date2,time2;
    ProgressBar progressBar;
    String typeCompte;
    String[] options;
    Boolean option;
    boolean connexion;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferencess=getSharedPreferences("modeDark", Activity.MODE_PRIVATE);
        String modeDark=sharedPreferencess.getString("choixDark","0");
        if(modeDark.equals("1")){
            setTheme(R.style.DarkTheme);
        }
        if(modeDark.equals("0")){
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_dashboard);
        linearLayout=findViewById(R.id.dashboardLayout);
        if(modeDark.equals("1")){
            linearLayout.setBackgroundResource(R.drawable.dark_back);
        }
        if(modeDark.equals("0")){
            linearLayout.setBackgroundResource(R.drawable.back_light);
        }
        trip=findViewById(R.id.lastMap);
        histTrip=findViewById(R.id.histoMap);
        profile=findViewById(R.id.profile);
        logout=findViewById(R.id.logout);
        newtrip=findViewById(R.id.newtrip);
        parametre=findViewById(R.id.param);
        controler=findViewById(R.id.controller);
        about=findViewById(R.id.aboutus);
        progressBar=findViewById(R.id.progDash);
        progressBar.setVisibility(View.INVISIBLE);
        userId=getIntent().getStringExtra("userId");

        SharedPreferences sharedPreferences=getSharedPreferences("Compte", Activity.MODE_PRIVATE);
        typeCompte=sharedPreferences.getString("Type","");
        trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connexion=isOnline();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    progressBar.setVisibility(View.INVISIBLE);
                                    options=new String[]{getResources().getString(R.string.itinéraire),getResources().getString(R.string.eval)};
                                    AlertDialog.Builder builder=new AlertDialog.Builder(Dashboard.this);
                                    builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==0){
                                                option=true;
                                                dialog.dismiss();
                                                progressBar.setVisibility(View.VISIBLE);
                                                final String url=Url.url_api+"/position/"+userId;
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Http http=new Http(Dashboard.this,url);
                                                        http.setMethod("GET");
                                                        http.setData(null);
                                                        http.sendData(null);

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if(http.getResponse()!=null){
                                                                    try {
                                                                        JSONObject response=new JSONObject(http.getResponse());
                                                                        String status=response.getString("status");
                                                                        if(status.equals("succes")){
                                                                            JSONArray positions=response.getJSONArray("data");
                                                                            for(int i=0;i<positions.length();i++){
                                                                                JSONObject trip=positions.getJSONObject(i);
                                                                                pointX.add(Double.valueOf(trip.getString("latitude")));
                                                                                pointY.add(Double.valueOf(trip.getString("longitude")));
                                                                                timeTab.add(trip.getString("created_at"));
                                                                                locationTab.add(trip.getString("zone"));
                                                                            }
                                                                            if(pointX.size()>1 ) {
                                                                                Intent intent = new Intent(Dashboard.this, Location.class);
                                                                                intent.putExtra("tableauLatitude", pointX);
                                                                                intent.putExtra("tableauLongitude", pointY);
                                                                                intent.putExtra("tableauTime", timeTab);
                                                                                intent.putExtra("tableauLocation", locationTab);
                                                                                startActivity(intent);
                                                                            }
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                            }
                                                        });
                                                    }
                                                }).start();
                                            }
                                            if(which==1){
                                                option=false;
                                                dialog.dismiss();
                                                //progressBar.setVisibility(View.VISIBLE);
                                                //new Connection().execute();
                                            }
                                        }
                                    });
                                    AlertDialog alertDialog=builder.create();
                                    alertDialog.show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });
        histTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeCompte.equals("pro")){
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            connexion=isOnline();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!connexion){
                                        progressBar.setVisibility(View.INVISIBLE);
                                        NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                    }
                                    else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Intent intent = new Intent(Dashboard.this, LocationSearch.class);
                                        intent.putExtra("userId", userId);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }).start();
            }
                else Toast.makeText(getApplicationContext(),R.string.prosvp,Toast.LENGTH_SHORT).show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connexion=isOnline();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else {
                                    Intent intent = new Intent(Dashboard.this, receive.class);
                                    intent.putExtra("userId", userId);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }).start();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connexion=isOnline();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else {
                                    SharedPreferences sharedPreferencesLogin=getSharedPreferences("login", MODE_PRIVATE);
                                    final String token_key = sharedPreferencesLogin.getString("token", "");
                                    final String url=Url.url_api+"/logout";
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final Http http=new Http(Dashboard.this,url);
                                            http.setToken(true);
                                            http.setMethod("POST");
                                            http.sendData(token_key);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    SharedPreferences preferences = getSharedPreferences("login", 0);
                                                    preferences.edit().remove("userId").commit();
                                                    Intent i=new Intent(Dashboard.this,login.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        newtrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connexion=isOnline();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else {
                                    Intent intent = new Intent(Dashboard.this, SampleActivity.class);
                                    intent.putExtra("userId", userId);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        parametre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeCompte.equals("pro")){
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            connexion=isOnline();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!connexion){
                                        progressBar.setVisibility(View.INVISIBLE);
                                        NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                    }
                                    else {
                                        Intent intent = new Intent(Dashboard.this, Parametres.class);
                                        intent.putExtra("userId", userId);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                    }).start();
            }
                else Toast.makeText(getApplicationContext(),R.string.prosvp,Toast.LENGTH_SHORT).show();
            }
        });

        controler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeCompte.equals("pro")){
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            connexion=isOnline();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!connexion){
                                        progressBar.setVisibility(View.INVISIBLE);
                                        NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                    }
                                    else{
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Intent intent = new Intent(Dashboard.this, CarList.class);
                                        intent.putExtra("userId", userId);
                                        startActivity(intent);

                                    }
                                }
                            });
                        }
                    }).start();
                }
                else Toast.makeText(getApplicationContext(),R.string.prosvp,Toast.LENGTH_SHORT).show();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeCompte.equals("pro")){
                        Intent intent=new Intent(Dashboard.this,Aboutus.class);
                        startActivity(intent);
                }
                else Toast.makeText(getApplicationContext(),R.string.prosvp,Toast.LENGTH_SHORT).show();
            }
        });
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
    /*class Connection extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String host = Url.url+"/lastMap.php?CIN="+cin;

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer stringBuffer = new StringBuffer("");
                String line = "";
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonResult = new JSONObject(s);
                int success = jsonResult.getInt("success");
                if (success == 1) {
                    JSONArray users = jsonResult.getJSONArray("users");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                         dateDeb = user.getString("DateDebut");
                         dateFin = user.getString("DateFin");
                    }
                    new ConnectionPlus().execute();
                } else if(success==0)
                    Toast.makeText(getApplicationContext(),R.string.noshow,Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(),R.string.server,Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ConnectionPlus extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String[] dateFrac1=dateDeb.split(" ");
            date1=dateFrac1[0];
            time1=dateFrac1[1];
            String[] dateFrac2=dateFin.split(" ");
            date2=dateFrac2[0];
            time2=dateFrac2[1];
            String host = Url.url+"/recupPosition.php?CIN="+cin+"&dateDeb="+date1+"%20"+time1+"&dateFin="+date2+"%20"+time2;
            try {

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer stringBuffer = new StringBuffer("");
                String line = "";
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonResult = new JSONObject(s);
                int success = jsonResult.getInt("success");
                if (success == 1) {
                    JSONArray users = jsonResult.getJSONArray("users");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        double latitude = user.getDouble("altitude");
                        double longitude = user.getDouble("longitude");
                        String time=user.getString("time");
                        String speed=user.getString("SPEED");
                        String zone=user.getString("zone");
                        String engineRpm=user.getString("ENGINE_RPM");
                        String engineLoad=user.getString("ENGINE_LOAD");
                        String throttlePos=user.getString("ThrottlePos");
                        String fuelCons=user.getString("insFuel");
                        pointX.add(latitude);
                        pointY.add(longitude);
                        timeTab.add(time);
                        speedTab.add(speed);
                        locationTab.add(zone);
                        engineRpmTab.add(engineRpm);
                        engineLoadTab.add(engineLoad);
                        throttlePosTab.add(throttlePos);
                        fuelConsTab.add(fuelCons);
                    }
                    if(pointX.size()>1 ) {
                        Intent intent = new Intent(Dashboard.this, Location.class);
                        intent.putExtra("tableauLatitude", pointX);
                        intent.putExtra("tableauLongitude", pointY);
                        intent.putExtra("tableauTime", timeTab);
                        intent.putExtra("tableauLocation", locationTab);
                        intent.putExtra("tableauSpeed", speedTab);
                        intent.putExtra("cin",cin);
                        intent.putExtra("tableauEngineRpm",engineRpmTab);
                        intent.putExtra("tableauEngineLoad",engineLoadTab);
                        intent.putExtra("tableauThrottlePos",throttlePosTab);
                        intent.putExtra("tableauFuelCons",fuelConsTab);
                        //temporaire
                        intent.putExtra("date",date1);
                        intent.putExtra("time",time1);
                        intent.putExtra("reponse","no");
                        Intent intent2 = new Intent(Dashboard.this, result.class);
                        intent2.putExtra("tableauLatitude", pointX);
                        intent2.putExtra("tableauLongitude", pointY);
                        intent2.putExtra("tableauTime", timeTab);
                        intent2.putExtra("tableauSpeed", speedTab);
                        intent2.putExtra("tableauLocation", locationTab);
                        intent2.putExtra("cin",cin);
                        intent2.putExtra("tableauEngineRpm",engineRpmTab);
                        intent2.putExtra("tableauEngineLoad",engineLoadTab);
                        intent2.putExtra("tableauThrottlePos",throttlePosTab);
                        intent2.putExtra("tableauFuelCons",fuelConsTab);
                        //temporaire
                        intent2.putExtra("date",date1);
                        intent2.putExtra("time",time1);
                        intent2.putExtra("reponse","no");
                        if(option)
                            startActivity(intent);
                        else
                            startActivity(intent2);

                        pointX.clear();
                        pointY.clear();
                        timeTab.clear();
                        speedTab.clear();
                        locationTab.clear();
                        engineLoadTab.clear();
                        engineRpmTab.clear();
                        throttlePosTab.clear();
                        fuelConsTab.clear();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),R.string.emptyTrip,Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);}
                }
                else if(success==2){
                    Toast.makeText(getApplicationContext(),R.string.server,Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);}
                else{
                    Toast.makeText(getApplicationContext(),R.string.emptyTrip,Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ConnectionVin extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String host = Url.url+"/recupVin.php?CIN="+cin;

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(host));
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer stringBuffer = new StringBuffer("");
                String line = "";
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                    break;
                }
                reader.close();
                result = stringBuffer.toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonResult = new JSONObject(s);
                int success = jsonResult.getInt("success");
                if (success == 1) {
                    JSONArray users = jsonResult.getJSONArray("users");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        vinTab.add(user.getString("VIN"));
                    }
                    Intent intent = new Intent(Dashboard.this, CarList.class);
                    intent.putExtra("tableauVin", vinTab);
                    intent.putExtra("cin", cin);
                    startActivity(intent);
                    vinTab.clear();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    Toast.makeText(getApplicationContext(),R.string.nocar,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Dashboard.this, CarList.class);
                    intent.putExtra("cin", cin);
                    startActivity(intent);

                    progressBar.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        }*/
    }

