package com.sohrab.obd.reader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class update extends AppCompatActivity {
    static final int DATE_DIALOG_ID_S1 = 0;
    public int year, month, day;
    private int mYear, mMonth, mDay;
    Toolbar toolbar;
    TextView dateNais;
    String sexeSelected="";
    Spinner spinnerSexe;
    EditText nom,adress,email,username,password;
    String permis="";
    Button update;
    private int PICK_IMAGE_REQUEST = 1;
    private ImageButton buttonChoose;
    private CircleImageView imageView;
    ProgressBar simpleProgressBar;
    private Bitmap bitmap_recup;
    private Uri filePath;
    String photo;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-zA-Z0-9._-]+";
    boolean connexion;
    String userId;
    ConstraintLayout first,second,progressLayout;
    AlertDialog.Builder builder;
    String color;

    public update() {
        // Assign current Date and Time Values to Variables
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

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
        setContentView(R.layout.activity_update);
        first=findViewById(R.id.first_back);
        second=findViewById(R.id.second_back);
        progressLayout=findViewById(R.id.progresslayout);
        if(modeDark.equals("1")){
            first.setBackgroundResource(R.drawable.first_back_dark);
            second.setBackgroundResource(R.drawable.second_back_dark);
        }
        if(modeDark.equals("0")){
            first.setBackgroundResource(R.drawable.first_back_light);
            second.setBackgroundResource(R.drawable.second_back_light);
        }
        nom=findViewById(R.id.txtName);
        adress=findViewById(R.id.txtAdd);
        email=findViewById(R.id.txtEmail);
        username=findViewById(R.id.txtUser);
        password=findViewById(R.id.txtPass);
        buttonChoose = (ImageButton) findViewById(R.id.buttonChoose);
        imageView =  findViewById(R.id.imageView);
        dateNais=findViewById(R.id.txtDateNaissUpdate);
        builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        nom.setText(getIntent().getStringExtra("name"));
        adress.setText(getIntent().getStringExtra("adress"));
        email.setText(getIntent().getStringExtra("email"));
        username.setText(getIntent().getStringExtra("username"));
        dateNais.setText(getIntent().getStringExtra("date"));
        permis=getIntent().getStringExtra("permis");
        sexeSelected=getIntent().getStringExtra("sexe");
        photo=getIntent().getStringExtra("photo");
        userId=getIntent().getStringExtra("userId");
        simpleProgressBar=findViewById(R.id.progRec);
        toolbar=findViewById(R.id.myToolBarBack);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.update));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream= null;
                try {
                    inputStream = new URL(photo).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bitmap != null){
                            imageView.setImageBitmap(bitmap);
                            bitmap_recup=bitmap;
                        }
                    }
                });
            }
        }).start();

        update=findViewById(R.id.update);
        simpleProgressBar=findViewById(R.id.progUpd);
        simpleProgressBar.setVisibility(View.INVISIBLE);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserData();
            }
        });
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        final Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinnerSexe = (Spinner) findViewById(R.id.spinnerSexeUpdate);

        String[] plants = new String[]{
                getResources().getString(R.string.permiscategorie),
                getResources().getString(R.string.cata),
                getResources().getString(R.string.catb),
                getResources().getString(R.string.catd),
                getResources().getString(R.string.catce)
        };

        String[] sexe = new String[]{
                getResources().getString(R.string.sex),
                getResources().getString(R.string.men),
                getResources().getString(R.string.women),
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));
        final List<String> sexeList = new ArrayList<>(Arrays.asList(sexe));

        final  ArrayAdapter<String> sexeArrayAdapter=new ArrayAdapter<String>(this,R.layout.sexespinneritem,sexeList){

            @Override
            public boolean isEnabled(int position) {
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(R.color.rahma);
                }
                return view;
            }
        };

        sexeArrayAdapter.setDropDownViewResource(R.layout.sexespinneritem);
        spinnerSexe.setAdapter(sexeArrayAdapter);

        spinnerSexe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    sexeSelected=selectedItemText.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,plantsList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @SuppressLint("ResourceAsColor")
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(R.color.rahma);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    permis=selectedItemText.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(spinnerArrayAdapter.getPosition(permis));
        spinnerSexe.setSelection(sexeArrayAdapter.getPosition(sexeSelected));

        dateNais.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the DatePickerDialog
                showDialog(DATE_DIALOG_ID_S1);
            }
        });


    }
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                // the callback received when the user "sets" the Date in the DatePickerDialog
                public void onDateSet(DatePicker view, int yearSelected,
                                      int monthOfYear, int dayOfMonth) {
                    year = yearSelected;
                    month = monthOfYear;
                    day = dayOfMonth;
                    // Set the Selected Date in Select date Button
                    dateNais.setText( year + "-" + (month+1) + "-" + day);
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID_S1:
                // create a new DatePickerDialog with values you want to show
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);}
        return null;
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap_recup = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap_recup);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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

    public void updateUserData(){
       // update.setEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //progressLayout.setVisibility(View.VISIBLE);
        simpleProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connexion=isOnline();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!connexion){
                            NotificationHelper.showNotificatioon(getApplicationContext(),getResources().getString(R.string.warning),getResources().getString(R.string.nocnx));
                            simpleProgressBar.setVisibility(View.INVISIBLE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            //progressLayout.setVisibility(View.INVISIBLE);
                        }
                        else{
                            String nomtxt = nom.getText().toString();
                            String adrtxt = adress.getText().toString();
                            String emailtxt = email.getText().toString();
                            String usertxt = username.getText().toString();
                            String userpwd = password.getText().toString();
                            String date=dateNais.getText().toString();
                            if (nomtxt.equals("") || adrtxt.equals("") || emailtxt.equals("") || usertxt.equals("") ||
                                    permis.equals("")|| userpwd.equals("") || date.equals("") || sexeSelected.equals("")) {
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), R.string.please, Toast.LENGTH_LONG).show();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                //progressLayout.setVisibility(View.INVISIBLE);
                            }
                            else if (! emailtxt.trim().matches(emailPattern)){
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), R.string.xmail, Toast.LENGTH_LONG).show();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                //progressLayout.setVisibility(View.INVISIBLE);
                            }
                            else{
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                //progressLayout.setVisibility(View.INVISIBLE);
                                JSONObject params=new JSONObject();
                                try {
                                    params.put("email",emailtxt);
                                    params.put("name",nomtxt);
                                    params.put("password",userpwd);
                                    params.put("dateBirth",date);
                                    params.put("gender",sexeSelected);
                                    params.put("address",adrtxt);
                                    params.put("username",usertxt);
                                    params.put("permis",permis);
                                    params.put("image",getStringImage(bitmap_recup));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String data=params.toString();
                                final String url=Url.url_api+"/update/"+userId;

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferences sharedPreferencesLogin=getSharedPreferences("login", MODE_PRIVATE);
                                        final String token_key = sharedPreferencesLogin.getString("token", "");
                                        final Http http=new Http(update.this,url);
                                        http.setMethod("PUT");
                                        http.setData(data);
                                        http.setToken(true);
                                        http.sendData(token_key);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                JSONObject response= null;
                                                try {
                                                    if(http.getResponse()!=null) {
                                                        response = new JSONObject(http.getResponse());
                                                        String status = response.getString("status");
                                                        if (status.equals("succes")) {
                                                            simpleProgressBar.setVisibility(View.INVISIBLE);
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            //progressLayout.setVisibility(View.INVISIBLE);
                                                            Intent i = new Intent(update.this, receive.class);
                                                            i.putExtra("userId", userId);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                    }
                                                    else{
                                                        //Toast.makeText(getApplicationContext(),R.string.server,Toast.LENGTH_LONG).show();
                                                        simpleProgressBar.setVisibility(View.INVISIBLE);
                                                        //update.setEnabled(true);
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        //progressLayout.setVisibility(View.INVISIBLE);
                                                        tryAgain();
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        });
                                    }
                                }).start();
                            }
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
                updateUserData();
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
        finish();
    }
}
