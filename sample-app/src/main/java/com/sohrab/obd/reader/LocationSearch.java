package com.sohrab.obd.reader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class LocationSearch extends AppCompatActivity {

    TextView btnSelectDateStart, btnSelectTimeStart;
    TextView btnSelectDateEnd, btnSelectTimeEnd;
    Button showTrip;
    boolean aBoolean=true;
    TextView start,end;
    String dateStart="",timeStart="",dateEnd="",timeEnd="";

    static final int DATE_DIALOG_ID_S1 = 0;
    static final int TIME_DIALOG_ID_S1 = 1;
    static final int DATE_DIALOG_ID_F1 = 2;
    static final int TIME_DIALOG_ID_F1 = 3;
    //ArrayList<Double> pointY =new ArrayList<>();
   // ArrayList<Double> pointX =new ArrayList<>();
   // ArrayList<String> timeTab =new ArrayList<>();
    ArrayList<String> dateDEbTab=new ArrayList<>();
    ArrayList<String> dateFinTab=new ArrayList<>();
    AlertDialog.Builder builder;

    // declare  the variables to Show/Set the date and time whenTime and  Date Picker Dialog first appears
    public int year, month, day, hour=0, minute=1;
    public int year1, month1, day1, hour1=23, minute1=58;
    // variables to save user selected date and time
    private int mYear, mMonth, mDay, mHour, mMinute;
    String dateDEb="", dateFin="";
    String userId;
    ProgressBar simpleProgressBar;
    Toolbar toolbar;
    boolean connexion;
    //ConstraintLayout progressLayout;
    int icon;
    BottomNavigationView bottomNavigationView;
    ArrayList<Double> pointX=new ArrayList<>();
    ArrayList<Double> pointY=new ArrayList<>();
    ArrayList<String> timeTab=new ArrayList<>();
    ArrayList<String> locationTab=new ArrayList<>();
    String color;
    // constructor

    public LocationSearch() {
        // Assign current Date and Time Values to Variables
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferencess=getSharedPreferences("modeDark", Activity.MODE_PRIVATE);
        String modeDark=sharedPreferencess.getString("choixDark","0");
        if(modeDark.equals("1")){
            setTheme(R.style.DarkTheme);
            icon=R.drawable.toolbar_dark;
            color="#FFFFFF";
        }
        if(modeDark.equals("0")){
            setTheme(R.style.AppTheme);
            icon=R.drawable.toolbar_light;
            color="#000000";
        }
        setContentView(R.layout.activity_location_search);
        // get the references of buttons
        btnSelectDateStart = (TextView) findViewById(R.id.buttonSelectDateStart);
        btnSelectTimeStart = (TextView) findViewById(R.id.buttonSelectTimeStart);
        btnSelectDateEnd = (TextView) findViewById(R.id.buttonSelectDateEnd);
        btnSelectTimeEnd = (TextView) findViewById(R.id.buttonSelectTimeEnd);
        builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        //progressLayout=findViewById(R.id.progresslayout);
        start=findViewById(R.id.start);
        end=findViewById(R.id.end);
        showTrip = (Button) findViewById(R.id.buttonSelectLoc);
        simpleProgressBar=findViewById(R.id.loc);
        toolbar=findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(icon);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.historique));
        simpleProgressBar.setVisibility(View.INVISIBLE);
        Intent intent=getIntent();
        userId=intent.getStringExtra("userId");
        bottomNavigationView=findViewById(R.id.navbar);
        bottomNavigationView.setSelectedItemId(R.id.nav_histo);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNavOption(item.getItemId());
                return true;
            }
        });

        // Set ClickListener on btnSelectDate
        btnSelectDateStart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the DatePickerDialog
                showDialog(DATE_DIALOG_ID_S1);
            }
        });
        btnSelectDateEnd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the DatePickerDialog
                showDialog(DATE_DIALOG_ID_F1);
            }
        });

        // Set ClickListener on btnSelectTime
        btnSelectTimeStart.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the TimePickerDialog
                showDialog(TIME_DIALOG_ID_S1);
            }
        });
        btnSelectTimeEnd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the TimePickerDialog
                showDialog(TIME_DIALOG_ID_F1);
            }
        });

        showTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
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
                simpleProgressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connexion=isOnline();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    SharedPreferences sharedPreferencesLogin=getSharedPreferences("login", MODE_PRIVATE);
                                    final String token_key = sharedPreferencesLogin.getString("token", "");
                                    final String url=Url.url_api+"/logout";
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final Http http=new Http(LocationSearch.this,url);
                                            http.setToken(true);
                                            http.setMethod("POST");
                                            http.sendData(token_key);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    SharedPreferences preferences = getSharedPreferences("login", 0);
                                                    preferences.edit().remove("userId").commit();
                                                    Intent i=new Intent(LocationSearch.this,login.class);
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
                simpleProgressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connexion=isOnline();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    Intent i=new Intent(LocationSearch.this,receive.class);
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
                simpleProgressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connexion=isOnline();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    Intent i=new Intent(LocationSearch.this,Parametres.class);
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

    // Register  DatePickerDialog listener
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                // the callback received when the user "sets" the Date in the DatePickerDialog
                public void onDateSet(DatePicker view, int yearSelected,
                                      int monthOfYear, int dayOfMonth) {
                    year = yearSelected;
                    month = monthOfYear;
                    day = dayOfMonth;
                    // Set the Selected Date in Select date Button
                    dateStart=" "+day + "-" + (month+1) + "-" + year+" ";
                    start.setText(dateStart+"\t"+timeStart);
                }
            };

    // Register  TimePickerDialog listener
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                // the callback received when the user "sets" the TimePickerDialog in the dialog
                public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                    hour = hourOfDay;
                    minute = min;
                    // Set the Selected Date in Select date Button
                    timeStart=" "+hour + "-" + minute+" ";
                    start.setText(dateStart+"\t"+timeStart);
                }
            };
    // Register  DatePickerDialog listener
    private DatePickerDialog.OnDateSetListener mDateSetListener2 =
            new DatePickerDialog.OnDateSetListener() {
                // the callback received when the user "sets" the Date in the DatePickerDialog
                public void onDateSet(DatePicker view, int yearSelected,
                                      int monthOfYear, int dayOfMonth) {
                    year1 = yearSelected;
                    month1 = monthOfYear;
                    day1 = dayOfMonth;
                    // Set the Selected Date in Select date Button
                    dateEnd=" "+day1 + "-" + (month1+1) + "-" + year1+" ";
                    end.setText(dateEnd+"\t"+timeEnd);
                }
            };

    // Register  TimePickerDialog listener
    private TimePickerDialog.OnTimeSetListener mTimeSetListener2 =
            new TimePickerDialog.OnTimeSetListener() {
                // the callback received when the user "sets" the TimePickerDialog in the dialog
                public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                    hour1 = hourOfDay;
                    minute1 = min;
                    // Set the Selected Date in Select date Button
                    timeEnd=" "+hour1 + "-" + minute1+" ";
                    end.setText(dateEnd+"\t"+timeEnd);
                }
            };


    // Method automatically gets Called when you call showDialog()  method
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID_S1:
                // create a new DatePickerDialog with values you want to show
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
            // create a new TimePickerDialog with values you want to show
            case TIME_DIALOG_ID_S1:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
            case DATE_DIALOG_ID_F1:
                // create a new DatePickerDialog with values you want to show
                return new DatePickerDialog(this,
                        mDateSetListener2,
                        mYear, mMonth, mDay);
            // create a new TimePickerDialog with values you want to show
            case TIME_DIALOG_ID_F1:
                return new TimePickerDialog(this,
                        mTimeSetListener2, mHour, mMinute, false);

        }
        return null;
    }

    /*class Connection extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            cin=getIntent().getStringExtra("cin");
            String result = "";
            String host = Url.url+"/listTrip.php?CIN="+cin+"&dateDeb="+dateDEb+"&dateFin="+dateFin;
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
                        String dateDeb = user.getString("DateDebut");
                        String dateFin = user.getString("DateFin");
                        dateDEbTab.add(dateDeb);
                        dateFinTab.add(dateFin);
                    }
                    Intent intent=new Intent(LocationSearch.this,TripList.class);
                    intent.putExtra("tableauTimeDebut",dateDEbTab);
                    intent.putExtra("tableauTimeFin",dateFinTab);
                    intent.putExtra("cin",cin);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),R.string.noTrip,Toast.LENGTH_SHORT).show();
                    simpleProgressBar.setVisibility(View.INVISIBLE);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/
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

    public void bottomNavOption(int id){
        switch(id){
            case R.id.nav_new:
                Intent ii = new Intent(LocationSearch.this, SampleActivity.class);
                ii.putExtra("userId", userId);
                startActivity(ii);
                finish();
                break;
            case R.id.nav_cont:
                simpleProgressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connexion=isOnline();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    Intent ii = new Intent(LocationSearch.this, CarList.class);
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
            case R.id.nav_histo:
                break;
            case R.id.nav_last:
                simpleProgressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connexion=isOnline();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    lastTrip();
                                    //showAlertLastTrip(getResources().getString(R.string.savetrip),getResources().getString(R.string.warning));
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.nav_live:
                Toast.makeText(getApplicationContext(),R.string.noshow,Toast.LENGTH_SHORT).show();
                bottomNavigationView.setSelectedItemId(R.id.nav_histo);
                break;
        }
    }

    public void lastTrip(){
        simpleProgressBar.setVisibility(View.VISIBLE);
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
                            simpleProgressBar.setVisibility(View.INVISIBLE);
                            NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                        }
                        else{
                            simpleProgressBar.setVisibility(View.INVISIBLE);
                            simpleProgressBar.setVisibility(View.VISIBLE);
                            final String url=Url.url_api+"/position/"+userId;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final Http http=new Http(LocationSearch.this,url);
                                    http.setMethod("GET");
                                    http.setData(null);
                                    http.sendData(null);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(http.getResponse()!=null){
                                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                                                            Intent intent = new Intent(LocationSearch.this, Location.class);
                                                            intent.putExtra("tableauLatitude", pointX);
                                                            intent.putExtra("tableauLongitude", pointY);
                                                            intent.putExtra("tableauTime", timeTab);
                                                            intent.putExtra("tableauLocation", locationTab);
                                                            intent.putExtra("ref", "historique");
                                                            intent.putExtra("userId", userId);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else{
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                                bottomNavigationView.setSelectedItemId(R.id.nav_histo);
                                                tryAgainLastTrip();
                                            }
                                            simpleProgressBar.setVisibility(View.INVISIBLE);
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

    public void search(){
        simpleProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(month+1<10)
            dateDEb=String.valueOf(year)+"-0"+String.valueOf(month+1)+"-"+String.valueOf(day)+" "+String.valueOf(hour)+":"+String.valueOf(minute-1)+":"+"00";
        else
            dateDEb=String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(day)+" "+String.valueOf(hour)+":"+String.valueOf(minute-1)+":"+"00";
        if(month1+1<10)
            dateFin=String.valueOf(year1)+"-0"+String.valueOf(month1+1)+"-"+String.valueOf(day1)+" "+String.valueOf(hour1)+":"+String.valueOf(minute1+1)+":"+"59";
        else
            dateFin=String.valueOf(year1)+"-"+String.valueOf(month1+1)+"-"+String.valueOf(day1)+" "+String.valueOf(hour1)+":"+String.valueOf(minute1+1)+":"+"59";

        if(dateDEb.contains("0-01-0") || dateFin.contains("0-01-0")){
            Log.i("kkkk","not here");
            Log.i("kkkk",dateFin+"--"+dateDEb);
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.histodate),Toast.LENGTH_LONG).show();
            simpleProgressBar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else{
            Log.i("kkkk","here");
            Log.i("kkkk",dateFin+"--"+dateDEb);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    connexion=isOnline();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!connexion){
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                NotificationHelper.showNotificatioon(getApplicationContext(),"Attention ","Pas de connexion");
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                //progressLayout.setVisibility(View.INVISIBLE);
                                tryAgain();
                            }
                            else{
                                Intent intent=new Intent(LocationSearch.this,TripList.class);
                                intent.putExtra("start",dateDEb);
                                intent.putExtra("end",dateFin);
                                intent.putExtra("userId",userId);
                                startActivity(intent);
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                //progressLayout.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }).start();
        }

        //progressLayout.setVisibility(View.VISIBLE);

        //if(!isOnline())
            //NotificationHelper.showNotificatioon(getApplicationContext(),"Attention ","Pas de connexion");
        //simpleProgressBar.setVisibility(View.VISIBLE);
    }

    public void tryAgain(){
        //Setting message manually and performing action on button click
        builder.setMessage(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.requested_data_could_not_be_loaded)+"</font>")).
                setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#4D7D8D'>"+
                getResources().getString(R.string.try_again)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                search();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
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
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
    }

    @Override
    public void onBackPressed() {}
}



