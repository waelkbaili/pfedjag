package com.sohrab.obd.reader;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.maps.model.JointType.ROUND;

import info.hoang8f.android.segmented.SegmentedGroup;


public class Location extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    ArrayList<Double> altitude=new ArrayList<>();
    ArrayList<Double> longitude=new ArrayList<>();
    ArrayList<String> timeTab=new ArrayList<>();
    ArrayList<Integer> vitesseTab=new ArrayList<>();
    ArrayList<Float> angleTab=new ArrayList<>();
    ArrayList<String> locationTab=new ArrayList<>();
    ArrayList<String> speedTab=new ArrayList<>();
    ArrayList<String> engineRpmTab=new ArrayList<>();
    ArrayList<String> engineLoadTab=new ArrayList<>();
    ArrayList<String> throttlePosTab=new ArrayList<>();
    ArrayList<String> fuelConsTab=new ArrayList<>();
    public List<LatLng> polyLineList=new ArrayList<>();
    public Marker marker;
    public float v;
    public double lat,lng;
    public Handler handler;
    public LatLng startPosition, endPosition;
    public int index, next;
    Button speedPos,speedMin,eval;
    ImageButton toogle,restart,details,back;
    int duration=3000;
    Marker markerStart,markerEnd;
    public PolylineOptions polylineOptions;
    public Polyline greyPolyLine;
    ValueAnimator valueAnimator;
    TextView vitesse,time;
    int red=Color.RED;
    int green=Color.GREEN;
    int orange=Color.rgb(255,127,0);
    int color;
    TextView speedLimit;
    String evalDate,evalTime,evalReponse;
    double sDistance=0;
    int sTime=0;
    float sFuel=0;
    DecimalFormat df = new DecimalFormat("0.000");
    String refrence,userId;
    SegmentedGroup radioGroup;
    RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        toogle=findViewById(R.id.btnToggle);
        speedPos=findViewById(R.id.btn_speedpos);
        speedMin=findViewById(R.id.btnSpeedmin);
        vitesse=findViewById(R.id.vitesse);
        restart=findViewById(R.id.restart);
        time=findViewById(R.id.time);
        speedLimit=findViewById(R.id.limiteSpeed);
        //eval=findViewById(R.id.eva);
        details=findViewById(R.id.details);
        back=findViewById(R.id.back);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setTintColor(Color.parseColor("#4D7D8D"), Color.parseColor("#FFFFFF"));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(refrence!=null && refrence.equals("home")){
                    Intent intent=new Intent(Location.this,SampleActivity.class);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
                    finish();
                }
                else if(refrence!=null && refrence.equals("historique")){
                    Intent intent=new Intent(Location.this,LocationSearch.class);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
                    finish();
                }
                else if(refrence!=null && refrence.equals("control")){
                    Intent intent=new Intent(Location.this,CarList.class);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
                    finish();
                }
                else
                    finish();*/
                onBackPressed();
            }
        });

        /*eval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(Location.this,result.class);
                i.putExtra("tableauLatitude", altitude);
                i.putExtra("tableauLongitude", longitude);
                i.putExtra("tableauTime", timeTab);
                i.putExtra("tableauSpeed", speedTab);
                i.putExtra("tableauLocation", locationTab);
                i.putExtra("cin",getIntent().getStringExtra("cin"));
                i.putExtra("tableauEngineRpm",engineRpmTab);
                i.putExtra("tableauEngineLoad",engineLoadTab);
                i.putExtra("tableauThrottlePos",throttlePosTab);
                i.putExtra("date",evalDate);
                i.putExtra("time",evalTime);
                i.putExtra("reponse",evalReponse);
                startActivity(i);
            }
        });*/

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
        speedPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(duration > 500){
                    duration-=500;
                    speedMin.setEnabled(true);}
                else if(duration>100)
                    duration-=100;
                else if(duration >10)
                    duration-=10;
                if(duration == 10)
                    speedPos.setEnabled(false);
            }
        });
        speedMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(duration>=10 && duration<100){
                    duration+=10;
                    speedPos.setEnabled(true);}
                else if(duration>=100&& duration<500)
                    duration+=100;
                else if(duration>=500)
                    duration+=500;
                if(duration==3000)
                    speedMin.setEnabled(false);
                else
                    speedMin.setEnabled(true);
            }

        });
        altitude = (ArrayList<Double>) getIntent().getSerializableExtra("tableauLatitude");
        longitude = (ArrayList<Double>) getIntent().getSerializableExtra("tableauLongitude");
        timeTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauTime");
        locationTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauLocation");
        refrence=getIntent().getStringExtra("ref");
        userId=getIntent().getStringExtra("userId");
        /*speedTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauSpeed");
        engineRpmTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauEngineRpm");
        engineLoadTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauEngineLoad");
        throttlePosTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauThrottlePos");
        fuelConsTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauFuelCons");
        evalDate=getIntent().getStringExtra("date");
        evalTime=getIntent().getStringExtra("time");
        evalReponse=getIntent().getStringExtra("reponse");*/

        for(int i=0;i<altitude.size();i++){
            LatLng latLng=new LatLng(altitude.get(i),longitude.get(i));
            polyLineList.add(latLng);
            String loc =locationTab.get(i);
            switch (loc){
                case "Urbaine":locationTab.set(i,"50");break;
                case "Nationale":locationTab.set(i,"90");break;
                case "Autoroute":locationTab.set(i,"110");break;
            }
        }
        supportMapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap=googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target)
                .zoom(17)
                .bearing(30)
                .tilt(45)
                .build()));

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
        }
        //Adjusting bounds

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds bounds = builder.build();
                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                mMap.animateCamera(mCameraUpdate);
            }
        });

        for(int i=0;i<polyLineList.size()-1;i++){
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
            double distance=getDistance(polyLineList.get(i).latitude,polyLineList.get(i).longitude,polyLineList.get(i+1).latitude,polyLineList.get(i+1).longitude);
            double dure=dateDifferenceExample(year1,mounth1,day1,hour1,minute1,seconde1,year2,mounth2,day2,hour2,minute2,seconde2);
            sDistance+=distance;
            sTime+=dure;
            //if(! fuelConsTab.get(i).equals("NULL"))
            //sFuel+=Float.parseFloat(fuelConsTab.get(i));
            Integer vitesse= (int) ((distance*3600)/dure);
            if(vitesse<30)
                color=red;
            else if(vitesse<70)
                color=orange;
            else
                color=green;
            vitesseTab.add(vitesse);
            if(i<polyLineList.size()-2){
            float angle1=getBearing(polyLineList.get(i),polyLineList.get(i+1));
            float angle2=getBearing(polyLineList.get(i+1),polyLineList.get(i+2));
            float diff = Math.max(angle1, angle2) - Math.min(angle1, angle2);
            if (diff > 180) diff = 360 - diff;
           angleTab.add(diff);
            }
            mMap.addPolyline(new PolylineOptions()
                    .add(polyLineList.get(i),polyLineList.get(i+1))
                    .width(10)
                    .color(color)
                    .startCap(new SquareCap())
                    .endCap(new SquareCap())
                    .jointType(ROUND));
        }

       /* details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderr = new AlertDialog.Builder(Location.this);
                builderr.setMessage(getResources().getString(R.string.distancetraveled)+" : "+df.format(sDistance)+" KM"+"\n"+
                        getResources().getString(R.string.tripduration)+" : "+sTime/3600+"H "+(sTime%3600)/60+"M "+sTime%60+"S"+"\n"+
                        getResources().getString(R.string.consomationfuel)+" : "+sFuel/fuelConsTab.size()+" L/H")
                        .setCancelable(false)
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builderr.create();
                alert.show();
            }
        });*/
        //altitude.clear();
        //longitude.clear();
        markerStart=mMap.addMarker(new MarkerOptions()
                .position(polyLineList.get(0)).title("Start"));
        markerStart.showInfoWindow();
        markerEnd = mMap.addMarker(new MarkerOptions()
                .position(polyLineList.get(polyLineList.size() - 1)).title("Sweet Trip"));

        marker = mMap.addMarker(new MarkerOptions().position(polyLineList.get(0))
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carr)));
        handler = new Handler();
        index = -1;
        next = 1;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                restart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        index = -1;
                        next = 1;
                    }
                });
                if (index < polyLineList.size() - 1) {
                    index++;
                    next = index + 1;
                }
                if (index < polyLineList.size() - 1) {
                    startPosition = polyLineList.get(index);
                    endPosition = polyLineList.get(next);
                }
                valueAnimator = ValueAnimator.ofFloat(0, 1);
                //time animation
                valueAnimator.setDuration(duration);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        v = valueAnimator.getAnimatedFraction();
                        double distance=getDistance(startPosition.latitude,startPosition.longitude,endPosition.latitude,endPosition.longitude);
                        if(distance != 0) {
                            lng = v * endPosition.longitude + (1 - v)
                                    * startPosition.longitude;
                            lat = v * endPosition.latitude + (1 - v)
                                    * startPosition.latitude;
                            LatLng newPos = new LatLng(lat, lng);
                            marker.setPosition(newPos);
                            marker.setAnchor(0.5f, 0.5f);
                            marker.setRotation(getBearing(startPosition, newPos));
                            /*mMap.moveCamera(CameraUpdateFactory
                                    .newCameraPosition
                                            (new CameraPosition.Builder().
                                                    zoom(mMap.getCameraPosition().zoom)
                                                    .target(newPos)
                                                    .build()));*/
                            //marker.showInfoWindow();
                        }
                    }

                });
                valueAnimator.start();
                //temps entre 2 position
                handler.postDelayed(this, duration);
                if(index<vitesseTab.size())
                    vitesse.setText(vitesseTab.get(index)+ " Km/h");
                    //vitesse.setText(speedTab.get(index+1)+ " Km/h");
                time.setText(timeTab.get(index));
                speedLimit.setText(locationTab.get(index)+"");

                //if(index>0)
                    //Toast.makeText(getApplicationContext(),angleTab.get(index-1)+"",Toast.LENGTH_SHORT).show();
                //Log.i("kkkk",angleTab.get(index-1)+"");

                double distanceFin=getDistance(polyLineList.get(polyLineList.size()-1).latitude,polyLineList.get(polyLineList.size()-1).longitude,endPosition.latitude,endPosition.longitude);
                if(distanceFin == 0) {
                    valueAnimator.end();
                    markerEnd.showInfoWindow();
                    handler.removeCallbacksAndMessages(null);
                    //timeTab.clear();
                    //locationTab.clear();
                }
                Locale locale=Locale.getDefault();
                Geocoder geocoder = new Geocoder(Location.this, locale);
                try {
                    List<Address> addresses = geocoder.getFromLocation(startPosition.latitude,startPosition.longitude, 1);
                   time.append("---"+ addresses.get(0).getFeatureName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //delay to start
            }
        }, 3000);
    }


    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
    /** calculates the distance between two locations in MILES */
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
    public String getLocationNom(double lat,double longi){
        String location ="";
        String place = "";
        Locale locale = Locale.getDefault();
        Geocoder geocoder = new Geocoder(Location.this, locale);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
        if(refrence!=null && refrence.equals("home")){
            Intent intent=new Intent(Location.this,SampleActivity.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
            finish();
        }
        else if(refrence!=null && refrence.equals("historique")){
            Intent intent=new Intent(Location.this,LocationSearch.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
            finish();
        }
        else if(refrence!=null && refrence.equals("control")){
            Intent intent=new Intent(Location.this,CarList.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
            finish();
        }
        else
            finish();
    }
}
