package com.sohrab.obd.reader;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

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

import static com.google.android.gms.maps.model.JointType.ROUND;

public class LiveTrip extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    private ArrayList<Double> latitude=new ArrayList<>();
    private ArrayList<Double> longitude=new ArrayList<>();
    private ArrayList<LatLng> polyLineList=new ArrayList<>();
    private ArrayList<LatLng> polyLineListNew=new ArrayList<>();
    private Marker markerStart,marker;
    ImageButton toogle,restart;
    //private String cin,vin,date;
    TextView vitesse;
    ArrayList<Double> latitudeTab=new ArrayList<>();
    ArrayList<Double> longitudeTab=new ArrayList<>();
    String last_date,user_id;
    boolean live=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_trip);
        toogle=findViewById(R.id.btnToggle);
        restart=findViewById(R.id.back);
        vitesse=findViewById(R.id.vitesseLive);
        SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        latitude = (ArrayList<Double>) getIntent().getSerializableExtra("tableauLatitude");
        longitude = (ArrayList<Double>) getIntent().getSerializableExtra("tableauLongitude");
        last_date=getIntent().getStringExtra("last_date");
        user_id=getIntent().getStringExtra("userId");

        //cin=getIntent().getStringExtra("cin");
        //vin=getIntent().getStringExtra("vin");
        //date=getIntent().getStringExtra("date");
        for(int i=0;i<latitude.size()-3;i++){
            LatLng latLng=new LatLng(latitude.get(i),longitude.get(i));
            polyLineList.add(latLng);
        }
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
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        supportMapFragment.getMapAsync(this);

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                while (live){
                    JSONObject params=new JSONObject();
                    try {
                        params.put("user_id",user_id);
                        params.put("date_start",last_date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String data=params.toString();
                    final String url=Url.url_api+"/liveposition";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Http http=new Http(LiveTrip.this,url);
                            http.setMethod("POST");
                            http.setData(data);
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
                                                    LatLng lng=new LatLng(Double.parseDouble(trip.getString("latitude")),
                                                            Double.parseDouble(trip.getString("longitude")));
                                                    polyLineList.add(lng);
                                                    mMap.addPolyline(new PolylineOptions()
                                                            .add(polyLineList.get(polyLineList.size()-2),
                                                                    polyLineList.get(polyLineList.size()-1))
                                                            .width(10)
                                                            .color(Color.RED)
                                                            .startCap(new SquareCap())
                                                            .endCap(new SquareCap())
                                                            .jointType(ROUND));
                                                    vitesse.setText(trip.getString("speed")+"  Km/h");
                                                }

                                                marker.remove();
                                                markerStart.remove();
                                                markerStart=mMap.addMarker(new MarkerOptions()
                                                        .position(polyLineList.get(0)).title("Start"));
                                                markerStart.showInfoWindow();
                                                marker = mMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size()-1))
                                                        .flat(true)
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.carr)));
                                            }
                                            else
                                                live=false;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),R.string.server,Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        }
                    }).start();
                }

            }
        }.start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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

            mMap.addPolyline(new PolylineOptions()
                .add(polyLineList.get(i),polyLineList.get(i+1))
                .width(10)
                .color(Color.GRAY)
                .startCap(new SquareCap())
                .endCap(new SquareCap())
                .jointType(ROUND));
    }

        markerStart=mMap.addMarker(new MarkerOptions()
                .position(polyLineList.get(0)).title("Start"));
        markerStart.showInfoWindow();

        marker = mMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size()-1))
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carr)));
    }

    /*class LiveIt extends AsyncTask<String,String,String> {
        String[] dateTab=date.split(" ");
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String host = Url.url+"/recupPositionLive.php?CIN="+cin+"&dateDeb="+dateTab[0]+"%20"+dateTab[1];
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
                        double latitude = Double.parseDouble(user.getString("altitude"));
                        double longitude = Double.parseDouble(user.getString("longitude"));
                        String speed=user.getString("SPEED");
                        vitesse.setText(speed+"  Km/h");
                            LatLng latLng=new LatLng(latitude,longitude);
                            polyLineListNew.add(latLng);
                    }
                    mMap.clear();
                    for(int i=0;i<polyLineListNew.size()-1;i++){

                        mMap.addPolyline(new PolylineOptions()
                                .add(polyLineListNew.get(i),polyLineListNew.get(i+1))
                                .width(10)
                                .color(Color.RED)
                                .startCap(new SquareCap())
                                .endCap(new SquareCap())
                                .jointType(ROUND));
                    }

                    markerStart=mMap.addMarker(new MarkerOptions()
                            .position(polyLineListNew.get(0)).title("Start"));
                    markerStart.showInfoWindow();

                    marker = mMap.addMarker(new MarkerOptions().position(polyLineListNew.get(polyLineListNew.size()-1))
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.carr)));
                    polyLineListNew.clear();

                    new CountDownTimer(10000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            new LiveIt().execute();
                        }
                    }.start();
                }
                else
                    Toast.makeText(getApplicationContext(), R.string.server, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/
}
