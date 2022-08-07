package com.sohrab.obd.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ListTripVin extends AppCompatActivity {

    // ArrayList for person names
    ArrayList<String> listDate = new ArrayList<>();
    ArrayList<String> timeDebTab=new ArrayList<>();
    ArrayList<String> timeFinTab=new ArrayList<>();
    ArrayList<String> cinTab=new ArrayList<>();
    Context context;
    CustomAdapterListVin customAdapter;
    RecyclerView recyclerView;
    boolean iscrolling=false;
    int currentItems,totalItems,scrollOutItems;
    RelativeLayout relativeLayout;
    boolean connexion;
    ProgressBar progressBar;
    int nbr,nbrpages,actualpage=1;
    LinearLayoutManager linearLayoutManager;
    String vin;

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
        setContentView(R.layout.activity_list_trip_vin);
        /*relativeLayout=findViewById(R.id.listtripbyvin);
        if(modeDark.equals("1")){
            relativeLayout.setBackgroundResource(R.drawable.dark_back);
        }
        if(modeDark.equals("0")){
            relativeLayout.setBackgroundResource(R.drawable.back_light);
        }*/
        context=getApplicationContext();
        // get the reference of RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewVinTrip);
        progressBar=findViewById(R.id.progresslist);
        progressBar.setVisibility(View.INVISIBLE);
        // set a LinearLayoutManager with default vertical orientation
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        Intent intent=getIntent();
        vin=intent.getStringExtra("vin");
        new Thread(new Runnable() {
            @Override
            public void run() {
                connexion=isOnline();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!connexion){
                            NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        else{
                            final String url=Url.url_api+"/showtrip/"+vin+"?page=1";
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final Http http=new Http(ListTripVin.this,url);
                                    http.setMethod("GET");
                                    http.sendData(null);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(http.getResponse()!=null){
                                                try {
                                                    JSONObject response=new JSONObject(http.getResponse());
                                                    String status=response.getString("status");
                                                    if(status.equals("succes")){
                                                        nbr= Integer.parseInt(response.getString("nbr"));
                                                        nbrpages=nbr/15+1;
                                                        JSONArray trips=response.getJSONArray("data");
                                                        for(int i=0;i<trips.length();i++){
                                                            JSONObject trip=trips.getJSONObject(i);
                                                            listDate.add(trip.getString("date_start")+"\n"+
                                                                    "conducteur:"+"\t"+trip.getString("user_id"));
                                                        }
                                                        //  call the constructor of CustomAdapter to send the reference and data to Adapter
                                                        customAdapter = new CustomAdapterListVin(ListTripVin.this, listDate);
                                                        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                    else{
                                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.notrip),Toast.LENGTH_LONG).show();
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        finish();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    iscrolling=true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems=linearLayoutManager.getChildCount();
                totalItems=linearLayoutManager.getItemCount();
                scrollOutItems=linearLayoutManager.findFirstVisibleItemPosition();
                if(iscrolling && currentItems+scrollOutItems==totalItems){
                    iscrolling=false;
                    fetchData();
                }
            }
        });
        /*timeDebTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauTimeDebut");
        timeFinTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauTimeFin");
        timeFinTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauTimeFin");
        cinTab= (ArrayList<String>) getIntent().getSerializableExtra("tableauCin");
        for (int i=0;i<timeFinTab.size();i++){
            listDate.add(timeDebTab.get(i)+"--"+timeFinTab.get(i)+"--"+cinTab.get(i));
        }*/
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
         /*customAdapter = new CustomAdapterListVin(ListTripVin.this, listDate);
        recyclerView.addItemDecoration(new LineDividerItemDecoration(this, R.drawable.line_divider));
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView*/
    }

    private void fetchData() {
        if(nbrpages>1&&actualpage<nbrpages){
            actualpage++;
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
                                final String url=Url.url_api+"/showtrip/"+vin+"?page="+actualpage;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Http http=new Http(ListTripVin.this,url);
                                        http.setMethod("GET");
                                        http.sendData(null);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(http.getResponse()!=null){
                                                    try {
                                                        JSONObject response=new JSONObject(http.getResponse());
                                                        String status=response.getString("status");
                                                        if(status.equals("succes")){
                                                            JSONArray trips=response.getJSONArray("data");
                                                            for(int i=0;i<trips.length();i++){
                                                                JSONObject trip=trips.getJSONObject(i);
                                                                listDate.add(trip.getString("date_start")+"  "+"\n"+
                                                                        "conducteur: "+trip.getString("user_id"));
                                                            }
                                                            //  call the constructor of CustomAdapter to send the reference and data to Adapter
                                                            customAdapter.notifyDataSetChanged();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
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

        }
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



}
