package com.sohrab.obd.reader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
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
import android.widget.ImageView;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class receive extends AppCompatActivity {
    String cin;
    TextView name_view,address_view,email_view,
            bienvenue_view,image_view,permis_view,dateBirth_view,gender_view;
    View ligne1,ligne2,ligne3,ligne4,ligne5,ligne6;
    Toolbar toolbar;
    Button update;
    CircleImageView img;
    String usernamee,photo,name,gender,address,email,permis,birth_date;
    ProgressBar simpleProgressBar;
    String typeCompte;
    SharedPreferences sharedPreferencesLogin;
    ConstraintLayout first,second,progressLayout;
    String userId;
    boolean connexion;
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
        setContentView(R.layout.activity_receive);
        first=findViewById(R.id.first_back);
        second=findViewById(R.id.second_back);
        progressLayout=findViewById(R.id.progresslayout);
        builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        if(modeDark.equals("1")){
            first.setBackgroundResource(R.drawable.first_back_dark);
            second.setBackgroundResource(R.drawable.second_back_dark);
        }
        if(modeDark.equals("0")){
            first.setBackgroundResource(R.drawable.first_back_light);
            second.setBackgroundResource(R.drawable.second_back_light);
        }
        name_view=findViewById(R.id.name);
        address_view=findViewById(R.id.adress);
        email_view=findViewById(R.id.mail);
        bienvenue_view=findViewById(R.id.bien);
        img=findViewById(R.id.imagepro);
        image_view=findViewById(R.id.image);
        permis_view=findViewById(R.id.permis);
        dateBirth_view=findViewById(R.id.birthday);
        gender_view=findViewById(R.id.sexe);
        ligne1=findViewById(R.id.ligne1);
        ligne2=findViewById(R.id.ligne2);
        ligne3=findViewById(R.id.ligne3);
        ligne4=findViewById(R.id.ligne4);
        ligne5=findViewById(R.id.ligne5);
        ligne6=findViewById(R.id.ligne6);
        toolbar=findViewById(R.id.myToolBarBack);
        update=findViewById(R.id.update);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.profil));
        simpleProgressBar=findViewById(R.id.progRec);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(receive.this,SampleActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
                finish();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
        sharedPreferencesLogin = getSharedPreferences("login", MODE_PRIVATE);
        userId = sharedPreferencesLogin.getString("userId", "");
        recupData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menucopie,menu);
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
                        connexion=haveInternetConnection();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              if(!connexion){
                                  NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                  simpleProgressBar.setVisibility(View.INVISIBLE);
                              }
                              else{
                                  SharedPreferences sharedPreferencesLogin=getSharedPreferences("login", MODE_PRIVATE);
                                  final String token_key = sharedPreferencesLogin.getString("token", "");
                                  final String url=Url.url_api+"/logout";
                                  new Thread(new Runnable() {
                                      @Override
                                      public void run() {
                                          final Http http=new Http(receive.this,url);
                                          http.setToken(true);
                                          http.setMethod("POST");
                                          http.sendData(token_key);
                                          runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  SharedPreferences preferences = getSharedPreferences("login", 0);
                                                  preferences.edit().remove("userId").commit();
                                                  Intent i=new Intent(receive.this,login.class);
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
            case R.id.param:
                simpleProgressBar.setVisibility(View.VISIBLE);
                if(typeCompte.equals("pro")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            connexion=haveInternetConnection();
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  if(!connexion){
                                      NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                      simpleProgressBar.setVisibility(View.INVISIBLE);
                                  }
                                  else{
                                      Intent i2=new Intent(receive.this,Parametres.class);
                                      i2.putExtra("userId",userId);
                                      simpleProgressBar.setVisibility(View.INVISIBLE);
                                      startActivity(i2);
                                      finish();
                                  }
                              }
                          });
                        }
                    }).start();
                }
                else Toast.makeText(getApplicationContext(),R.string.prosvp,Toast.LENGTH_SHORT).show();
                break;
            /*case R.id.loc:
                simpleProgressBar.setVisibility(View.VISIBLE);
                if(typeCompte.equals("pro")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            connexion=haveInternetConnection();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!connexion){
                                        NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                        simpleProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                   else{
                                        simpleProgressBar.setVisibility(View.INVISIBLE);
                                        Intent iLoc=new Intent(receive.this,LocationSearch.class);
                                        iLoc.putExtra("userId",userId);
                                        startActivity(iLoc);
                                    }
                                }
                            });
                        }
                    }).start();
                }
                else Toast.makeText(getApplicationContext(),R.string.prosvp,Toast.LENGTH_SHORT).show();
            break;

            case R.id.dash:
                simpleProgressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connexion=haveInternetConnection();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!connexion){
                                    NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                }
                                else{
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    Intent dash=new Intent(receive.this,Dashboard.class);
                                    dash.putExtra("userId",userId);
                                    startActivity(dash);
                                }
                            }
                        });
                    }
                }).start();
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }



    public void update() {
        if(typeCompte.equals("pro")) {
            if (!haveInternetConnection())
                NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
            else {
                Intent i = new Intent(receive.this, update.class);
                i.putExtra("userId", userId);
                i.putExtra("name",name);
                i.putExtra("adress", address);
                i.putExtra("email", email);
                i.putExtra("username", usernamee);
                i.putExtra("permis", permis_view.getText().toString());
                i.putExtra("photo", Url.url_api_image+photo);
                i.putExtra("date",birth_date);
                i.putExtra("sexe",gender_view.getText().toString());
                startActivity(i);
                //finish();
            }
        }
        else Toast.makeText(getApplicationContext(),R.string.prosvp,Toast.LENGTH_SHORT).show();
    }

    public boolean haveInternetConnection(){
        // Fonction haveInternetConnection : return true si connecté, return false dans le cas contraire
        NetworkInfo network = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (network==null || !network.isConnected())
        {
            // Le périphérique n'est pas connecté à Internet
            return false;
        }

        // Le périphérique est connecté à Internet
        return true;
    }

    public void recupData(){
        //update.setEnabled(false);
        //toolbar.setEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //progressLayout.setVisibility(View.VISIBLE);
        final String url=Url.url_api+"/users/"+userId;
        simpleProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Http http=new Http(receive.this,url);
                http.setMethod("GET");
                http.sendData(null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (http.getResponse() != null) {
                                JSONObject response = new JSONObject(http.getResponse());
                                String status = response.getString("status");
                                if (status.equals("succes")) {
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    //update.setEnabled(true);
                                    //toolbar.setEnabled(true);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    //progressLayout.setVisibility(View.INVISIBLE);
                                    JSONObject user = response.getJSONObject("data");
                                    name = user.getString("name");
                                    address = user.getString("address");
                                    email = user.getString("email");
                                    usernamee = user.getString("username");
                                    permis = user.getString("permis");
                                    birth_date = user.getString("dateBirth");
                                    gender = user.getString("gender");
                                    photo = user.getString("image");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            InputStream inputStream = null;
                                            try {
                                                inputStream = new URL(Url.url_api_image + photo).openStream();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (bitmap != null) {
                                                        img.setImageBitmap(bitmap);
                                                    }
                                                }
                                            });
                                        }
                                    }).start();

                                    if (name.equals("null")) {
                                        simpleProgressBar.setVisibility(View.INVISIBLE);
                                        getSharedPreferences("Compte", MODE_PRIVATE)
                                                .edit()
                                                .putString("Type", "normal")
                                                .apply();
                                        SharedPreferences sharedPreferences = getSharedPreferences("Compte", Activity.MODE_PRIVATE);
                                        typeCompte = sharedPreferences.getString("Type", "");
                                    } else {
                                        name_view.setVisibility(View.VISIBLE);
                                        address_view.setVisibility(View.VISIBLE);
                                        email_view.setVisibility(View.VISIBLE);
                                        permis_view.setVisibility(View.VISIBLE);
                                        gender_view.setVisibility(View.VISIBLE);
                                        dateBirth_view.setVisibility(View.VISIBLE);
                                        ligne1.setVisibility(View.VISIBLE);
                                        ligne2.setVisibility(View.VISIBLE);
                                        ligne3.setVisibility(View.VISIBLE);
                                        ligne4.setVisibility(View.VISIBLE);
                                        ligne5.setVisibility(View.VISIBLE);
                                        ligne6.setVisibility(View.VISIBLE);
                                        bienvenue_view.append("  " + usernamee);
                                        address_view.setText(address);
                                        email_view.setText(email);
                                        permis_view.setText(permis);
                                        dateBirth_view.setText(birth_date);
                                        name_view.setText(name);
                                        if (gender.equals("male") || gender.equals("Men"))
                                            gender_view.setText(R.string.men);
                                        else
                                            gender_view.setText(R.string.women);
                                        if (permis.contains(" A"))
                                            permis_view.setText(R.string.cata);
                                        if (permis.contains(" B"))
                                            permis_view.setText(R.string.catb);
                                        if (permis.contains(" C"))
                                            permis_view.setText(R.string.catce);
                                        if (permis.contains(" D"))
                                            permis_view.setText(R.string.catd);


                                        simpleProgressBar.setVisibility(View.INVISIBLE);
                                        getSharedPreferences("Compte", MODE_PRIVATE)
                                                .edit()
                                                .putString("Type", "pro")
                                                .apply();
                                        SharedPreferences sharedPreferences = getSharedPreferences("Compte", Activity.MODE_PRIVATE);
                                        typeCompte = sharedPreferences.getString("Type", "");
                                    }
                                } else {
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                //Toast.makeText(getApplicationContext(),R.string.server,Toast.LENGTH_LONG).show();
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                tryAgain();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
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
                recupData();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(receive.this,SampleActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
        finish();
    }
}