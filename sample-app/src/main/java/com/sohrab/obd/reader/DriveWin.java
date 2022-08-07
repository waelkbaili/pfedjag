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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import info.hoang8f.android.segmented.SegmentedGroup;

public class DriveWin extends AppCompatActivity {
    static final int DATE_DIALOG_ID_S1 = 0;
    public int year, month, day;
    private int mYear, mMonth, mDay;
    Button _btnReg;
    EditText _txtName, _txtAdd, _txtEmail, _txtUser, _txtPass;
    TextView dateNais;
    String permis="";
    String sexeSelected="";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-zA-Z0-9._-]+";
    String email="";
    ProgressBar simpleProgressBar;
    SegmentedGroup radioGroup;
    RadioButton radioButton;
    String compte="Proffessionel";
     Spinner spinner;
     Spinner spinnerSexe;
     boolean connexion;
     ConstraintLayout first,second,progress_layout;
     View ligne;
    AlertDialog.Builder builder;
    String color;

    public DriveWin() {
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
        setContentView(R.layout.activity_drive_win);
        first=findViewById(R.id.first_back);
        second=findViewById(R.id.second_back);
        if(modeDark.equals("1")){
            first.setBackgroundResource(R.drawable.first_back_dark);
            second.setBackgroundResource(R.drawable.second_back_dark);
        }
        if(modeDark.equals("0")){
            first.setBackgroundResource(R.drawable.first_back_light);
            second.setBackgroundResource(R.drawable.second_back_light);
        }
        progress_layout=findViewById(R.id.progresslayout);
        _btnReg = (Button) findViewById(R.id.btnReg);
        _txtName = (EditText) findViewById(R.id.txtName);
        _txtAdd = (EditText) findViewById(R.id.txtAdd);
        _txtEmail = (EditText) findViewById(R.id.txtEmail);
        _txtUser = (EditText) findViewById(R.id.txtUser);
        _txtPass = (EditText) findViewById(R.id.txtPass);
        ligne=findViewById(R.id.ligne);
        simpleProgressBar =  findViewById(R.id.progReg);
        simpleProgressBar.setVisibility(View.INVISIBLE);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setTintColor(Color.parseColor("#4D7D8D"), Color.parseColor("#FFFFFF"));
        dateNais=findViewById(R.id.txtDateNaiss);
        builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        _txtEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //_txtEmail.setTextColor(R.attr.firstTxtColor);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });

        _btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
         spinner = (Spinner) findViewById(R.id.spinner1);
        spinnerSexe = (Spinner) findViewById(R.id.spinnerSexe);
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
                    tv.setTextColor(Color.BLACK);
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
                    tv.setTextColor(Color.BLACK);
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

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
       compte= (String) radioButton.getText();
       if(compte.equals("Normal")||compte.equals("Normale")||compte.equals("حساب عادي")){
           _txtAdd.setVisibility(View.INVISIBLE);
           _txtName.setVisibility(View.INVISIBLE);
           _txtUser.setVisibility(View.INVISIBLE);
           dateNais.setVisibility(View.INVISIBLE);
           spinner.setVisibility(View.INVISIBLE);
           spinnerSexe.setVisibility(View.INVISIBLE);
           ligne.setVisibility(View.INVISIBLE);
           SharedPreferences.Editor editor=getSharedPreferences("Compte",MODE_PRIVATE).edit();
           editor.putString("Type","normal");
           editor.apply();
       }
       else{
           compte="Proffessionel";
           _txtAdd.setVisibility(View.VISIBLE);
           _txtName.setVisibility(View.VISIBLE);
           _txtUser.setVisibility(View.VISIBLE);
           dateNais.setVisibility(View.VISIBLE);
           spinner.setVisibility(View.VISIBLE);
           spinnerSexe.setVisibility(View.VISIBLE);
           ligne.setVisibility(View.VISIBLE);
           SharedPreferences.Editor editor=getSharedPreferences("Compte",MODE_PRIVATE).edit();
           editor.putString("Type","pro");
           editor.apply();
       }
    }

    public void tryAgain(){
        //Setting message manually and performing action on button click
        builder.setMessage(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.requested_data_could_not_be_loaded)+"</font>")).
                setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#4D7D8D'>"+
                getResources().getString(R.string.try_again)+"</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                register();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        //Setting the title manually
        alert.setTitle(Html.fromHtml("<font color="+color+">"+getResources().getString(R.string.something_went_wrong)+"</font>"));
        alert.show();
    }

    public void register(){
        /*_txtAdd.setEnabled(false);
        _txtEmail.setEnabled(false);
        _txtPass.setEnabled(false);
        _txtName.setEnabled(false);
        _txtUser.setEnabled(false);
        dateNais.setEnabled(false);
        spinner.setEnabled(false);
        spinnerSexe.setEnabled(false);
        //radioButton.setEnabled(false);*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //progress_layout.setVisibility(View.VISIBLE);
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
                            /*_txtAdd.setEnabled(true);
                            _txtEmail.setEnabled(true);
                            _txtPass.setEnabled(true);
                            _txtName.setEnabled(true);
                            _txtUser.setEnabled(true);
                            dateNais.setEnabled(true);
                            spinner.setEnabled(true);
                            spinnerSexe.setEnabled(true);
                            //radioButton.setEnabled(true);*/
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            //progress_layout.setVisibility(View.INVISIBLE);
                        }
                        else{
                            if (_txtEmail.getText().toString().trim().matches(emailPattern)) {
                                email = _txtEmail.getText().toString();
                            } else {
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.xmail), Toast.LENGTH_SHORT).show();
                                //_txtEmail.setTextColor(Color.RED);
                                /*_txtAdd.setEnabled(true);
                                _txtEmail.setEnabled(true);
                                _txtPass.setEnabled(true);
                                _txtName.setEnabled(true);
                                _txtUser.setEnabled(true);
                                dateNais.setEnabled(true);
                                spinner.setEnabled(true);
                                spinnerSexe.setEnabled(true);*/
                                //radioButton.setEnabled(true);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                //progress_layout.setVisibility(View.INVISIBLE);
                            }
                            if(compte.equals("Proffessionel")){
                                String name = _txtName.getText().toString();
                                String address = _txtAdd.getText().toString();
                                String username = _txtUser.getText().toString();
                                String password = _txtPass.getText().toString();
                                String dateNaissance=dateNais.getText().toString();
                                if(name.equals("")||address.equals("")||email.equals("")||
                                        username.equals("")||password.equals("")|| permis.equals("")||
                                        dateNaissance.equals("") || sexeSelected.equals("")){
                                    /*_txtAdd.setEnabled(true);
                                    _txtEmail.setEnabled(true);
                                    _txtPass.setEnabled(true);
                                    _txtName.setEnabled(true);
                                    _txtUser.setEnabled(true);
                                    dateNais.setEnabled(true);
                                    spinner.setEnabled(true);
                                    spinnerSexe.setEnabled(true);*/
                                    //radioButton.setEnabled(true);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    //progress_layout.setVisibility(View.INVISIBLE);
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.please), Toast.LENGTH_LONG).show();
                                }
                                else {
                                    JSONObject params=new JSONObject();
                                    try {
                                        params.put("email",email);
                                        params.put("gender",sexeSelected+"");
                                        params.put("dateBirth",dateNaissance+"");
                                        params.put("address",address);
                                        params.put("username",username);
                                        params.put("permis",permis);
                                        params.put("password",password);
                                        params.put("name",name);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    final String data=params.toString();
                                    final String url=Url.url_api+"/register";
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final Http http=new Http(DriveWin.this,url);
                                            http.setMethod("POST");
                                            http.setData(data);
                                            http.sendData(null);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        JSONObject response=new JSONObject(http.getResponse());
                                                        String status=response.getString("status");
                                                        if(status.equals("succes")){
                                                            simpleProgressBar.setVisibility(View.INVISIBLE);
                                                            SharedPreferences.Editor editorAcces=getSharedPreferences("param",MODE_PRIVATE).edit();
                                                            editorAcces.putString("gps","1");
                                                            editorAcces.putString("obd","0");
                                                            editorAcces.apply();
                                                            Intent i = new Intent(DriveWin.this, login.class);
                                                            startActivity(i);
                                                            finish();
                                                        }
                                                        else {
                                                            /*_txtAdd.setEnabled(true);
                                                            _txtEmail.setEnabled(true);
                                                            _txtPass.setEnabled(true);
                                                            _txtName.setEnabled(true);
                                                            _txtUser.setEnabled(true);
                                                            dateNais.setEnabled(true);
                                                            spinner.setEnabled(true);
                                                            spinnerSexe.setEnabled(true);*/
                                                            //radioButton.setEnabled(true);
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            //progress_layout.setVisibility(View.INVISIBLE);
                                                            simpleProgressBar.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getApplicationContext(),status,Toast.LENGTH_LONG).show();
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
                            else{
                                String password = _txtPass.getText().toString();
                                if(email.equals("")|| password.equals("")){
                                    /*_txtAdd.setEnabled(true);
                                    _txtEmail.setEnabled(true);
                                    _txtPass.setEnabled(true);
                                    _txtName.setEnabled(true);
                                    _txtUser.setEnabled(true);
                                    dateNais.setEnabled(true);
                                    spinner.setEnabled(true);
                                    spinnerSexe.setEnabled(true);*/
                                    //radioButton.setEnabled(true);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    //progress_layout.setVisibility(View.INVISIBLE);
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.please), Toast.LENGTH_LONG).show();
                                }
                                else{

                                    JSONObject params=new JSONObject();
                                    try {
                                        params.put("email",email);
                                        params.put("password",password);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    final String data=params.toString();
                                    final String url=Url.url_api+"/register";
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final Http http=new Http(DriveWin.this,url);
                                            http.setMethod("POST");
                                            http.setData(data);
                                            http.sendData(null);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        if(http.getResponse()!=null){
                                                            JSONObject response=new JSONObject(http.getResponse());
                                                            String status=response.getString("status");
                                                            if(status.equals("succes")){
                                                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                                                SharedPreferences.Editor editorAcces=getSharedPreferences("param",MODE_PRIVATE).edit();
                                                                editorAcces.putString("gps","1");
                                                                editorAcces.putString("obd","0");
                                                                editorAcces.apply();
                                                                Intent i = new Intent(DriveWin.this, login.class);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                            else {
                                                                /*_txtAdd.setEnabled(true);
                                                                _txtEmail.setEnabled(true);
                                                                _txtPass.setEnabled(true);
                                                                _txtName.setEnabled(true);
                                                                _txtUser.setEnabled(true);
                                                                dateNais.setEnabled(true);
                                                                spinner.setEnabled(true);
                                                                spinnerSexe.setEnabled(true);*/
                                                                // radioButton.setEnabled(true);
                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                //progress_layout.setVisibility(View.INVISIBLE);
                                                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                                                if(status.equals("Email existe"))
                                                                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.already_mail),Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                        else{
                                                            /*_txtAdd.setEnabled(true);
                                                            _txtEmail.setEnabled(true);
                                                            _txtPass.setEnabled(true);
                                                            _txtName.setEnabled(true);
                                                            _txtUser.setEnabled(true);
                                                            dateNais.setEnabled(true);
                                                            spinner.setEnabled(true);
                                                            spinnerSexe.setEnabled(true);*/
                                                            //radioButton.setEnabled(true);
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            //progress_layout.setVisibility(View.INVISIBLE);
                                                            //Toast.makeText(getApplicationContext(),getResources().getString(R.string.server),Toast.LENGTH_LONG).show();
                                                            simpleProgressBar.setVisibility(View.INVISIBLE);
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

                    }
                });
            }
        }).start();
    }
}
