package com.sohrab.obd.reader;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CarList extends MyActivity {

    ArrayList<String> vinTab=new ArrayList<>();
    ArrayList<String> listDate = new ArrayList<>();
    String userId;
    Context context;
    Button addCar;
    String newVin="";
    RelativeLayout relativeLayout;
    boolean connexion;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    android.app.AlertDialog.Builder builder;
    String color;
    BottomNavigationView bottomNavigationView;
    ArrayList<Double> pointX=new ArrayList<>();
    ArrayList<Double> pointY=new ArrayList<>();
    ArrayList<String> timeTab=new ArrayList<>();
    ArrayList<String> locationTab=new ArrayList<>();
    Toolbar toolbar;
    int icon;
    String token_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferencess=getSharedPreferences("modeDark", Activity.MODE_PRIVATE);
        String modeDark=sharedPreferencess.getString("choixDark","0");
        if(modeDark.equals("1")){
            setTheme(R.style.DarkTheme);
            color="#FFFFFF";
            icon=R.drawable.toolbar_dark;
        }
        if(modeDark.equals("0")){
            setTheme(R.style.AppTheme);
            color="#000000";
            icon=R.drawable.toolbar_light;
        }
        setContentView(R.layout.activity_car_list);
        /*relativeLayout=findViewById(R.id.carlist);
        if(modeDark.equals("1")){
            relativeLayout.setBackgroundResource(R.drawable.dark_back);
        }
        if(modeDark.equals("0")){
            relativeLayout.setBackgroundResource(R.drawable.back_light);
        }*/
        context=getApplicationContext();
        // get the reference of RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewVin);
        TextView cinView=findViewById(R.id.cinCar);
        addCar=findViewById(R.id.addCar);
        progressBar=findViewById(R.id.progresslist);
        //progressLayout=findViewById(R.id.progresslayout);
        toolbar=findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(icon);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.controller));
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        userId = getIntent().getStringExtra("userId");
        builder = new android.app.AlertDialog.Builder(this,R.style.MyDialogTheme);
        loadCarList();
        bottomNavigationView=findViewById(R.id.navbar);
        bottomNavigationView.setSelectedItemId(R.id.nav_cont);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNavOption(item.getItemId());
                return true;
            }
        });
        //cinView.append(userId);
        /*if(getIntent().hasExtra("tableauVin")) {
            vinTab = (ArrayList<String>) getIntent().getSerializableExtra("tableauVin");
            for (int i = 0; i < vinTab.size(); i++) {
                listDate.add(vinTab.get(i) + "--" + getMarque(vinTab.get(i).substring(0, 3)));
            }
        }*/
        /*SharedPreferences sharedPreferences=getSharedPreferences("newwCar", Activity.MODE_PRIVATE);
        newVin=sharedPreferences.getString("caar","");

        if(!newVin.equals("")){
            listDate.add(newVin+"--"+getMarque(newVin.substring(0,3)));
            SharedPreferences preferences = getSharedPreferences("newwCar", 0);
            preferences.edit().remove("caar").commit();
        }*/

        addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        SharedPreferences sharedPreferencesLogin=getSharedPreferences("login", MODE_PRIVATE);
        token_key = sharedPreferencesLogin.getString("token", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
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

                                    final String url=Url.url_api+"/logout";
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final Http http=new Http(CarList.this,url);
                                            http.setToken(true);
                                            http.setMethod("POST");
                                            http.sendData(token_key);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    SharedPreferences preferences = getSharedPreferences("login", 0);
                                                    preferences.edit().remove("userId").commit();
                                                    Intent i=new Intent(CarList.this,login.class);
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
                break;
            case R.id.profil:
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
                                    Intent i=new Intent(CarList.this,receive.class);
                                    i.putExtra("userId",userId);
                                    startActivity(i);
                                    //finish();
                                    //showAlertProfil(getResources().getString(R.string.savetrip),getResources().getString(R.string.warning));
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.param:
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
                                    Intent i=new Intent(CarList.this,Parametres.class);
                                    i.putExtra("userId",userId);
                                    startActivity(i);
                                    //finish();
                                    //showAlertSet(getResources().getString(R.string.savetrip),getResources().getString(R.string.warning));
                                }
                            }
                        });
                    }
                }).start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getMarque(String code){
        String marque ="";
        switch (code){
            case "TRU":marque="AUDI TT";break;
            case "1C4":marque="1C4";break;
            case "1FA":marque="Ford";break;
            case "4S3":marque="Subaru";break;
            case "5FN":marque="Honda";break;
            case "5GT":marque="Hummer";break;
            case "5TD":marque="Toyota";break;
            case "JF1":marque="Subaru";break;
            case "JMA":marque="Mitsubishi";break;
            case "JMZ":marque="Mazda";break;
            case "JTD":marque="Toyota";break;
            case "KL1":marque="Chevrolet";break;
            case "KND":marque="Kia";break;
            case "KPA":marque="Ssangyong";break;
            case "SAL":marque="Landrover";break;
            case "SJN":marque="Nissan";break;
            case "TMB":marque="Skoda";break;
            case "UU1":marque="Dacia";break;
            case "VF1":marque="Renault";break;
            case "VF6":marque="RENAULT TRUCKS (Volvo)";break;
            case "VF3":marque="Peugeot";break;
            case "VF7":marque="Citroën";break;
            case "VWV":marque="Wolkswagen";break;
            case "WBA":marque="BMW";break;
            case "W0L":marque="Opel";break;
            case "WDA":marque="Mercedes";break;
            case "WDB":marque="Mercedes";break;
            case "WF0":marque="Ford";break;
            case "WME":marque="Smart";break;
            case "WMW":marque="Mini";break;
            case "WP0":marque="Porsche";break;
            case "WVW":marque="Volkswagen";break;
            case "VF4":marque="TALBOT";break;
            case "VF8":marque="MATRA";break;
            case "VFA":marque="ALPINE RENAULT";break;
            case "VJ1":marque="HEULIEZ BUS";break;
            case "VJ2":marque="MIA ELECTRIC";break;
            case "VN1":marque="OPEL ( Utilitaires )";break;
            case "VNV":marque="NISSAN  ( Utilitaires )";break;
            case "VNK":marque="TOYOTA";break;
            case "VR1":marque="DS AUTOMOBILE";break;
            case "VR3":marque="PEUGEOT";break;
            case "VR7":marque="CITROËN";break;
            case "VSS":marque="SEAT";break;
            case "VSX":marque="OPEL";break;
            case "VS6":marque="FORD";break;
            case "VSG":marque="NISSAN";break;
            case "VSE":marque="Santana Motors";break;
            case "WAU":marque="AUDI";break;
            case "WP1":marque="PORSCHE SUV";break;
        }
        return marque;
    }

    private void openDialog(){
        LayoutInflater inflater = LayoutInflater.from(CarList.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);
        final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);
        SharedPreferences sharedPreferences=getSharedPreferences("newCar", Activity.MODE_PRIVATE);
        subEditText.setText(sharedPreferences.getString("car",""));
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        builder.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.addCar)+"</font>"));
        builder.setMessage(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.enterVin)+"</font>"));
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton(Html.fromHtml("<font color='#4D7D8D'>"+
                getResources().getString(R.string.valider)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(subEditText.getText().length()==17){
                    final String vin=subEditText.getText().toString();
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
                                        JSONObject params=new JSONObject();
                                        try {
                                            params.put("vin",vin);
                                            params.put("user_id",userId);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        final String data=params.toString();
                                        final String url=Url.url_api+"/addcar";
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                final Http http=new Http(CarList.this,url);
                                                http.setMethod("POST");
                                                http.setData(data);
                                                http.setToken(true);
                                                http.sendData(token_key);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if(http.getResponse()!=null){
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            try {
                                                                JSONObject response = new JSONObject(http.getResponse());
                                                                String status=response.getString("status");
                                                                if(status.equals("succes")){
                                                                    listDate.add(0,vin+"   "+getMarque(vin.substring(0,3)));
                                                                    // call the constructor of CustomAdapter to send the reference and data to Adapter
                                                                    CustomAdapterVin customAdapter = new CustomAdapterVin(CarList.this, listDate);
                                                                    //recyclerView.addItemDecoration(new LineDividerItemDecoration(this, R.drawable.line_divider));
                                                                    recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
                                                                }
                                                                else{
                                                                    Toast.makeText(getApplicationContext(),status,Toast.LENGTH_LONG).show();
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }


                                                            //recreate();
                                                        }
                                                        else{
                                                            Toast.makeText(getApplicationContext(),R.string.server,Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                }
                            });
                        }
                    }).start();
                    /*String type = "car";
                    BackgroundTask backgroundTask = new BackgroundTask(getApplicationContext());
                    backgroundTask.execute(type, cin, vin);
                    JSONObject jsonResult = null;
                    try {
                        jsonResult = new JSONObject(backgroundTask.get());
                        String success = jsonResult.getString("message");

                        if (success.equals("registered successfully")) {
                            Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor=getSharedPreferences("newwCar",MODE_PRIVATE).edit();
                            editor.putString("caar",vin);
                            editor.apply();
                            recreate();
                        } else  {
                            Toast.makeText(getApplicationContext(),success, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }
                else
                Toast.makeText(getApplicationContext(),R.string.vininvalide,Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton(Html.fromHtml("<font color='#4D7D8D'>"+
                getResources().getString(R.string.cancel)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
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

    public void loadCarList(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connexion=isOnline();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!connexion){
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            //progressLayout.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                            tryAgain();
                        }
                        else{
                            final String url=Url.url_api+"/getcar/"+userId;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final Http http=new Http(CarList.this,url);
                                    http.setMethod("GET");
                                    http.sendData(null);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(http.getResponse()!=null) {
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                //progressLayout.setVisibility(View.INVISIBLE);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                JSONObject response = null;
                                                try {
                                                    response = new JSONObject(http.getResponse());
                                                    JSONArray cars=response.getJSONArray("data");
                                                    for(int i=0;i<cars.length();i++){
                                                        JSONObject car=cars.getJSONObject(i);
                                                        listDate.add(car.getString("vin")+"   "+getMarque(car.getString("vin").substring(0, 3)));
                                                    }
                                                    if(listDate.size()>0){
                                                        // call the constructor of CustomAdapter to send the reference and data to Adapter
                                                        CustomAdapterVin customAdapter = new CustomAdapterVin(CarList.this, listDate);
                                                        //recyclerView.addItemDecoration(new LineDividerItemDecoration(this, R.drawable.line_divider));
                                                        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                            else{
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                //progressLayout.setVisibility(View.INVISIBLE);
                                                tryAgain();
                                                // Toast.makeText(getApplicationContext(),R.string.server,Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
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

    public void tryAgain(){
        //Setting message manually and performing action on button click
        builder.setMessage(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.requested_data_could_not_be_loaded)+"</font>")).
                setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#4D7D8D'>"+
                getResources().getString(R.string.try_again)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadCarList();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
    }

    public void lastTrip(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connexion=isOnline();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!connexion){
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                        }
                        else{
                            progressBar.setVisibility(View.VISIBLE);
                            final String url=Url.url_api+"/position/"+userId;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final Http http=new Http(CarList.this,url);
                                    http.setMethod("GET");
                                    http.setData(null);
                                    http.sendData(null);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                                                            Intent intent = new Intent(CarList.this, Location.class);
                                                            intent.putExtra("tableauLatitude", pointX);
                                                            intent.putExtra("tableauLongitude", pointY);
                                                            intent.putExtra("tableauTime", timeTab);
                                                            intent.putExtra("tableauLocation", locationTab);
                                                            intent.putExtra("ref", "control");
                                                            intent.putExtra("userId", userId);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else{
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                bottomNavigationView.setSelectedItemId(R.id.nav_cont);
                                                tryAgainLastTrip();
                                            }
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

    public void tryAgainLastTrip(){
        builder.setMessage(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.requested_data_could_not_be_loaded)+"</font>")).
                setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#4D7D8D'>"+
                getResources().getString(R.string.try_again)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lastTrip();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
    }

    public void bottomNavOption(int id){
        switch(id){
            case R.id.nav_new:
                bottomNavigationView.setSelectedItemId(R.id.nav_cont);
                Intent ii = new Intent(CarList.this, SampleActivity.class);
                ii.putExtra("userId", userId);
                startActivity(ii);
                finish();
                break;
            case R.id.nav_cont:
                break;
            case R.id.nav_histo:
                bottomNavigationView.setSelectedItemId(R.id.nav_cont);
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
                                    Intent ii = new Intent(CarList.this, LocationSearch.class);
                                    ii.putExtra("userId", userId);
                                    startActivity(ii);
                                    finish();
                                    //showAlertController(getResources().getString(R.string.savetrip),getResources().getString(R.string.warning));
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.nav_last:
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
                                    bottomNavigationView.setSelectedItemId(R.id.nav_cont);
                                    lastTrip();
                                    //showAlertLastTrip(getResources().getString(R.string.savetrip),getResources().getString(R.string.warning));
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.nav_live:
                bottomNavigationView.setSelectedItemId(R.id.nav_cont);
                Toast.makeText(getApplicationContext(),R.string.noshow,Toast.LENGTH_SHORT).show();
                bottomNavigationView.setSelectedItemId(R.id.nav_cont);
                break;
        }
    }

    @Override
    public void onBackPressed() {}
}
