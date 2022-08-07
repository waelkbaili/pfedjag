package com.sohrab.obd.reader;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.AwesomeSpeedometer;
import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.sohrab.obd.reader.application.ObdPreferences;
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.trip.TripRecord;
import com.sohrab.obd.reader.utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.maps.model.JointType.ROUND;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_OBD_CONNECTION_STATUS;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by sohrab on 30/11/2017.
 * Sample activity to display OBD data
 */
public class SampleActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {
    double longitude=0,latitude=0;
    double exLongitude,exLatitude;
    String timeSpeed,exTimeSpeed;
    Toolbar toolbar,toolbar_des;
    String userId;
    String obd,gps;
    boolean connexion;
    private SensorManager sensorManager;
    Sensor accelerometer,gyroscope;
    DecimalFormat df = new DecimalFormat("0.000");
    AlertDialog.Builder builder;
    int cpt=0;
    ProgressBar simpleProgressBar;
    ArrayList<String> dataOBD =new ArrayList<>();
    ArrayList<String> dataOBDSync =new ArrayList<>();
    //Boolean modeVeille=false;
    CountDownTimer cTimer = null;
    Boolean timeOut=true;
    Boolean timeOutSensor=true;
    String code;
    //GlobalClass globalClass;
    //int mode;
    boolean existObd=false;
    boolean existGps=false;
    String time="";
    int sumLoc=0;
    int sumSpeed=0;
    int cptSpeed=0;
    String modeAuto;


    Boolean firstPos=true;
    double gravX,gravY,gravZ,accX,accY,accZ,gyrox,gyroy,gyroz;
    TripRecord tripRecordVin = TripRecord.getTripRecode(SampleActivity.this);
    String vin="";
    String typeCompte;
    Boolean bolStart=false;
    Boolean liveTrip=true;

    private LayoutInflater inflater;
    View view1,view2,view3;
    //principal layout
    //TextView swich;
    Button saveBtn,start;
    private TextView mObdInfoTextView,mObdDataTextView,locView;
    TextView digitSpeedView;
    BottomNavigationView bottomNavigationView;
    SegmentedGroup radioGroup;
    RadioButton radioButton;
    int id_press;
    //attribut layout design
    AwesomeSpeedometer speed,engime,temp;
    int id_press_des;
    //TextView swich_des;
    private TextView mObdInfoTextView_des,locView_des;
    TextView timeDes,distanceDes;
    Button finishDes,startDes;
    BottomNavigationView bottomNavigationViewDes;
    SegmentedGroup radioGroupDes;
    RadioButton radioButtonDes;
    //layout live map
    ImageButton ret;
    ImageButton toogle;
    GoogleMap mMap;
    Marker markerStart,marker;
    TextView limit,limitMap,limitDes;

    File myExternalFile;
    int icon;

    ArrayList<Double> pointX=new ArrayList<>();
    ArrayList<Double> pointY=new ArrayList<>();
    ArrayList<String> timeTab=new ArrayList<>();
    ArrayList<String> locationTab=new ArrayList<>();
    String color;
    AlertDialog.Builder builderTry;
    boolean resultAdd;
    String token_key;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    String sensorData="";
    //ArrayList<ArrayList<String>> fullSensorData = new ArrayList<ArrayList<String>>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferencesLogin=getSharedPreferences("login", MODE_PRIVATE);
        token_key = sharedPreferencesLogin.getString("token", "");
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
        builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        builderTry = new AlertDialog.Builder(this,R.style.MyDialogTheme);

        //if(! isAccessServiceEnabled(getApplicationContext(),CustomAccessibilityService.class))
            //showAuth(getResources().getString(R.string.accessibility_service_description),getResources().getString(R.string.auth));
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        WindowManager.LayoutParams default_layout_params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
         view1 = inflater.inflate(R.layout.activity_main, null);
        addContentView(view1, default_layout_params);
         view2 = inflater.inflate(R.layout.nocnx, null);
        addContentView(view2, default_layout_params);
        view3=inflater.inflate(R.layout.design,null);
        addContentView(view3,default_layout_params);
        view3.setVisibility(View.INVISIBLE);
        view2.setVisibility(View.INVISIBLE);
        view1.setVisibility(View.VISIBLE);
        view1.bringToFront();
       //layout principal
        mObdInfoTextView = findViewById(R.id.tv_obd_info);
        locView = findViewById(R.id.loc);
        mObdDataTextView=findViewById(R.id.data);
        start=findViewById(R.id.btnstart);
        saveBtn=findViewById(R.id.button);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setTintColor(Color.parseColor("#4D7D8D"), Color.parseColor("#FFFFFF"));
        id_press =radioGroup.getCheckedRadioButtonId();

        bottomNavigationView=findViewById(R.id.navbar);
        bottomNavigationView.setSelectedItemId(R.id.nav_new);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNavOption(item.getItemId());
                return true;
            }
        });
        simpleProgressBar =  findViewById(R.id.progress_bar);
        simpleProgressBar.setVisibility(View.INVISIBLE);
        toolbar=findViewById(R.id.myToolBar);
        //toolbar_des=findViewById(R.id.myToolBarDes);

        if(modeDark.equals("1")){
           icon=R.drawable.toolbar_dark;
        }
        if(modeDark.equals("0")){
            icon=R.drawable.toolbar_light;
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(icon);
        //swich=findViewById(R.id.sport);
        limit=findViewById(R.id.limiteSpeed);
        //sad=findViewById(R.id.sad);
        //nodata=findViewById(R.id.nodata);
        digitSpeedView = (TextView) findViewById(R.id.digit_speed_view);
        //SharedPreferences.Editor editorAcces=getSharedPreferences("modePlus",MODE_PRIVATE).edit();
        //editorAcces.putString("acces","0");
        //editorAcces.apply();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    showAlertResult("",getResources().getString(R.string.confirmation));
                                }
                            }
                        });
                    }
                }).start();
            }
        });
        /*swich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(obd.equals("1")){
                view3.setVisibility(View.VISIBLE);
                view1.setVisibility(View.INVISIBLE);}
                else
                    Toast.makeText(getApplicationContext(),"No OBD",Toast.LENGTH_LONG).show();
            }
        });*/
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=getSharedPreferences("start",MODE_PRIVATE).edit();
                editor.putString("datestart",getDateTime());
                editor.apply();
                bolStart=true;
                start.setVisibility(View.INVISIBLE);
                startDes.setVisibility(View.INVISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                finishDes.setVisibility(View.VISIBLE);
            }
        });
        //layout map
        ret=findViewById(R.id.back);
        SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        limitMap=findViewById(R.id.limiteSpeedMap);
        supportMapFragment.getMapAsync(this);
        toogle=findViewById(R.id.btnToggle);
        toogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GoogleMap.MAP_TYPE_NORMAL == mMap.getMapType()) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view2.setVisibility(View.INVISIBLE);
                view1.setVisibility(View.VISIBLE);
                bottomNavigationView.setSelectedItemId(R.id.nav_new);
                bottomNavigationViewDes.setSelectedItemId(R.id.nav_new);

            }
        });

        //layout design
        //swich_des=findViewById(R.id.classique_des);
        speed=findViewById(R.id.awesomeSpeedometer2);
        engime=findViewById(R.id.awesomeSpeedometer);
        temp=findViewById(R.id.awesomeSpeedometer3);
        mObdInfoTextView_des = findViewById(R.id.tv_obd_info_des);
        locView_des = findViewById(R.id.loc_des);
        timeDes=findViewById(R.id.timeDes);
        distanceDes=findViewById(R.id.distance);
        finishDes=findViewById(R.id.btnfinish_des);
        startDes=findViewById(R.id.btnstart_des);
        limitDes=findViewById(R.id.limiteSpeed_des);
        radioGroupDes = findViewById(R.id.radioGroupDes);
        radioGroupDes.setTintColor(Color.parseColor("#4D7D8D"), Color.parseColor("#FFFFFF"));
        id_press_des =radioGroupDes.getCheckedRadioButtonId();
        bottomNavigationViewDes=findViewById(R.id.navbar_des);
        bottomNavigationViewDes.setSelectedItemId(R.id.nav_new);
        bottomNavigationViewDes.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNavOption(item.getItemId());
                return true;
            }
        });
        /*swich_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view1.setVisibility(View.VISIBLE);
                view3.setVisibility(View.INVISIBLE);
            }
        });*/
        /*speed=findViewById(R.id.awesomeSpeedometer2);
        engime=findViewById(R.id.awesomeSpeedometer);
        temp=findViewById(R.id.awesomeSpeedometer3);
        timeDes=findViewById(R.id.timeDes);
        cnxDes=findViewById(R.id.statutCnx);
        finishDes=findViewById(R.id.finishDes);
        startDes=findViewById(R.id.startDes);
        gpsDes=findViewById(R.id.latitude);
        distanceDes=findViewById(R.id.distance);
        liveDes=findViewById(R.id.liveDes);
        switchDes=findViewById(R.id.switchDes);
        limitDes=findViewById(R.id.limiteSpeedDes);*/
        finishDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        simpleProgressBar.setVisibility(View.VISIBLE);
                        connexion=isOnline();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    showAlertResult("",getResources().getString(R.string.confirmation));
                                }
                            }
                        });
                    }
                }).start();
            }
        });
        startDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=getSharedPreferences("start",MODE_PRIVATE).edit();
                editor.putString("datestart",getDateTime());
                editor.apply();
                bolStart=true;
                start.setVisibility(View.INVISIBLE);
                startDes.setVisibility(View.INVISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                finishDes.setVisibility(View.VISIBLE);
            }
        });
       /* switchDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view3.setVisibility(View.INVISIBLE);
                view1.setVisibility(View.VISIBLE);
            }
        });
        liveDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeCompte.equals("pro")) {
                    if (exLatitude != 0) {
                        view2.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.INVISIBLE);
                    } else
                        Toast.makeText(getApplicationContext(), R.string.noshow, Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getApplicationContext(),R.string.prosvp,Toast.LENGTH_SHORT).show();
            }
        });*/

        userId = getIntent().getStringExtra("userId");
        //SharedPreferences sharedPreferencesMode=getSharedPreferences("mode", Activity.MODE_PRIVATE);
         //modeAuto=sharedPreferencesMode.getString("choix","0");
        //globalClass=GlobalClass.getInstance();
        /*if(modeAuto.equals("1")){
            bolStart=true;
            start.setVisibility(View.INVISIBLE);
            startDes.setVisibility(View.INVISIBLE);
            saveBtn.setVisibility(View.VISIBLE);
            finishDes.setVisibility(View.VISIBLE);
        }*/

        //modeVeille=false;
        vin=tripRecordVin.getmVehicleIdentificationNumber();
        SharedPreferences sharedPreferencesCompte=getSharedPreferences("Compte", Activity.MODE_PRIVATE);
        typeCompte=sharedPreferencesCompte.getString("Type","pro");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Now need to get a sensor type
        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // Now we need to register a listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sensorManager.registerListener(SampleActivity.this, accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
            if(gyroscope != null)
            sensorManager.registerListener(SampleActivity.this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
        }

        /**
         *  configure obd: add required command in arrayList and set to ObdConfiguration.
         *  If you dont set any command or passing null, then all command OBD command will be requested.
         *  Therefore, it is recommended to set command that is required only like belows commented line.         *
         */

     /*   ArrayList<ObdCommand> obdCommands = new ArrayList<>();
        obdCommands.add(new SpeedCommand());
        obdCommands.add(new RPMCommand());
        ObdConfiguration.setmObdCommands(this, obdCommands);*/

        // passing null means we are executing all OBD command for now, but you should add required command for fast retrieval like above commented lines.
        ObdConfiguration.setmObdCommands(this, null);

        // set gas price per litre so that gas cost can calculated. Default is 7 $/l
        float gasPrice = 7; // per litre, you should initialize according to your requirement.
        ObdPreferences.get(this).setGasPrice(gasPrice);
        /**
         * Register receiver with some action related to OBD connection status
         */
        /*StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader inn = null;
        String privateDcimDirPathh = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File filee=new File(privateDcimDirPathh, "dataUser.txt");
        if(filee.exists()) {
            try {
                inn = new BufferedReader(new FileReader(filee));
                while ((line = inn.readLine()) != null) {

                    String[] arrayString = line.split("/");
                    gps = arrayString[1];
                    obd = arrayString[2];
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        SharedPreferences sharedPreferencesParam=getSharedPreferences("param", Activity.MODE_PRIVATE);
        gps=sharedPreferencesParam.getString("gps","1");
        obd=sharedPreferencesParam.getString("obd","0");
        //Log.i("kkkk",gps+"---"+obd);
        if(obd.equals("0")){
            mObdInfoTextView.setVisibility(View.INVISIBLE);
            mObdInfoTextView_des.setVisibility(View.INVISIBLE);
            mObdDataTextView.setVisibility(View.INVISIBLE);
            digitSpeedView.setVisibility(View.VISIBLE);
        }
        if(gps.equals("0")){
            locView.setVisibility(View.INVISIBLE);
            locView_des.setVisibility(View.INVISIBLE);
        }
        IntentFilter intentFilterGps = new IntentFilter();
        intentFilterGps.addAction("location_update");
        intentFilterGps.addAction(ACTION_OBD_CONNECTION_STATUS);
        registerReceiver(mGpsReaderReceiver, intentFilterGps);
        existGps=true;
        //start service which will execute in background for connecting and execute command until you stop
        if(! isMyServiceRunning(GPS_Service.class))
            startService(new Intent(this, GPS_Service.class));
        if(!isMyServiceRunning(ObdReaderService.class))
            startService(new Intent(this, ObdReaderService.class));
        Notificationplus.showNotificatioon(getApplicationContext(),"Drive&Win","");

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        boolean isWiFi = nInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    private void showAuth(String text, String title) {
        //Setting message manually and performing action on button click
        builder.setMessage(text).setCancelable(false).setPositiveButton(R.string.valider, new DialogInterface.OnClickListener() {
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
        alert.setTitle(title);
        alert.show();
    }

    /*public void setModeVeille(Boolean modeVeille){
        this.modeVeille=modeVeille;
    }*/
    /**
     * Broadcast Receiver to receive OBD connection status and real time data
     */
    public final BroadcastReceiver mObdReaderReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            existObd=true;
            if (action.equals(ACTION_READ_OBD_REAL_TIME_DATA) && bolStart==true) {
                if(obd.equals("1")) {
                    if (timeOut == true ) {
                        TripRecord tripRecord = TripRecord.getTripRecode(SampleActivity.this);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                connexion=isOnline();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!connexion){ }
                                        else{
                                            TripRecord tripRecord = TripRecord.getTripRecode(SampleActivity.this);
                                            if(liveTrip && tripRecord.getmVehicleIdentificationNumber()!= null){
                                                JSONObject params=new JSONObject();
                                                try {
                                                    params.put("date_start",getDateTime()+"");
                                                    params.put("vin",tripRecord.getmVehicleIdentificationNumber());
                                                    params.put("user_id",userId);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                final String data=params.toString();
                                                final String url=Url.url_api+"/addlivetrip";
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Http http=new Http(SampleActivity.this,url);
                                                        http.setMethod("POST");
                                                        http.setToken(true);
                                                        http.setData(data);
                                                        http.sendData(token_key);
                                                    }
                                                }).start();
                                                liveTrip=false;
                                            }
                                        }
                                    }
                                });
                            }
                        }).start();
                        //if (acces.equals("0"))
                        mObdDataTextView.setText(tripRecord.toString());
                        dataOBD.add(String.valueOf(latitude) + "");
                        dataOBD.add(String.valueOf(longitude) + "");
                        dataOBD.add(String.valueOf(tripRecord.getSpeed() + " "));
                        dataOBD.add(tripRecord.getEngineRpm() + " ");
                        dataOBD.add(tripRecord.getmEngineLoad() + " ");
                        dataOBD.add(tripRecord.getmAmbientAirTemp() + " ");
                        dataOBD.add(tripRecord.getmThrottlePos()+"");
                        dataOBD.add(tripRecord.getmInsFuelConsumption()+"");
                        dataOBD.add(null);
                        dataOBD.add(getDateTime()+"");
                        dataOBD.add(getLocationNom(latitude,longitude)+"");
                        //dataOBD.add(sensorData);

                        //if (acces.equals("0")) {
                            if (tripRecord.getSpeed() != null)
                                //speed.speedTo(tripRecord.getSpeed(), 1000);
                            if (tripRecord.getmEngineLoad() != null)
                                //engime.speedTo((Integer.parseInt(tripRecord.getEngineRpm())) / 100, 1000);
                            if (tripRecord.getmAmbientAirTemp() != null)
                                //temp.speedTo(Integer.parseInt(removeLastChar(tripRecord.getmAmbientAirTemp())), 1000);
                            distanceDes.setText(tripRecord.getmDistanceTravel() +"  "+ "Km");
                            timeDes.setText(tripRecord.getEngineRuntime() + "");
                        //}

                        sumLoc+=getLocation(latitude,longitude);
                        sumSpeed+=tripRecord.getSpeed();
                        cptSpeed++;
                        if(cptSpeed==10){
                            if((sumSpeed/10)-(getNearVal(sumLoc/10)) >20){
                                NotificationHelper.showNotificatioon(context, "Attention ", "Dépassement de vitesse légale de plus 20km/h");
                            }
                            cptSpeed=0;sumSpeed=0;sumLoc=0;
                        }
                        //if (acces.equals("0")){
                        if(gps.equals("1")){

                        if(exLatitude!=0 ){
                            if(firstPos){
                                markerStart=mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(exLatitude,exLongitude)).title("Start"));
                                markerStart.showInfoWindow();
                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(exLatitude,exLongitude))
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.carr)));
                                firstPos=false;
                            }
                            else{
                                marker.setPosition(new LatLng(latitude,longitude));
                            }

                            mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(exLatitude,exLongitude),new LatLng(latitude,longitude))
                                    .width(10)
                                    .color(Color.BLACK)
                                    .startCap(new SquareCap())
                                    .endCap(new SquareCap())
                                    .jointType(ROUND));



                            mMap.moveCamera(CameraUpdateFactory
                                    .newCameraPosition
                                            (new CameraPosition.Builder().
                                                    zoom(mMap.getCameraPosition().zoom)
                                                    .target(new LatLng(exLatitude,exLongitude))
                                                    .build()));
                        }

                        exLatitude=latitude;
                        exLongitude=longitude;
                      }
                        //}

                        if (isOnline()) {
                            if (cpt != 0) {
                                //NotificationHelper.showNotificatioon(context, "Attention ", "connexion rétablit");
                                cpt = 0;
                            }
                            addTripData(dataOBD.get(0),dataOBD.get(1),dataOBD.get(2),dataOBD.get(3),
                                    dataOBD.get(4),dataOBD.get(5),dataOBD.get(6),dataOBD.get(7),
                                    dataOBD.get(10),dataOBD.get(8));
                            dataOBD.clear();
                            sensorData="";
                            /*if(sensorData.size()<400)
                                fullSensorData.add(sensorData);
                            sensorData.clear();*/
                        } else {
                            if (cpt == 0) {
                                //NotificationHelper.showNotificatioon(context, "Attention ", "Pas de connexion! Stockage local activé");
                            }
                            LocalStorage(dataOBD.get(0) + "/");
                            LocalStorage(dataOBD.get(1) + "/");
                            LocalStorage(dataOBD.get(2) + "/");
                            LocalStorage(dataOBD.get(3) + "/");
                            LocalStorage(dataOBD.get(4) + "/");
                            LocalStorage(dataOBD.get(5) + "/");
                            LocalStorage(dataOBD.get(6) + "/");
                            LocalStorage(dataOBD.get(7) + "/");
                            LocalStorage(dataOBD.get(8) + "/");
                            LocalStorage(dataOBD.get(9) + "/");
                            LocalStorage(dataOBD.get(10) + "\n");
                            cpt++;
                            dataOBD.clear();
                            sensorData="";
                            /*if(sensorData.size()<400)
                                fullSensorData.add(sensorData);
                            sensorData.clear();*/
                        }
                        startTimer();
                    }
                }

                context.unregisterReceiver(mObdReaderReceiver);
                existObd=false;
                IntentFilter intentFilterGps = new IntentFilter();
                intentFilterGps.addAction("location_update");
                intentFilterGps.addAction(ACTION_OBD_CONNECTION_STATUS);
                context.registerReceiver(mGpsReaderReceiver, intentFilterGps);
                existGps=true;
            }

        }

    };

    public final BroadcastReceiver mGpsReaderReceiver=new BroadcastReceiver() {
        String connectionStatusMsg = "";

        public void onReceive(Context context, Intent intent) {
            //SharedPreferences sharedPreferencesAcces=context.getSharedPreferences("modePlus", Activity.MODE_PRIVATE);
            //String acces =sharedPreferencesAcces.getString("acces","0");
            String action = intent.getAction();

            if (action.equals(ACTION_OBD_CONNECTION_STATUS) && bolStart ==true) {
                connectionStatusMsg = intent.getStringExtra(ObdReaderService.INTENT_OBD_EXTRA_DATA);
                //if(acces.equals("0")){
                    //mObdInfoTextView.setText(connectionStatusMsg);
                    //cnxDes.setText(connectionStatusMsg);
                    switch (connectionStatusMsg){
                        case "OBD Connected":
                            mObdInfoTextView.setBackgroundResource(R.drawable.green);
                            mObdInfoTextView_des.setBackgroundResource(R.drawable.green);
                            //sensorData="";
                            //cnxDes.setBackgroundColor(Color.GREEN);
                            break;
                        case "Connect Lost":
                            mObdInfoTextView.setBackgroundResource(R.drawable.red);
                            mObdInfoTextView_des.setBackgroundResource(R.drawable.red);
                            //cnxDes.setBackgroundColor(Color.RED);
                            break;
                        default:
                            mObdInfoTextView.setBackgroundResource(R.drawable.orange);
                            mObdInfoTextView_des.setBackgroundResource(R.drawable.orange);
                            //cnxDes.setBackgroundColor(Color.rgb(255,127,0));
                            break;
                    }
                //}
            }
              else if (action.equals("location_update") && bolStart==true) {

                if (gps.equals("1")){
                    longitude = (double) intent.getExtras().get("longitude");
                    latitude = (double) intent.getExtras().get("latitude");
                    timeSpeed= (String) intent.getExtras().get("time");
                    String loc=(getLocationNom(latitude,longitude)+"");
                    //if (acces.equals("0")){
                    switch (loc){
                        case "Urbaine":limitMap.setText("50");limitDes.setText("50");limit.setText("50");break;
                        case "Nationale":limitMap.setText("90");limitDes.setText("90");limit.setText("90");break;
                        case "Autoroute":limitMap.setText("110");limitDes.setText("110");limit.setText("110");break;
                    }
                    //}
                    }
                    //code= (String) getIntent().getStringExtra("cin");
                    time= (String) intent.getExtras().get("time");
                    if(/*acces.equals("0") &&*/gps.equals("1")){
                        if(longitude != 0){
                            locView.setBackgroundResource(R.drawable.green);
                            locView_des.setBackgroundResource(R.drawable.green);
                            //gpsDes.setBackgroundColor(Color.GREEN);
                            }
                        //loc.setText("longitude : ="+longitude+"--latitude : ="+latitude);
                        //latDes.setText(latitude+"");
                        //longDes.setText(longitude+"");
                    }
                    if((!connectionStatusMsg.equals("OBD Connected")) || (obd.equals("0") && gps.equals("1")) ){
                        if (timeOut==true){
                            //if (acces.equals("0")){
                            if(exLatitude!=0 ){
                                if(firstPos){
                                    markerStart=mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(exLatitude,exLongitude)).title("Start"));
                                    markerStart.showInfoWindow();
                                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(exLatitude,exLongitude))
                                            .flat(true)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.carr)));
                                    firstPos=false;
                                }
                                else{
                                    marker.setPosition(new LatLng(latitude,longitude));
                                }

                            mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(exLatitude,exLongitude),new LatLng(latitude,longitude))
                                    .width(10)
                                    .color(Color.BLACK)
                                    .startCap(new SquareCap())
                                    .endCap(new SquareCap())
                                    .jointType(ROUND));



                            mMap.moveCamera(CameraUpdateFactory
                                    .newCameraPosition
                                            (new CameraPosition.Builder().
                                                    zoom(mMap.getCameraPosition().zoom)
                                                    .target(new LatLng(exLatitude,exLongitude))
                                                    .build()));
                                String dateLoc1=exTimeSpeed;
                                String dateLoc2=timeSpeed;
                                String[] fulldate1 = dateLoc1.split(" ");
                                String[] fulldate2 = dateLoc2.split(" ");
                                String date1=fulldate1[0];
                                String time1=fulldate1[1];
                                String date2=fulldate2[0];
                                String time2=fulldate2[1];
                                String[] decdate1 = date1.split("-");
                                String[] dectime1 = time1.split(":");
                                String[] decdate2 = date2.split("-");
                                String[] dectime2 = time2.split(":");
                                int year1= Integer.parseInt(decdate1[0]);
                                int mounth1= Integer.parseInt(decdate1[1]);
                                int day1= Integer.parseInt(decdate1[2]);
                                int hour1= Integer.parseInt(dectime1[0]);
                                int minute1= Integer.parseInt(dectime1[1]);
                                int seconde1= Integer.parseInt(dectime1[2]);
                                int year2= Integer.parseInt(decdate2[0]);
                                int mounth2= Integer.parseInt(decdate2[1]);
                                int day2= Integer.parseInt(decdate2[2]);
                                int hour2= Integer.parseInt(dectime2[0]);
                                int minute2= Integer.parseInt(dectime2[1]);
                                int seconde2= Integer.parseInt(dectime2[2]);

                                double dure=dateDifferenceExample(year1,mounth1,day1,hour1,minute1,seconde1,year2,mounth2,day2,hour2,minute2,seconde2);
                                double distance=getDistance(exLatitude,exLongitude,latitude,longitude);
                                Integer vitesse= (int) ((distance*3600)/dure);
                                digitSpeedView.setText(vitesse+" KM");
                            }
                            exLatitude=latitude;
                            exLongitude=longitude;
                            exTimeSpeed=timeSpeed;
                               // }
                        if(isOnline()) {
                            if(cpt != 0){
                                //NotificationHelper.showNotificatioon(context,"Attention ","connexion rétablit");
                                cpt=0;}
                            addTripData(latitude+"",longitude+"",null,null,null,
                                    null,null,null,getLocationNom(latitude,longitude),null);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    localStorageSensor(sensorData+"\n");
                                    sensorData="";
                                }
                            }).start();

                            /*if(sensorData.size()<40){
                                fullSensorData.add(sensorData);
                                sensorData.clear();
                            }*/
                        }
                        else{
                            if(cpt==0){
                                //NotificationHelper.showNotificatioon(context,"Attention ","Pas de connexion! Stockage local activé");
                            }
                            LocalStorage(latitude+"/");
                            LocalStorage(longitude+"/");
                            LocalStorage(null+"/");
                            LocalStorage(null+"/");
                            LocalStorage(null+"/");
                            LocalStorage(null+"/");
                            LocalStorage(null+"/");
                            LocalStorage(null+"/");
                            LocalStorage(null+"/");
                            LocalStorage(getDateTime()+"/");
                            LocalStorage(getLocationNom(latitude,longitude)+"\n");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    localStorageSensor(sensorData+"\n");
                                    sensorData="";
                                }
                            });

                            /*if(sensorData.size()<40)
                                fullSensorData.add(sensorData);
                            sensorData.clear();*/
                            cpt++;
                        }
                            startTimer();
                    }

                }
                    else{
                        context.unregisterReceiver(mGpsReaderReceiver);
                        existGps=false;
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
                        context.registerReceiver(mObdReaderReceiver,intentFilter);
                        existObd=true;
                    }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!liveTrip)
            deleteLiveTrip();
        sensorManager.unregisterListener(this);
        FileReader fr = null;
        File myExternalFile = new File(getExternalFilesDir("drivewin"), "tripdata.txt");
        if (myExternalFile.exists()) {
            myExternalFile.delete();
        }
        //unregister receiver
        if(existObd)
        unregisterReceiver(mObdReaderReceiver);
        if(existGps)
        unregisterReceiver(mGpsReaderReceiver);
        //stop service
        stopService(new Intent(this, ObdReaderService.class));
        stopService(new Intent(this, GPS_Service.class));
        // This will stop background thread if any running immediately.
        ObdPreferences.get(this).setServiceRunningStatus(false);
        Notificationplus.remove(getApplicationContext());
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            final float alpha = 0.8f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

            gravX=gravity[0];
            gravY=gravity[1];
            gravZ=gravity[2];

            linear_acceleration[0] = sensorEvent.values[0] - gravity[0];
            linear_acceleration[1] = sensorEvent.values[1] - gravity[1];
            linear_acceleration[2] = sensorEvent.values[2] - gravity[2];

            accX=linear_acceleration[0];
            accY=linear_acceleration[1];
            accZ=linear_acceleration[2];
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyrox=Math.toDegrees(sensorEvent.values[0]);
            gyroy=Math.toDegrees(sensorEvent.values[1]);
            gyroz=Math.toDegrees(sensorEvent.values[2]);
        }
        if(bolStart){
            if(timeOutSensor){
                sensorData=sensorData.concat(gravX+"/"+gravY+"/"+gravZ+"/"+accX+"/"+accY+"/"+accZ+"/"+gyrox+"/"+gyroy+"/"+gyroz+"-");
                startTimerSensor();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
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
    public void showAlertProfil(String text,String title){
        //Setting message manually and performing action on button click
        if(bolStart){
        builder.setMessage((Html.fromHtml("<font color="+color+">"+text+"</font>")))
                .setCancelable(false)
                .setPositiveButton(Html.fromHtml("<font color=#4D7D8D>"+
                        getResources().getString(R.string.yes)+"</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TripRecord tripRecord = TripRecord.getTripRecode(SampleActivity.this);
                        SharedPreferences sharedPreferences=getSharedPreferences("start", Activity.MODE_PRIVATE);
                        String dateStart=sharedPreferences.getString("datestart","");
                        addTrip(dateStart,tripRecord.getmVehicleIdentificationNumber()+"","profil");
                    }
                }).setNegativeButton(Html.fromHtml("<font color=#4D7D8D>"+
                getResources().getString(R.string.no)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!liveTrip)
                    deleteLiveTrip();
                FileReader fr = null;
                File myExternalFile = new File(getExternalFilesDir("drivewin"), "tripdata.txt");
                if (myExternalFile.exists()) {
                    synchroniser();
                }
                Intent inte=new Intent(SampleActivity.this,receive.class);
                inte.putExtra("userId",userId);
                startActivity(inte);
                finish();
            }
        });

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+title+"</font>"));
        alert.show();}
        else{
            Intent inte=new Intent(SampleActivity.this,receive.class);
            inte.putExtra("userId",userId);
            startActivity(inte);
            finish();
        }

    }
    public void showAlertResult(String text,String title){
        //Setting message manually and performing action on button click
        builder.setMessage(Html.fromHtml("<font color="+color+">"+text+"</font>"))
                .setCancelable(false)
                .setPositiveButton(Html.fromHtml("<font color=#4D7D8D>"+
                        getResources().getString(R.string.yes)+"</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPreferences=getSharedPreferences("start", Activity.MODE_PRIVATE);
                        String dateStart=sharedPreferences.getString("datestart","");
                        TripRecord tripRecord = TripRecord.getTripRecode(SampleActivity.this);
                        addTrip(dateStart,tripRecord.getmVehicleIdentificationNumber()+"","result");

                    }
                }).setNegativeButton(Html.fromHtml("<font color=#4D7D8D>"+
                getResources().getString(R.string.no)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+title+"</font>"));
        alert.show();

    }
    public void showAlertLougout(String text,String title){
        //Setting message manually and performing action on button click
        if(bolStart){
        builder.setMessage(Html.fromHtml("<font color="+color+">"+text+"</font>"))
                .setCancelable(false)
                .setPositiveButton(Html.fromHtml("<font color=#4D7D8D>"+
                        getResources().getString(R.string.yes)+"</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPreferences=getSharedPreferences("start", Activity.MODE_PRIVATE);
                        String dateStart=sharedPreferences.getString("datestart","");
                        TripRecord tripRecord = TripRecord.getTripRecode(SampleActivity.this);
                        addTrip(dateStart,tripRecord.getmVehicleIdentificationNumber()+"","logout");
                    }
                }).setNegativeButton(Html.fromHtml("<font color=#4D7D8D>"+
                getResources().getString(R.string.no)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!liveTrip)
                    deleteLiveTrip();
                FileReader fr = null;
                File myExternalFile = new File(getExternalFilesDir("drivewin"), "tripdata.txt");
                if (myExternalFile.exists()) {
                    myExternalFile.delete();
                }
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
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else {
                                    SharedPreferences sharedPreferencesLogin=getSharedPreferences("login", MODE_PRIVATE);
                                    final String token_key = sharedPreferencesLogin.getString("token", "");
                                    final String url=Url.url_api+"/logout";
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final Http http=new Http(SampleActivity.this,url);
                                            http.setToken(true);
                                            http.setMethod("POST");
                                            http.sendData(token_key);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    SharedPreferences preferences = getSharedPreferences("login", 0);
                                                    preferences.edit().remove("userId").commit();
                                                    Intent i=new Intent(SampleActivity.this,login.class);
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

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+title+"</font>"));
        alert.show();}
        else{
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
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            if(!connexion){
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                            }
                            else {
                                SharedPreferences sharedPreferencesLogin=getSharedPreferences("login", MODE_PRIVATE);
                                final String token_key = sharedPreferencesLogin.getString("token", "");
                                final String url=Url.url_api+"/logout";
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Http http=new Http(SampleActivity.this,url);
                                        http.setToken(true);
                                        http.setMethod("POST");
                                        http.sendData(token_key);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SharedPreferences preferences = getSharedPreferences("login", 0);
                                                preferences.edit().remove("userId").commit();
                                                Intent i=new Intent(SampleActivity.this,login.class);
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

    }
    public void showAlertController(String text,String title){
        //Setting message manually and performing action on button click
        if(bolStart) {
            builder.setMessage(Html.fromHtml("<font color="+color+">"+text+"</font>"))
                    .setCancelable(false)
                    .setPositiveButton(Html.fromHtml("<font color=#4D7D8D>"+
                            getResources().getString(R.string.yes)+"</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences sharedPreferences = getSharedPreferences("start", Activity.MODE_PRIVATE);
                            String dateStart = sharedPreferences.getString("datestart", "");
                            TripRecord tripRecord = TripRecord.getTripRecode(SampleActivity.this);
                            addTrip(dateStart,tripRecord.getmVehicleIdentificationNumber()+"","controller");
                        }
                    }).setNegativeButton(Html.fromHtml("<font color=#4D7D8D>"+
                    getResources().getString(R.string.no)+"</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(!liveTrip)
                        deleteLiveTrip();
                    FileReader fr = null;
                    File myExternalFile = new File(getExternalFilesDir("drivewin"), "tripdata.txt");
                    if (myExternalFile.exists()) {
                        myExternalFile.delete();
                    }
                    Intent ii = new Intent(SampleActivity.this, CarList.class);
                    ii.putExtra("userId", userId);
                    startActivity(ii);
                    finish();
                }
            });

            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(true);
            //Setting the title manually
            alert.setTitle(Html.fromHtml("<font color="+color+">"+title+"</font>"));
            alert.show();
        }
        else{
            Intent ii = new Intent(SampleActivity.this, CarList.class);
            ii.putExtra("userId", userId);
            startActivity(ii);
            finish();
        }

    }
    public void showAlertSet(String text,String title){
        //Setting message manually and performing action on button click
        if(bolStart) {
            builder.setMessage(Html.fromHtml("<font color="+color+">"+text+"</font>"))
                    .setCancelable(false)
                    .setPositiveButton(Html.fromHtml("<font color=#4D7D8D>"+
                            getResources().getString(R.string.yes)+"</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences sharedPreferences = getSharedPreferences("start", Activity.MODE_PRIVATE);
                            String dateStart = sharedPreferences.getString("datestart", "");
                            TripRecord tripRecord = TripRecord.getTripRecode(SampleActivity.this);
                            addTrip(dateStart,tripRecord.getmVehicleIdentificationNumber()+"","settings");
                        }
                    }).setNegativeButton(Html.fromHtml("<font color=#4D7D8D>"+
                    getResources().getString(R.string.no)+"</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(!liveTrip)
                        deleteLiveTrip();
                    FileReader fr = null;
                    File myExternalFile = new File(getExternalFilesDir("drivewin"), "tripdata.txt");
                    if (myExternalFile.exists()) {
                        myExternalFile.delete();
                    }
                    Intent ii = new Intent(SampleActivity.this, Parametres.class);
                    ii.putExtra("userId", userId);
                    startActivity(ii);
                    finish();
                }
            });

            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(true);
            //Setting the title manually
            alert.setTitle(Html.fromHtml("<font color="+color+">"+title+"</font>"));
            alert.show();
        }
        else{
            Intent ii = new Intent(SampleActivity.this, Parametres.class);
            ii.putExtra("userId", userId);
            startActivity(ii);
            finish();
        }

    }
    public void showAlertHisto(String text,String title){
        //Setting message manually and performing action on button click
        if(bolStart) {
            builder.setMessage(Html.fromHtml("<font color="+color+">"+text+"</font>"))
                    .setCancelable(false)
                    .setPositiveButton(Html.fromHtml("<font color=#4D7D8D>"+
                            getResources().getString(R.string.yes)+"</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences sharedPreferences = getSharedPreferences("start", Activity.MODE_PRIVATE);
                            String dateStart = sharedPreferences.getString("datestart", "");
                            TripRecord tripRecord = TripRecord.getTripRecode(SampleActivity.this);
                            addTrip(dateStart,tripRecord.getmVehicleIdentificationNumber()+"","historique");
                        }
                    }).setNegativeButton(Html.fromHtml("<font color=#4D7D8D>"+
                    getResources().getString(R.string.no)+"</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(!liveTrip)
                        deleteLiveTrip();
                    FileReader fr = null;
                    File myExternalFile = new File(getExternalFilesDir("drivewin"), "tripdata.txt");
                    if (myExternalFile.exists()) {
                        myExternalFile.delete();
                    }
                    Intent ii = new Intent(SampleActivity.this, LocationSearch.class);
                    ii.putExtra("userId", userId);
                    startActivity(ii);
                    finish();
                }
            });

            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(true);
            //Setting the title manually
            alert.setTitle(Html.fromHtml("<font color="+color+">"+title+"</font>"));
            alert.show();
        }
        else{
            Intent ii = new Intent(SampleActivity.this, LocationSearch.class);
            ii.putExtra("userId", userId);
            startActivity(ii);
            finish();
        }

    }
    public void showAlertLastTrip(String text,String title){
        //Setting message manually and performing action on button click
        if(bolStart) {
            builder.setMessage(Html.fromHtml("<font color="+color+">"+text+"</font>"))
                    .setCancelable(false)
                    .setPositiveButton(Html.fromHtml("<font color=#4D7D8D>"+
                            getResources().getString(R.string.yes)+"</font>"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences sharedPreferences = getSharedPreferences("start", Activity.MODE_PRIVATE);
                            String dateStart = sharedPreferences.getString("datestart", "");
                            TripRecord tripRecord = TripRecord.getTripRecode(SampleActivity.this);
                            addTrip(dateStart,tripRecord.getmVehicleIdentificationNumber()+"","lasttrip");
                        }
                    }).setNegativeButton(Html.fromHtml("<font color=#4D7D8D>"+
                    getResources().getString(R.string.no)+"</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(!liveTrip)
                        deleteLiveTrip();
                    FileReader fr = null;
                    File myExternalFile = new File(getExternalFilesDir("drivewin"), "tripdata.txt");
                    if (myExternalFile.exists()) {
                        myExternalFile.delete();
                    }
                    lastTrip();
                }
            });

            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(true);
            //Setting the title manually
            alert.setTitle(Html.fromHtml("<font color="+color+">"+title+"</font>"));
            alert.show();
        }
        else{
            lastTrip();
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
                                        final String url=Url.url_api+"/position/"+userId;
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                final Http http=new Http(SampleActivity.this,url);
                                                http.setMethod("GET");
                                                http.setData(null);
                                                http.sendData(null);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        simpleProgressBar.setVisibility(View.INVISIBLE);
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
                                                                        Intent intent = new Intent(SampleActivity.this, Location.class);
                                                                        intent.putExtra("tableauLatitude", pointX);
                                                                        intent.putExtra("tableauLongitude", pointY);
                                                                        intent.putExtra("tableauTime", timeTab);
                                                                        intent.putExtra("tableauLocation", locationTab);
                                                                        intent.putExtra("ref", "home");
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
                                                            simpleProgressBar.setVisibility(View.INVISIBLE);
                                                            bottomNavigationViewDes.setSelectedItemId(R.id.nav_new);
                                                            bottomNavigationView.setSelectedItemId(R.id.nav_new);
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

    public void LocalStorage(String data){

        File myExternalFile = new File(getExternalFilesDir("drivewin"), "tripdata.txt");

        FileOutputStream fos = null;
        try {
            // Instantiate the FileOutputStream object and pass myExternalFile in constructor
            fos = new FileOutputStream(myExternalFile,true);
            // Write to the file
            fos.write(data.getBytes());
            // Close the stream
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void localStorageSensor(String data){

        File myExternalFile = new File(getExternalFilesDir("sensorData"), "sensorData.txt");

        FileOutputStream fos = null;
        try {
            // Instantiate the FileOutputStream object and pass myExternalFile in constructor
            fos = new FileOutputStream(myExternalFile,true);
            // Write to the file
            fos.write(data.getBytes());
            // Close the stream
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void synchroniser() {
        simpleProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileReader fr = null;
                myExternalFile = new File(getExternalFilesDir("drivewin"), "tripdata.txt");
                if (myExternalFile.exists()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    try {
                        fr = new FileReader(myExternalFile);
                        BufferedReader br = new BufferedReader(fr);
                        String line = br.readLine();

                        while(line != null){
                            String[] arrayString = line.split("/");
                            dataOBDSync.add(arrayString[0]);
                            dataOBDSync.add(arrayString[1]);
                            dataOBDSync.add(arrayString[2]);
                            dataOBDSync.add(arrayString[3]);
                            dataOBDSync.add(arrayString[4]);
                            dataOBDSync.add(arrayString[5]);
                            dataOBDSync.add(arrayString[6]);
                            dataOBDSync.add(arrayString[7]);
                            dataOBDSync.add(arrayString[8]);
                            dataOBDSync.add(arrayString[9]);
                            dataOBDSync.add(arrayString[10]);

                            addTripDataSync(dataOBDSync.get(0),dataOBDSync.get(1),dataOBDSync.get(2),dataOBDSync.get(3),
                                    dataOBDSync.get(4),dataOBDSync.get(5),dataOBDSync.get(6),dataOBDSync.get(7),
                                    dataOBDSync.get(10),dataOBDSync.get(8),dataOBDSync.get(9));

                            dataOBDSync.clear();
                            line = br.readLine();
                        }
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        simpleProgressBar.setVisibility(View.INVISIBLE);
                        myExternalFile.delete();
                    }
                });
            }
        }).start();


        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
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
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                               if(!connexion){
                                   simpleProgressBar.setVisibility(View.INVISIBLE);
                                   NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                               }
                               else{
                                   showAlertLougout(getResources().getString(R.string.savetrip), getResources().getString(R.string.warning));
                               }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.profil:
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
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    showAlertProfil(getResources().getString(R.string.savetrip),getResources().getString(R.string.warning));
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.param:
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
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    showAlertSet(getResources().getString(R.string.savetrip),getResources().getString(R.string.warning));
                                }
                            }
                        });
                    }
                }).start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void startTimer() {
        timeOut=false;
        cTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                timeOut=true;
            }
        };
        cTimer.start();
    }

    void startTimerSensor() {
        timeOutSensor=false;
        cTimer = new CountDownTimer(100, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                timeOutSensor=true;
            }
        };
        cTimer.start();
    }

    public String getDateTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String datetime = dateformat.format(c.getTime());
        return datetime;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target)
                .zoom(17)
                .bearing(30)
                .tilt(45)
                .build()));

    }

    public String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1);
    }

    public int getLocation(double lat,double longi){
        int location = -1;
        String place = "";
        Locale locale = Locale.getDefault();
        Geocoder geocoder = new Geocoder(SampleActivity.this, locale);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, longi, 1);
            if (addresses.get(0).getFeatureName() != null)
                place = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (place.contains("A1") || place.contains("Autoroute") || place.contains("highway")|| place.contains("الطريق السيارة") || place.contains("أ1"))
            location = 110;
        else if ((place.startsWith("P") && place.length() < 4) ||
                (place.startsWith("p") && place.length() < 4) ||
                (place.startsWith("GP") && place.length() < 5) ||
                (place.startsWith("C") && place.length() < 5) ||
                (place.startsWith("N") && place.length() < 4)||
                (place.startsWith("Express Rocade"))||
                place.contains("Number"))
            location = 90;
        else
            location = 50;
        return location;
    }

    public String getLocationNom(double lat,double longi){
        String location ="";
        String place = "";
        Locale locale = Locale.getDefault();
        Geocoder geocoder = new Geocoder(SampleActivity.this, locale);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, longi, 1);
            if (addresses.get(0).getFeatureName() != null)
                place = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (place.contains("A1") || place.contains("Autoroute") || place.contains("highway")|| place.contains("الطريق السيارة") || place.contains("أ1"))
            location ="Autoroute";
        else if ((place.startsWith("P") && place.length() < 4) ||
                (place.startsWith("p") && place.length() < 4) ||
                (place.startsWith("GP") && place.length() < 5) ||
                (place.startsWith("C") && place.length() < 5) ||
                (place.startsWith("N") && place.length() < 4)||
                (place.startsWith("Express Rocade"))||
                place.contains("Number"))
            location ="Nationale";
        else
            location = "Urbaine";
        return location;
    }

    public float getNearVal(float val){
        float val1=0,val2=0,val3=0;
        float min;
        float res=0;
        val1=Math.abs(50-val);
        val2=Math.abs(90-val);
        val3=Math.abs(110-val);
        min=Math.min(val1,val2);
        min=Math.min(min,val3);
        if(min ==val1)
            res= 50;
        else if(min == val2)
            res= 90;
        else if (min ==val3)
            res= 110;
        return res;
    }

    private double getDistance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371;

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }

    public double dateDifferenceExample(int year1,int mounth1,int day1,int hour1,int minute1,int seconde1,
                                        int year2,int mounth2,int day2,int hour2,int minute2,int seconde2) {

        // Set the date for both of the calendar instance
        GregorianCalendar calDate = new GregorianCalendar(year1, mounth1, day1,hour1,minute1,seconde1);
        GregorianCalendar cal2 = new GregorianCalendar(year2, mounth2, day2,hour2,minute2,seconde2);

        // Get the represented date in milliseconds
        long millis1 = calDate.getTimeInMillis();
        long millis2 = cal2.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = millis2 - millis1;

        // Calculate difference in seconds
        long diffSeconds = diff / 1000;

        // Calculate difference in minutes
        long diffMinutes = diff / (60 * 1000);

        // Calculate difference in hours
        long diffHours = diff / (60 * 60 * 1000);

        // Calculate difference in days
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return diffSeconds;

    }

    @Override
    protected void onStop() {
        super.onStop();
        //this.finish();
    }

    void deleteLiveTrip(){
    final String url=Url.url_api+"/deletelivetrip/"+userId;
    new Thread(new Runnable() {
        @Override
        public void run() {
            final Http http=new Http(SampleActivity.this,url);
            http.setToken(true);
            http.setMethod("DELETE");
            http.sendData(token_key);
        }
    }).start();
    }

    void addTrip(final String date_start, final String vin, final String layout){

        final String url=Url.url_api+"/addtrip";
        JSONObject params=new JSONObject();
        try {
            params.put("date_start",date_start);
            params.put("date_end",getDateTime()+"");
            params.put("vin",vin);
            params.put("user_id",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String data=params.toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Http http=new Http(SampleActivity.this,url);
                http.setMethod("POST");
                http.setToken(true);
                http.setData(data);
                http.sendData(token_key);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(http.getResponse()!=null){
                                JSONObject response=new JSONObject(http.getResponse());
                                String status=response.getString("status");
                                if(status.equals("succes")){
                                    switch (layout){
                                        case "lasttrip":
                                            lastTrip();
                                            break;
                                        case "historique":
                                            Intent i = new Intent(SampleActivity.this, LocationSearch.class);
                                            i.putExtra("userId", userId);
                                            startActivity(i);
                                            finish();
                                            break;
                                        case "settings":
                                            Intent i1 = new Intent(SampleActivity.this, Parametres.class);
                                            i1.putExtra("userId", userId);
                                            startActivity(i1);
                                            finish();
                                            break;
                                        case "controller":
                                            Intent i2 = new Intent(SampleActivity.this, CarList.class);
                                            i2.putExtra("userId", userId);
                                            startActivity(i2);
                                            finish();
                                            break;
                                        case "logout":
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
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            if(!connexion){
                                                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                                                NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                                            }
                                                            else {
                                                                SharedPreferences sharedPreferencesLogin=getSharedPreferences("login", MODE_PRIVATE);
                                                                final String token_key = sharedPreferencesLogin.getString("token", "");
                                                                final String url=Url.url_api+"/logout";
                                                                new Thread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        final Http http=new Http(SampleActivity.this,url);
                                                                        http.setToken(true);
                                                                        http.setMethod("POST");
                                                                        http.sendData(token_key);
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                SharedPreferences preferences = getSharedPreferences("login", 0);
                                                                                preferences.edit().remove("userId").commit();
                                                                                Intent i=new Intent(SampleActivity.this,login.class);
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
                                        case "profil":
                                            Intent inte=new Intent(SampleActivity.this,receive.class);
                                            inte.putExtra("userId",userId);
                                            startActivity(inte);
                                            finish();
                                            break;
                                        case "result":
                                            recreate();
                                            break;
                                    }
                                    TripRecord tripRecord = TripRecord.getTripRecode(SampleActivity.this);
                                    if(!liveTrip)
                                        deleteLiveTrip();
                                    if(tripRecord.getmVehicleIdentificationNumber() != null) {
                                        SharedPreferences.Editor editorVin = getSharedPreferences("newCar", MODE_PRIVATE).edit();
                                        editorVin.putString("car", tripRecord.getmVehicleIdentificationNumber() + "");
                                        editorVin.apply();
                                    }
                                    FileReader fr = null;
                                    File myExternalFile = new File(getExternalFilesDir("drivewin"), "tripdata.txt");
                                    if (myExternalFile.exists()) {
                                        myExternalFile.delete();
                                    }
                                }
                                else {
                                    tryAgain(date_start,vin,layout);
                                }
                            }
                            else{
                                tryAgain(date_start,vin,layout);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    void addTripData(String lat,String longi,String speed,String eng_rpm,
                     String engine_load,String ambAir,String throt_pos,String instfuel,
                     String zone,String place){
        final String url=Url.url_api+"/addtripdata";
        JSONObject params=new JSONObject();
        try {
            params.put("latitude",lat);
            params.put("longitude",longi);
            params.put("speed",speed);
            params.put("user_id",userId);
            params.put("engine_rpm",eng_rpm);
            params.put("engine_load",engine_load);
            params.put("ambiant_temp",ambAir);
            params.put("throttle_pos",throt_pos);
            params.put("inst_fuel",instfuel);
            params.put("zone",zone);
            params.put("place",place);
            //params.put("sensor",sensor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String data=params.toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Http http=new Http(SampleActivity.this,url);
                http.setMethod("POST");
                http.setData(data);
                http.sendData(null);
            }
        }).start();
    }

    void addTripDataSync(String lat,String longi,String speed,String eng_rpm,
                     String engine_load,String ambAir,String throt_pos,String instfuel,
                     String zone,String place,String time){
        final String url=Url.url_api+"/addtripdata";
        JSONObject params=new JSONObject();
        try {
            params.put("latitude",lat);
            params.put("longitude",longi);
            params.put("speed",speed);
            params.put("user_id",userId);
            params.put("engine_rpm",eng_rpm);
            params.put("engine_load",engine_load);
            params.put("ambiant_temp",ambAir);
            params.put("throttle_pos",throt_pos);
            params.put("inst_fuel",instfuel);
            params.put("zone",zone);
            params.put("place",place);
            params.put("created_at",time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String data=params.toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Http http=new Http(SampleActivity.this,url);
                http.setMethod("POST");
                http.setToken(true);
                http.setData(data);
                http.sendData(token_key);
            }
        }).start();
    }

    public void bottomNavOption(int id){
        switch(id){
            case R.id.nav_new:
                break;
            case R.id.nav_cont:
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
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    showAlertController(getResources().getString(R.string.savetrip),getResources().getString(R.string.warning));
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.nav_histo:
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
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    showAlertHisto(getResources().getString(R.string.savetrip),getResources().getString(R.string.warning));
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.nav_last:
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
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                if(!connexion){
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                }
                                else{
                                    showAlertLastTrip(getResources().getString(R.string.savetrip),getResources().getString(R.string.warning));
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.nav_live:
                if(typeCompte.equals("pro")) {
                    if (exLatitude != 0) {
                        view2.setVisibility(View.VISIBLE);
                        view1.setVisibility(View.INVISIBLE);
                    } else{
                        Toast.makeText(getApplicationContext(), R.string.noshow, Toast.LENGTH_SHORT).show();
                        bottomNavigationView.setSelectedItemId(R.id.nav_new);
                    }
                }
                else Toast.makeText(getApplicationContext(),R.string.prosvp,Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        view2.setVisibility(View.INVISIBLE);
        view1.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.nav_new);
        //bottomNavigationViewDes.setSelectedItemId(R.id.nav_new);
    }

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        String value= (String) radioButton.getText();
        radioButton=findViewById(radioId);
        radioButton.setChecked(false);
        if(value.equals("Sport")){
            if(obd.equals("1")){
                view3.setVisibility(View.VISIBLE);
                view1.setVisibility(View.INVISIBLE);
            }
            else
                Toast.makeText(getApplicationContext(),"No OBD",Toast.LENGTH_LONG).show();
        }
        radioButton=findViewById(id_press);
        radioButton.setChecked(true);
    }

    public void checkButtonDes(View view) {
        int radioId = radioGroupDes.getCheckedRadioButtonId();
        radioButtonDes = findViewById(radioId);
        //Log.i("kkkk",radioId+"");
        //radioButtonDes.setChecked(false);
        radioButtonDes=findViewById(radioId);
        radioButtonDes.setChecked(false);
        String value= (String) radioButtonDes.getText();
        if(value.equals(getResources().getString(R.string.classic))){
            view1.setVisibility(View.VISIBLE);
            view3.setVisibility(View.INVISIBLE);
            radioButtonDes=findViewById(id_press_des);
            radioButtonDes.setChecked(true);
        }
    }

    public void tryAgain(final String dateStart, final String vin, final String layout){
        //Setting message manually and performing action on button click
        builderTry.setMessage(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.requested_data_could_not_be_loaded)+"</font>")).
                setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#4D7D8D'>"+
                getResources().getString(R.string.try_again)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addTrip(dateStart,vin,layout);
            }
        });
        AlertDialog alert = builderTry.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
    }

    public void tryAgainLastTrip(){
        builderTry.setMessage(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.requested_data_could_not_be_loaded)+"</font>")).
                setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#4D7D8D'>"+
                getResources().getString(R.string.try_again)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lastTrip();
            }
        });
        AlertDialog alert = builderTry.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
    }
}