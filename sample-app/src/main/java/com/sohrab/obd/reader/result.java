package com.sohrab.obd.reader;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.Task;




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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executor;

import im.dacer.androidcharts.BarView;
import im.dacer.androidcharts.LineView;
import im.dacer.androidcharts.PieHelper;
import im.dacer.androidcharts.PieView;




public class result extends AppCompatActivity {



    String cin;
    Toolbar toolbar;
    ArrayList<Double> altitude=new ArrayList<>();
    ArrayList<Double> longitude=new ArrayList<>();
    ArrayList<String> timeTab=new ArrayList<>();
    ArrayList<String> speedTab=new ArrayList<>();
    ArrayList<String> locationTab=new ArrayList<>();
    ArrayList<Integer> vitesseTab=new ArrayList<>();
    ArrayList<String> engineRpmTab=new ArrayList<>();
    ArrayList<String> engineLoadTab=new ArrayList<>();
    ArrayList<String> throttlePosTab=new ArrayList<>();
    ProgressBar progressBar;
    BarView barview;

    private LayoutInflater inflater;
    View viewEvaluation,viewSpeed,viewEngineRpm,viewEngineLoad,viewTrottlePos,viewInf;

    //ViewInfraction
    Button backInf;
    //BarView barView;
    //ArrayList<PieHelper> pieHelperArrayList;
    //ViewEvaluation
    Button share,details;
    PieView pieView;
    TextView faible,moyenne,forte;
    ArrayList<PieHelper> pieHelperArrayList;
    //ViewSpeed
    Button back,next;
    LineView lineViewSpeed;
    ArrayList<String> strList = new ArrayList<>();
    ArrayList<ArrayList<Float>> floatDataLists = new ArrayList<>();
    ArrayList<Float> listSpeed = new ArrayList<>();
    ArrayList<Float> listSpeed2 = new ArrayList<>();
    //ViewRpmEngine
    Button back_Engine_Rpm,next_Engine_Rpm;
    LineView lineViewEngineRpm;
    ArrayList<String> strList_Engine_Rpm = new ArrayList<>();
    ArrayList<ArrayList<Float>> floatDataListsEngineRpm = new ArrayList<>();
    ArrayList<Float> listEngineRpm = new ArrayList<>();
    //ViewRpmLoad
    Button back_Engine_Load,next_Engine_Load;
    LineView lineViewEngineLoad;
    ArrayList<String> strList_Engine_Load = new ArrayList<>();
    ArrayList<ArrayList<Float>> floatDataListsEngineLoad = new ArrayList<>();
    ArrayList<Float> listEngineLoad = new ArrayList<>();
    //ViewThrottlePos
    Button back_Throttle_Pos,next_Throttle_Pos;
    LineView lineViewThrottlePos;
    ArrayList<String> strList_Throttle_Pos = new ArrayList<>();
    ArrayList<ArrayList<Float>> floatDataListsThrottlePos = new ArrayList<>();
    ArrayList<Float> listThrottlePos = new ArrayList<>();
    String evalDate,evalTime,evalReponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.eva);
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        WindowManager.LayoutParams default_layout_params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        viewEvaluation = inflater.inflate(R.layout.eva, null);
        addContentView(viewEvaluation, default_layout_params);
        viewSpeed = inflater.inflate(R.layout.graph, null);
        addContentView(viewSpeed, default_layout_params);
        viewEngineRpm = inflater.inflate(R.layout.graph_engine_rpm, null);
        addContentView(viewEngineRpm, default_layout_params);
        viewEngineLoad = inflater.inflate(R.layout.graph_engine_load, null);
        addContentView(viewEngineLoad, default_layout_params);
        viewTrottlePos = inflater.inflate(R.layout.graph_throttle_pos, null);
        addContentView(viewTrottlePos, default_layout_params);
        viewInf = inflater.inflate(R.layout.infractions, null);
        addContentView(viewInf, default_layout_params);
        viewSpeed.setVisibility(View.INVISIBLE);
        viewEngineRpm.setVisibility(View.INVISIBLE);
        viewEngineLoad.setVisibility(View.INVISIBLE);
        viewTrottlePos.setVisibility(View.INVISIBLE);
        viewInf.setVisibility(View.INVISIBLE);
        viewEvaluation.setVisibility(View.VISIBLE);
        viewEvaluation.bringToFront();
        toolbar=findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        altitude = (ArrayList<Double>) getIntent().getSerializableExtra("tableauLatitude");
        longitude = (ArrayList<Double>) getIntent().getSerializableExtra("tableauLongitude");
        timeTab = (ArrayList<String>) getIntent().getSerializableExtra("tableauTime");
        speedTab = (ArrayList<String>) getIntent().getSerializableExtra("tableauSpeed");
        locationTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauLocation");
        engineRpmTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauEngineRpm");
        engineLoadTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauEngineLoad");
        throttlePosTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauThrottlePos");
        evalDate=getIntent().getStringExtra("date");
        evalTime=getIntent().getStringExtra("time");
        evalReponse=getIntent().getStringExtra("reponse");

        for(int i=0;i<altitude.size()-1;i++){
            if(engineRpmTab.get(i).contains("null") || engineRpmTab.get(i).contains("NULL")){}
            else{
           listEngineRpm.add(Float.valueOf(engineRpmTab.get(i)));
            String[] dateRpm = timeTab.get(timeTab.size() - 1).split(" ");
            strList_Engine_Rpm.add(dateRpm[1]);}
            if(engineLoadTab.get(i).contains("null") || engineLoadTab.get(i).contains("NULL")){}
            else{
                listEngineLoad.add(Float.valueOf(engineLoadTab.get(i).split("%")[0].replace(",",".")));
                String[] dateRpm = timeTab.get(timeTab.size() - 1).split(" ");
                strList_Engine_Load.add(dateRpm[1]);}
            if(throttlePosTab.get(i).contains("null") || throttlePosTab.get(i).contains("NULL")){}
            else{
                listThrottlePos.add(Float.valueOf(throttlePosTab.get(i).split("%")[0].replace(",",".")));
                String[] dateRpm = timeTab.get(timeTab.size() - 1).split(" ");
                strList_Throttle_Pos.add(dateRpm[1]);}
            String dateLoc1=timeTab.get(i);
            String dateLoc2=timeTab.get(i+1);
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
            double distance=getDistance(altitude.get(i),longitude.get(i),altitude.get(i+1),longitude.get(i+1));
            double dure=dateDifferenceExample(year1,mounth1,day1,hour1,minute1,seconde1,year2,mounth2,day2,hour2,minute2,seconde2);
            Integer vitesse= (int) ((distance*3600)/dure);
            vitesseTab.add(vitesse);
        }
         pieView = (PieView)findViewById(R.id.pie_view);
        progressBar=findViewById(R.id.progDes);
        details=findViewById(R.id.details);
        share=findViewById(R.id.share);
        faible=findViewById(R.id.inflegere);
        moyenne=findViewById(R.id.infmoyen);
        forte=findViewById(R.id.infforte);
        back=findViewById(R.id.back);
        next=findViewById(R.id.next);
        back_Engine_Rpm=findViewById(R.id.back_rpm_engine);
        next_Engine_Rpm=findViewById(R.id.next_rpm_engine);
        back_Engine_Load=findViewById(R.id.back_rpm_load);
        next_Engine_Load=findViewById(R.id.next_rpm_load);
        back_Throttle_Pos=findViewById(R.id.back_throttle_pos);
        next_Throttle_Pos=findViewById(R.id.next_throttle_pos);
        cin=getIntent().getStringExtra("cin");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listEngineRpm.size() >= 10) {
                    viewSpeed.setVisibility(View.INVISIBLE);
                    viewEngineRpm.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(getApplicationContext(), "nothing to show", Toast.LENGTH_SHORT).show();
            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSpeed.setVisibility(View.INVISIBLE);
                viewEvaluation.setVisibility(View.VISIBLE);
            }
        });

        back_Engine_Rpm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewEngineRpm.setVisibility(View.INVISIBLE);
                viewSpeed.setVisibility(View.VISIBLE);
            }
        });
        next_Engine_Rpm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewEngineRpm.setVisibility(View.INVISIBLE);
                viewEngineLoad.setVisibility(View.VISIBLE);
            }
        });
        back_Engine_Load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewEngineLoad.setVisibility(View.INVISIBLE);
                viewEngineRpm.setVisibility(View.VISIBLE);
            }
        });
        next_Engine_Load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewEngineLoad.setVisibility(View.INVISIBLE);
                viewTrottlePos.setVisibility(View.VISIBLE);
            }
        });
        back_Throttle_Pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewTrottlePos.setVisibility(View.INVISIBLE);
                viewEngineLoad.setVisibility(View.VISIBLE);
            }
        });
        /*next_Throttle_Pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewTrottlePos.setVisibility(View.INVISIBLE);
                viewInf.setVisibility(View.VISIBLE);
            }
        });*/

        pieHelperArrayList = new ArrayList<PieHelper>();
        pieHelperArrayList.add(new PieHelper(17,Color.BLUE));
        pieHelperArrayList.add(new PieHelper(73,Color.GREEN));
        pieHelperArrayList.add(new PieHelper(10,Color.rgb(255,127,0)));
        pieHelperArrayList.add(new PieHelper(0,Color.RED));
        new Test().execute();
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listSpeed.size()>0) {
                    viewEvaluation.setVisibility(View.INVISIBLE);
                    viewSpeed.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(getApplicationContext(),"nothing to show",Toast.LENGTH_SHORT).show();
               //new ConnectionEvaluation().execute();
                }

        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOnline())
                    NotificationHelper.showNotificatioon(getApplicationContext(),"Attention ","Pas de connexion");
                else{
                    Bitmap bitmap=getScreenShoot(view);

                    try {
                        String publicDcimDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                        File file=new File(publicDcimDirPath,"myImage.png");
                        //File file=new File(result.this.getExternalCacheDir(),"myImage.png");
                        FileOutputStream fout=new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG,80,fout);
                        fout.flush();
                        fout.close();
                        file.setReadable(true,false);

                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        intent.setType("image/png");
                        startActivity(Intent.createChooser(intent,"share image via"));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"file not found",Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menuresult,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                SharedPreferences sharedPreferencesLogin=getSharedPreferences("login", MODE_PRIVATE);
                final String token_key = sharedPreferencesLogin.getString("token", "");
                final String url=Url.url_api+"/logout";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Http http=new Http(result.this,url);
                        http.setToken(true);
                        http.setMethod("POST");
                        http.sendData(token_key);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences preferences = getSharedPreferences("login", 0);
                                preferences.edit().remove("userId").commit();
                                Intent i=new Intent(result.this,login.class);
                                startActivity(i);
                                finish();
                            }
                        });
                    }
                }).start();
                break;
            case R.id.historique:Intent iLoc=new Intent(result.this,LocationSearch.class);
                iLoc.putExtra("cin",cin);
                startActivity(iLoc);
                break;
            case R.id.profil:
                Intent intentProfil=new Intent(result.this,receive.class);
                intentProfil.putExtra("userCin",cin);
                startActivity(intentProfil);
                finish();
                break;
            case R.id.trip:
                Intent intentTrip=new Intent(result.this,SampleActivity.class);
                intentTrip.putExtra("cin",cin);
                startActivity(intentTrip);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    public int getLocation(double lat,double longi){
            int location = -1;
            String place = "";
            Locale locale = Locale.getDefault();
            Geocoder geocoder = new Geocoder(result.this, locale);
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, longi, 1);
                if (addresses.get(0).getFeatureName() != null)
                    place = addresses.get(0).getFeatureName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (place.contains("A1") || place.contains("Autoroute") || place.contains("highway")|| place.contains("الطريق السيارة") || place.contains("أ1"))
                location = 0;
            else if ((place.startsWith("P") && place.length() < 4) ||
                (place.startsWith("p") && place.length() < 4) ||
                (place.startsWith("GP") && place.length() < 5) ||
                (place.startsWith("C") && place.length() < 5) ||
                (place.startsWith("N") && place.length() < 4)||
                (place.startsWith("Express Rocade"))||
                place.contains("Number"))
                location = 1;
            else
                location = 2;
            return location;
        }

        public class Test extends AsyncTask<String, Integer, String>{

            //ProgressDialog mProgressDialog;
            int nbrFaible=0,nbrMoyenne,nbrForte=0;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Create ProgressBar
                /*mProgressDialog = new ProgressDialog(result.this);
                // Set your ProgressBar Title
                mProgressDialog.setTitle("Wait please");
                // Set your ProgressBar Message
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                // Show ProgressBar
                mProgressDialog.setCancelable(false);
                //  mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();*/
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                super.onProgressUpdate(progress);
                // Update the ProgressBar
                //mProgressDialog.setProgress(progress[0]);
            }

            @Override
            protected String doInBackground(String... strings) {
                int cpt=0;float sumSpeed=0;float sumLoc=0;
                for (int i = 1; i < speedTab.size(); i++) {

                        if(cpt==10 ){
                            listSpeed.add(sumSpeed/10);
                            listSpeed2.add(getNearVal(sumLoc/10));
                            String[] date = timeTab.get(i).split(" ");
                            strList.add(date[1]);
                            float diff=(sumSpeed/10)-(getNearVal(sumLoc/10));
                            if(diff>=0){
                                if(diff<=10)
                                    nbrFaible++;
                                else if(diff<=20)
                                    nbrMoyenne++;
                                else
                                    nbrForte++;
                            }
                            cpt=0;sumSpeed=0;sumLoc=0;
                        }
                        else{
                            cpt++;

                            if(! speedTab.get(i).equals("NULL") && ! speedTab.get(i).equals("null") )
                                sumSpeed+=Float.parseFloat(speedTab.get(i));
                            else
                                sumSpeed+=Float.parseFloat(String.valueOf(vitesseTab.get(i-1)));
                            String res=locationTab.get(i);
                            if(res.equals("Autoroute")|| res.equals("110"))
                               sumLoc+=110;
                            else if(res.equals("Nationale")|| res.equals("90"))
                                sumLoc+=90;
                            else if(res.equals("Urbaine")|| res.equals("50"))
                                sumLoc+=50;
                        }

                    //publishProgress((int) (i*100/speedTab.size()));
                }

                if(cpt != 0) {
                    listSpeed.add(sumSpeed / cpt);
                    listSpeed2.add(getNearVal(sumLoc / cpt));
                    String[] date = timeTab.get(timeTab.size() - 1).split(" ");
                    strList.add(date[1]);
                    float diff = (sumSpeed / cpt) - (getNearVal(sumLoc / cpt));
                    if (diff >= 0) {
                        if (diff <= 10)
                            nbrFaible++;
                        else if (diff <= 20)
                            nbrMoyenne++;
                        else
                            nbrForte++;
                    }
                }
                if(listSpeed2.size()>3){
                for(int i=1;i<listSpeed2.size()-2;i++){
                    if(listSpeed2.get(i-1).equals(listSpeed2.get(i+1))&& ! listSpeed2.get(i).equals(listSpeed2.get(i-1))){
                        listSpeed2.set(i,listSpeed2.get(i-1));
                    }
                }}

                floatDataLists.add(listSpeed);
                floatDataLists.add(listSpeed2);
                floatDataListsEngineRpm.add(listEngineRpm);
                floatDataListsEngineLoad.add(listEngineLoad);
                floatDataListsThrottlePos.add(listThrottlePos);

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //mProgressDialog.dismiss();
                pieView.setDate(pieHelperArrayList);
                pieView.showPercentLabel(true); //optional
                faible.append(nbrFaible+"");
                moyenne.append(nbrMoyenne+"");
                forte.append(nbrForte+"");

                lineViewSpeed = (LineView) findViewById(R.id.line_view);
                lineViewSpeed.setDrawDotLine(false); //optional
                lineViewSpeed.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
                lineViewSpeed.setBottomTextList(strList);
                lineViewSpeed.setColorArray(new int[]{Color.BLACK, Color.RED, Color.GRAY, Color.CYAN});
                lineViewSpeed.setFloatDataList(floatDataLists);

                lineViewEngineRpm = (LineView) findViewById(R.id.line_view_engine_rpm);
                lineViewEngineRpm.setDrawDotLine(false); //optional
                lineViewEngineRpm.setShowPopup(LineView.SHOW_POPUPS_All); //optional
                lineViewEngineRpm.setBottomTextList(strList_Engine_Rpm);
                lineViewEngineRpm.setColorArray(new int[]{Color.BLACK, Color.RED, Color.GRAY, Color.CYAN});
                lineViewEngineRpm.setFloatDataList(floatDataListsEngineRpm);

                lineViewEngineLoad = (LineView) findViewById(R.id.line_view_engine_load);
                lineViewEngineLoad.setDrawDotLine(false); //optional
                lineViewEngineLoad.setShowPopup(LineView.SHOW_POPUPS_All); //optional
                lineViewEngineLoad.setBottomTextList(strList_Engine_Load);
                lineViewEngineLoad.setColorArray(new int[]{Color.BLACK, Color.RED, Color.GRAY, Color.CYAN});
                lineViewEngineLoad.setFloatDataList(floatDataListsEngineLoad);

                lineViewThrottlePos = (LineView) findViewById(R.id.line_view_throttle_pos);
                lineViewThrottlePos.setDrawDotLine(false); //optional
                lineViewThrottlePos.setShowPopup(LineView.SHOW_POPUPS_All); //optional
                lineViewThrottlePos.setBottomTextList(strList_Throttle_Pos);
                lineViewThrottlePos.setColorArray(new int[]{Color.BLACK, Color.RED, Color.GRAY, Color.CYAN});
                lineViewThrottlePos.setFloatDataList(floatDataListsThrottlePos);
            }
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

    public static Bitmap getScreenShoot(View view){
        View screenView=view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap=Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
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

    public String getLocationNom(double lat,double longi){
        String location;
        String place = "";
        Locale locale = Locale.getDefault();
        Geocoder geocoder = new Geocoder(result.this, locale);
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
    public String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1);
    }

    class ConnectionEvaluation extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            String host = "http://192.168.1.103/auth/evaluation/esssai.php?CIN=3&dateDeb=2020-10-17%2007:46:15&dateFin=2020-10-17%2008:05:17";
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
                int timide = jsonResult.getInt("Timid pourcentage");
                int normal = jsonResult.getInt("Normal pourcentage");
                int aggressif = jsonResult.getInt("Agressif pourcentage");
                int dangereux = jsonResult.getInt("Dangerous pourcentage");
                pieHelperArrayList = new ArrayList<PieHelper>();
                pieHelperArrayList.add(new PieHelper(timide,Color.BLUE));
                pieHelperArrayList.add(new PieHelper(normal,Color.GREEN));
                pieHelperArrayList.add(new PieHelper(aggressif,Color.rgb(255,127,0)));
                pieHelperArrayList.add(new PieHelper(100-(normal+timide+aggressif),Color.RED));
                pieView.setDate(pieHelperArrayList);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

