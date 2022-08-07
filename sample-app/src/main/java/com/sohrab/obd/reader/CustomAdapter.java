package com.sohrab.obd.reader;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    ArrayList<String> ListDate;
    Context context;
    String dateDEb;
    String dateFin;
    String userId,date;
    ArrayList<Double> pointX=new ArrayList<>();
    ArrayList<Double> pointY=new ArrayList<>();
    ArrayList<String> timeTab=new ArrayList<>();
    ArrayList<String> speedTab=new ArrayList<>();
    ArrayList<String> locationTab=new ArrayList<>();
    ArrayList<String> engineRpmTab=new ArrayList<>();
    ArrayList<String> engineLoadTab=new ArrayList<>();
    ArrayList<String> throttlePosTab=new ArrayList<>();
    ArrayList<String> fuelConsTab=new ArrayList<>();
    String date1,time1,date2,time2;
    String[] options;
    Boolean option;
    SharedPreferences sharedPreferencesLogin;
    AlertDialog alertDialog;

    //int row_index=-1;
    //MyViewHolder myViewHolder;

    public CustomAdapter(Context context, ArrayList<String> ListDate) {
        this.context = context;
        this.ListDate = ListDate;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // set the data in items
        holder.name.setText(ListDate.get(position));
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
               // row_index=position;
                //notifyDataSetChanged();
                date=ListDate.get(position);
                sharedPreferencesLogin=context.getSharedPreferences("login", MODE_PRIVATE);
                userId=sharedPreferencesLogin.getString("userId", "");
                View ButtonView = View.inflate(context, R.layout.twobutton, null);
                Button route = (Button) ButtonView.findViewById(R.id.route);
                Button evaluation = (Button) ButtonView.findViewById(R.id.evaluation);
                route.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.progressBar.setVisibility(View.VISIBLE);
                        JSONObject params=new JSONObject();
                        try {
                            params.put("user_id",userId);
                            params.put("date_start",date);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String data=params.toString();
                        final String url=Url.url_api+"/position";
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Http http=new Http(context,url);
                                http.setMethod("POST");
                                http.setData(data);
                                http.sendData(null);

                                ((Activity) context).runOnUiThread(new Runnable() {
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
                                                    if(pointX.size()>15 ) {
                                                        Intent intent = new Intent(context, Location.class);
                                                        intent.putExtra("tableauLatitude", pointX);
                                                        intent.putExtra("tableauLongitude", pointY);
                                                        intent.putExtra("tableauTime", timeTab);
                                                        intent.putExtra("tableauLocation", locationTab);
                                                        context.startActivity(intent);
                                                    }
                                                    else{
                                                        Toast.makeText(context,context.getResources().getString(R.string.emptyTrip),Toast.LENGTH_LONG).show();
                                                        ListDate.remove(position);
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position,ListDate.size());
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }).start();
                        if (alertDialog.isShowing())
                            alertDialog.dismiss();
                    }
                });
                evaluation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (alertDialog.isShowing())
                            alertDialog.dismiss();
                    }
                });
                android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.MyDialogTheme);
                builder.setView(ButtonView);
                alertDialog = builder.show();
                 //cin=recupCin();
                 /*options=new String[]{context.getResources().getString(R.string.itinéraire),context.getResources().getString(R.string.eval)};
                AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.MyDialogThemePlus);
                builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            option=true;
                            dialog.dismiss();
                            holder.progressBar.setVisibility(View.VISIBLE);
                            JSONObject params=new JSONObject();
                            try {
                                params.put("user_id",userId);
                                params.put("date_start",date);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            final String data=params.toString();
                            final String url=Url.url_api+"/position";
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final Http http=new Http(context,url);
                                    http.setMethod("POST");
                                    http.setData(data);
                                    http.sendData(null);

                                    ((Activity) context).runOnUiThread(new Runnable() {
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
                                                            Intent intent = new Intent(context, Location.class);
                                                            intent.putExtra("tableauLatitude", pointX);
                                                            intent.putExtra("tableauLongitude", pointY);
                                                            intent.putExtra("tableauTime", timeTab);
                                                            intent.putExtra("tableauLocation", locationTab);
                                                            context.startActivity(intent);
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            holder.progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            }).start();
                            //new Connection().execute();
                        }

                        if(which==1){
                            option=false;
                            dialog.dismiss();
                            //holder.progressBar.setVisibility(View.VISIBLE);
                            //new Connection().execute();
                        }
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();*/
            }
        });
       // myViewHolder=holder;

       /* if(row_index==position){
           // holder.row_linearlayout.setBackgroundColor(Color.parseColor("#567845"));
            holder.name.setTextColor(Color.RED);
        }
        else
        {
           // holder.row_linearlayout.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.name.setTextColor(Color.GREEN);
        }*/
    }


    @Override
    public int getItemCount() {
        return ListDate.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;// init the item view's
        public ProgressBar progressBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            progressBar=itemView.findViewById(R.id.progList);
        }

    }
    /*class Connection extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
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
                        Intent intent = new Intent(context, Location.class);
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
                        intent.putExtra("date",date1);
                        intent.putExtra("time",time1);
                        intent.putExtra("reponse","no");
                        Intent intent2 = new Intent(context, result.class);
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
                        intent2.putExtra("date",date1);
                        intent2.putExtra("time",time1);
                        intent2.putExtra("reponse","no");
                        if(option)
                            context.startActivity(intent);
                        else
                            context.startActivity(intent2);
                        pointX.clear();
                        pointY.clear();
                        timeTab.clear();
                        speedTab.clear();
                        locationTab.clear();
                        engineLoadTab.clear();
                        engineRpmTab.clear();
                        throttlePosTab.clear();
                        fuelConsTab.clear();
                    }
                    else
                        Toast.makeText(context,R.string.emptyTrip,Toast.LENGTH_SHORT).show();
                }
                else if(success==2)
                    Toast.makeText(context, R.string.server, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context,R.string.emptyTrip,Toast.LENGTH_SHORT).show();


            } catch (JSONException e) {
                e.printStackTrace();
            }

            //myViewHolder.progressBar.setVisibility(View.INVISIBLE);

        }
    }
    public String recupCin() {
        String cinlog="";
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        String privateDcimDirPath = context.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File file = new File(privateDcimDirPath, "dataUser.txt");
        if (file.exists()) {
            try {
                in = new BufferedReader(new FileReader(file));
                while ((line = in.readLine()) != null) {

                    String[] arrayString = line.split("/");
                    cinlog = arrayString[0];
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cinlog;
    }*/









}
