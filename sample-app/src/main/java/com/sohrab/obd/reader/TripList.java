package com.sohrab.obd.reader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;
import java.util.Arrays;

public class TripList extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    CustomAdapter customAdapter;
    ArrayList<String> listDate = new ArrayList<>();
    Context context;
    boolean iscrolling=false;
    int currentItems,totalItems,scrollOutItems;
    boolean connexion;
    int nbr,nbrpages,actualpage=1;
    RelativeLayout relativeLayout;
    AlertDialog.Builder builder;
    String color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_trip_list);
        /*relativeLayout=findViewById(R.id.listtrip);
        if(modeDark.equals("1")){
            relativeLayout.setBackgroundResource(R.drawable.dark_back);
        }
        if(modeDark.equals("0")){
            relativeLayout.setBackgroundResource(R.drawable.back_light);
        }*/
        context=getApplicationContext();
        // get the reference of RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar=findViewById(R.id.progresslist);
        progressBar.setVisibility(View.INVISIBLE);
        builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        // set a LinearLayoutManager with default vertical orientation
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        loadData();
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
    }

    private void fetchData() {
        if(nbrpages>1&&actualpage<nbrpages){
            actualpage++;
            progressBar.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                Intent intent=getIntent();
                String dateDeb=intent.getStringExtra("start");
                String dateEnd=intent.getStringExtra("end");
                String user_id=intent.getStringExtra("userId");
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
                                    params.put("user_id",user_id);
                                    params.put("date_deb",dateDeb);
                                    params.put("date_end",dateEnd);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String data=params.toString();
                                final String url=Url.url_api+"/showtrip?page="+actualpage;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Http http=new Http(TripList.this,url);
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
                                                            JSONArray trips=response.getJSONArray("data");
                                                            for(int i=0;i<trips.length();i++){
                                                                JSONObject trip=trips.getJSONObject(i);
                                                                listDate.add(trip.getString("date_start"));
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

    public void loadData(){
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            Intent intent=getIntent();
            String dateDeb=intent.getStringExtra("start");
            String dateEnd=intent.getStringExtra("end");
            String user_id=intent.getStringExtra("userId");

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
                            JSONObject params=new JSONObject();
                            try {
                                params.put("user_id",user_id);
                                params.put("date_deb",dateDeb);
                                params.put("date_end",dateEnd);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            final String data=params.toString();
                            final String url=Url.url_api+"/showtrip?page=1";
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final Http http=new Http(TripList.this,url);
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
                                                        nbr= Integer.parseInt(response.getString("nbr"));
                                                        nbrpages=nbr/15+1;
                                                        JSONArray trips=response.getJSONArray("data");
                                                        for(int i=0;i<trips.length();i++){
                                                            JSONObject trip=trips.getJSONObject(i);
                                                            listDate.add(trip.getString("date_start"));
                                                        }
                                                        //  call the constructor of CustomAdapter to send the reference and data to Adapter
                                                        customAdapter = new CustomAdapter(TripList.this, listDate);
                                                        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                    else{
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.noshow),Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else{
                                                tryAgain();
                                                //Toast.makeText(getApplicationContext(),R.string.server,Toast.LENGTH_LONG).show();
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
                loadData();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
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
