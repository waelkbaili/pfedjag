package com.sohrab.obd.reader;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class CustomAdapterVin extends RecyclerView.Adapter<CustomAdapterVin.ViewHolder>{

    ArrayList<String> ListDate;
    Context context;
    String marque;
    ArrayList<String> dateDEbTab=new ArrayList<>();
    ArrayList<String> dateFinTab=new ArrayList<>();
    ArrayList<String> cinTab=new ArrayList<>();
    ArrayList<Double> latitudeTab=new ArrayList<>();
    ArrayList<Double> longitudeTab=new ArrayList<>();
    String[] options;
    String dateDeb;
    String cincin;
    String userId;
    boolean connexion;
    AlertDialog.Builder builder,builderTryAgain;
    String color;
    android.support.v7.app.AlertDialog alertDialog;


    public CustomAdapterVin(Context context, ArrayList<String> ListDate) {
        this.context = context;
        this.ListDate = ListDate;
        builder = new AlertDialog.Builder(context,R.style.MyDialogTheme);
        builderTryAgain = new AlertDialog.Builder(context,R.style.MyDialogTheme);
        SharedPreferences sharedPreferencess=context.getSharedPreferences("modeDark", MODE_PRIVATE);
        String modeDark=sharedPreferencess.getString("choixDark","0");
        if(modeDark.equals("1")){
            color="#FFFFFF";
        }
        if(modeDark.equals("0")){
            color="#000000";
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rawlayoutcar, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final CustomAdapterVin.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.name.setText(ListDate.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               optionChoice(holder,position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                builder.setMessage((Html.fromHtml("<font color="+color+">"+context.getResources().getString(R.string.deletecar)+"</font>"))).setPositiveButton(Html.fromHtml("<font color=#4D7D8D>"+
                        context.getResources().getString(R.string.yes)+"</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String vinCar=ListDate.get(position).substring(0,17);
                        Log.i("kkkk",vinCar);
                        deleteCar(vinCar,holder,position);
                    }
                }).setNegativeButton(Html.fromHtml("<font color=#4D7D8D>"+
                        context.getResources().getString(R.string.no)+"</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(true);
                //Setting the title manually
                alert.setTitle(Html.fromHtml("<font color="+color+">"+context.getResources().getString(R.string.areyousure)+"</font>"));
                alert.show();
                return true;
            }
        });
    }

    public void optionChoice(final ViewHolder holder, final int position){
        String[] data=ListDate.get(position).split("  ");
        final String vin=data[0];
        View ButtonView = View.inflate(context, R.layout.twubuttonvin, null);
        Button live = (Button) ButtonView.findViewById(R.id.live);
        Button old = (Button) ButtonView.findViewById(R.id.old);

        live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
                SharedPreferences sharedPreferencesLogin = context.getSharedPreferences("login", MODE_PRIVATE);
                userId = sharedPreferencesLogin.getString("userId", "");
                final String url=Url.url_api+"/showLiveTrip/"+userId;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Http http=new Http(context,url);
                        http.setMethod("GET");
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
                                                latitudeTab.add(Double.valueOf(trip.getString("latitude")));
                                                longitudeTab.add(Double.valueOf(trip.getString("longitude")));
                                            }
                                            String lastDate=response.getString("last");
                                            Intent intent=new Intent(context,LiveTrip.class);
                                            intent.putExtra("tableauLatitude",latitudeTab);
                                            intent.putExtra("tableauLongitude",longitudeTab);
                                            intent.putExtra("last_date",lastDate);
                                            intent.putExtra("userId",userId);
                                            context.startActivity(intent);
                                            latitudeTab.clear();
                                            longitudeTab.clear();
                                        }
                                        else{
                                            //tryAgain(holder,position);
                                            Toast.makeText(context,context.getResources().getString(R.string.nolive),Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    Toast.makeText(context,R.string.server,Toast.LENGTH_LONG).show();
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

        old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ListTripVin.class);
                intent.putExtra("vin",vin);
                context.startActivity(intent);
                if (alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        });

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context,R.style.MyDialogTheme);
        builder.setView(ButtonView);
        alertDialog = builder.show();
        /*options=new String[]{context.getResources().getString(R.string.liveroute),context.getResources().getString(R.string.listoldtrip)};
        AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.MyDialogThemePlus);
        builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==1){
                    dialog.dismiss();
                    Intent intent=new Intent(context,ListTripVin.class);
                    intent.putExtra("vin",vin);
                    context.startActivity(intent);
                }
                if(which==0){
                    dialog.dismiss();
                    holder.progressBar.setVisibility(View.VISIBLE);
                    SharedPreferences sharedPreferencesLogin = context.getSharedPreferences("login", context.MODE_PRIVATE);
                    userId = sharedPreferencesLogin.getString("userId", "");
                    final String url=Url.url_api+"/showLiveTrip/"+userId;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Http http=new Http(context,url);
                            http.setMethod("GET");
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
                                                    latitudeTab.add(Double.valueOf(trip.getString("latitude")));
                                                    longitudeTab.add(Double.valueOf(trip.getString("longitude")));
                                                }
                                                String lastDate=response.getString("last");
                                                Intent intent=new Intent(context,LiveTrip.class);
                                                intent.putExtra("tableauLatitude",latitudeTab);
                                                intent.putExtra("tableauLongitude",longitudeTab);
                                                intent.putExtra("last_date",lastDate);
                                                intent.putExtra("userId",userId);
                                                context.startActivity(intent);
                                                latitudeTab.clear();
                                                longitudeTab.clear();
                                            }
                                            else{
                                                //tryAgain(holder,position);
                                                Toast.makeText(context,context.getResources().getString(R.string.nolive),Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else{
                                        Toast.makeText(context,R.string.server,Toast.LENGTH_LONG).show();
                                    }
                                    holder.progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }).start();
                }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();*/
    }

    public void deleteCar(final String carVin, final ViewHolder holder, final int position){
        SharedPreferences sharedPreferencesLogin=context.getSharedPreferences("login", MODE_PRIVATE);
        final String token_key = sharedPreferencesLogin.getString("token", "");
        JSONObject params=new JSONObject();
        try {
            params.put("carVIN",carVin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String data=params.toString();
        final String url=Url.url_api+"/deletecar";
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Http http=new Http(context,url);
                http.setMethod("DELETE");
                http.setData(data);
                http.setToken(true);
                http.sendData(token_key);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(http.getResponse()!=null){
                            try {
                                JSONObject response=new JSONObject(http.getResponse());
                                String status=response.getString("status");
                                if(status.equals("succes")){
                                    ListDate.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,ListDate.size());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            tryAgainDelete(carVin,holder,position);
                        }
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    public void tryAgainDelete(final String carVin, final ViewHolder holder, final int position){
        //Setting message manually and performing action on button click
        builderTryAgain.setMessage(Html.fromHtml("<font color="+color+">"+context.getResources().getString(R.string.requested_data_could_not_be_loaded)+"</font>")).
                setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#4D7D8D'>"+
                context.getResources().getString(R.string.try_again)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCar(carVin,holder,position);
            }
        });
        AlertDialog alert = builderTryAgain.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+context.getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
    }

    @Override
    public int getItemCount() {
        return ListDate.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            progressBar=itemView.findViewById(R.id.progList);
        }

    }

    /*class ConnectionVin extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            String host = Url.url+"/listTripVin.php?VIN="+marque;
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
                        String dateDeb = user.getString("DateDebut");
                        String dateFin = user.getString("DateFin");
                        String cincin = user.getString("CIN");
                        dateDEbTab.add(dateDeb);
                        dateFinTab.add(dateFin);
                        cinTab.add(cincin);
                    }
                    Intent intent=new Intent(context,ListTripVin.class);
                    intent.putExtra("tableauTimeDebut",dateDEbTab);
                    intent.putExtra("tableauTimeFin",dateFinTab);
                    intent.putExtra("tableauCin",cinTab);
                    context.startActivity(intent);
                    dateDEbTab.clear();
                    dateFinTab.clear();
                    cinTab.clear();
                }
                else{
                    Toast.makeText(context,R.string.notrip,Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class Live extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String host = Url.url+"/liveTrip.php?VIN="+marque;
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
                         dateDeb = user.getString("DateDebut");
                         cincin = user.getString("CIN");
                    }
                    new LiveIt().execute();
                }
                else{
                    Toast.makeText(context,R.string.nolive,Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    class LiveIt extends AsyncTask<String,String,String>{
        String[] date=dateDeb.split(" ");
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String host = Url.url+"/recupPositionLive.php?CIN="+cincin+"&dateDeb="+date[0]+"%20"+date[1];
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
                        latitudeTab.add(latitude);
                        longitudeTab.add(longitude);
                    }
                    Intent intent=new Intent(context,LiveTrip.class);
                    intent.putExtra("tableauLatitude",latitudeTab);
                    intent.putExtra("tableauLongitude",longitudeTab);
                    intent.putExtra("cin",cincin);
                    intent.putExtra("vin",marque);
                    intent.putExtra("date",dateDeb);
                    context.startActivity(intent);
                    latitudeTab.clear();
                    longitudeTab.clear();
                }
                else Toast.makeText(context, R.string.server, Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/
}
