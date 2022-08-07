package com.sohrab.obd.reader;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundTask extends AsyncTask<String, String, String> {
    String result;
    Context context;
    BackgroundTask(Context ctx){
        context=ctx;
    }

    @Override
    protected String doInBackground(String... strings) {
        String type=strings[0];
        //authentification
        String loginURL=Url.url+"/login.php";
        //créer un compte
        String regURL=Url.url+"/registration.php";
        //modification des données personels d'un compte compte
        String updURL=Url.url+"/Update.php";
        //envoyer données OBD et GPS vers BD
        String sendURL=Url.url+"/sendData.php";
        //modification de mode automatique
        String UpdSpecURL=Url.url+"/updateSpec.php";
        //modification temporaire
        String UpdSpecURL2=Url.url+"/updateSpec2.php";
        //enregistrer un trip
        String tripURL=Url.url+"/sendTrip.php";
        //ajouter une véhicule
        String carURL=Url.url+"/addCar.php";
        //ajouter live trip
        String addLiveURL=Url.url+"/addLiveTrip.php";
        //supprimer live Trip
        String deleteLiveURL=Url.url+"/deleteLiveTrip.php";



        if(type.equals("reg")){
            String cin=strings[1];
            String name= strings[2];
            String address=strings[3];
            String email= strings[4];
            String username=strings[5];
            String password=strings[6];
            String permis=strings[7];
            String prenom=strings[8];
            String date=strings[9];
            String sex=strings[10];


            try{
                URL url= new URL(regURL);
                try{
                    HttpURLConnection httpURLConnection= (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream= httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter= new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String insert_data = URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")+
                            "&"+URLEncoder.encode("address", "UTF-8")+"="+URLEncoder.encode(address, "UTF-8")+
                            "&"+URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8")+
                            "&"+URLEncoder.encode("username", "UTF-8")+"="+URLEncoder.encode(username, "UTF-8")+
                            "&"+URLEncoder.encode("cin", "UTF-8")+"="+URLEncoder.encode(cin, "UTF-8")+
                            "&"+URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8")+
                            "&"+URLEncoder.encode("permis", "UTF-8")+"="+URLEncoder.encode(permis, "UTF-8")+
                            "&"+URLEncoder.encode("dateNais", "UTF-8")+"="+URLEncoder.encode(date, "UTF-8")+
                            "&"+URLEncoder.encode("sex", "UTF-8")+"="+URLEncoder.encode(sex, "UTF-8")+
                            "&"+URLEncoder.encode("prenom", "UTF-8")+"="+URLEncoder.encode(prenom, "UTF-8");
                    bufferedWriter.write(insert_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream= httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //String result="";
                    String line="";
                    StringBuilder stringBuilder= new StringBuilder();
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuilder.append(line).append("\n");

                    }
                    result=stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else if(type.equals("login")) {
            String user_cin = strings[1];
            String pass_word = strings[2];
            try {
                URL url = new URL(loginURL);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String login_data = URLEncoder.encode("usercin", "UTF-8") + "=" + URLEncoder.encode(user_cin, "UTF-8") +
                            "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass_word, "UTF-8");
                    bufferedWriter.write(login_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //String result = "";
                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");

                    }
                    result = stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else if(type.equals("upd")){
            String cin=strings[1];
            String name= strings[2];
            String address=strings[3];
            String email= strings[4];
            String username=strings[5];
            String image=strings[6];
            String permis=strings[7];
            String prenom=strings[8];
            String password=strings[9];
            String date=strings[10];
            String sexe=strings[11];


            try{
                URL url= new URL(updURL);
                try{
                    HttpURLConnection httpURLConnection= (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream= httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter= new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String update_data = URLEncoder.encode("UserName", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")+
                            "&"+URLEncoder.encode("UserAdress", "UTF-8")+"="+URLEncoder.encode(address, "UTF-8")+
                            "&"+URLEncoder.encode("Usermail", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8")+
                            "&"+URLEncoder.encode("Userusername", "UTF-8")+"="+URLEncoder.encode(username, "UTF-8")+
                            "&"+URLEncoder.encode("image", "UTF-8")+"="+URLEncoder.encode(image, "UTF-8")+
                            "&"+URLEncoder.encode("userCin", "UTF-8")+"="+URLEncoder.encode(cin, "UTF-8")+
                            "&"+URLEncoder.encode("permis", "UTF-8")+"="+URLEncoder.encode(permis, "UTF-8")
                            +"&"+URLEncoder.encode("prenom", "UTF-8")+"="+URLEncoder.encode(prenom, "UTF-8")
                            +"&"+URLEncoder.encode("date", "UTF-8")+"="+URLEncoder.encode(date, "UTF-8")
                            +"&"+URLEncoder.encode("sexe", "UTF-8")+"="+URLEncoder.encode(sexe, "UTF-8")
                            +"&"+URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8");
                    bufferedWriter.write(update_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream= httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //String result="";
                    String line="";
                    StringBuilder stringBuilder= new StringBuilder();
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuilder.append(line).append("\n");

                    }
                    result=stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else if(type.equals("send")){
            String cin = strings[1];String latitude = strings[2];String longitude = strings[3];String Speed = strings[4];
            String EngineRpm = strings[5];String engineLoad = strings[6];String ambientAirTemp = strings[7];String throttlePos = strings[8];
            String insFuel = strings[9];String time= strings[10];String valX= strings[11];String valY= strings[12];String valZ= strings[13];
            String zone= strings[14];

            try{
                URL url= new URL(sendURL);
                try{
                    HttpURLConnection httpURLConnection= (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream= httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter= new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String send_data = URLEncoder.encode("cin", "UTF-8") + "=" + URLEncoder.encode(cin, "UTF-8")
                            + "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8")
                            + "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8")
                            + "&" + URLEncoder.encode("Speed", "UTF-8") + "=" + URLEncoder.encode(Speed, "UTF-8")
                            + "&" + URLEncoder.encode("EngineRpm", "UTF-8") + "=" + URLEncoder.encode(EngineRpm, "UTF-8")
                            + "&" + URLEncoder.encode("EngineLoad", "UTF-8") + "=" + URLEncoder.encode(engineLoad, "UTF-8")
                            + "&" + URLEncoder.encode("AmbientAirTemp", "UTF-8") + "=" + URLEncoder.encode(ambientAirTemp, "UTF-8")
                            + "&" + URLEncoder.encode("ThrottlePos", "UTF-8") + "=" + URLEncoder.encode(throttlePos, "UTF-8")
                            + "&" + URLEncoder.encode("insFuel", "UTF-8") + "=" + URLEncoder.encode(insFuel, "UTF-8")
                            + "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8")
                             + "&" + URLEncoder.encode("valX", "UTF-8") + "=" + URLEncoder.encode(valX, "UTF-8")
                             + "&" + URLEncoder.encode("valY", "UTF-8") + "=" + URLEncoder.encode(valY, "UTF-8")
                            + "&" + URLEncoder.encode("valZ", "UTF-8") + "=" + URLEncoder.encode(valZ, "UTF-8")
                             + "&" + URLEncoder.encode("zone", "UTF-8") + "=" + URLEncoder.encode(zone, "UTF-8");

                    bufferedWriter.write(send_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream= httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //String result="";
                    String line="";
                    StringBuilder stringBuilder= new StringBuilder();
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuilder.append(line).append("\n");

                    }
                    result=stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals("updSpec")){
            String mode=strings[1];
            String cin=strings[2];
            try{
                URL url= new URL(UpdSpecURL);
                try{
                    HttpURLConnection httpURLConnection= (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream= httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter= new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String updSpec_data = URLEncoder.encode("mode", "UTF-8")+"="+URLEncoder.encode(mode, "UTF-8")+
                            "&"+URLEncoder.encode("CIN", "UTF-8")+"="+URLEncoder.encode(cin, "UTF-8");
                    bufferedWriter.write(updSpec_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream= httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //String result="";
                    String line="";
                    StringBuilder stringBuilder= new StringBuilder();
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuilder.append(line).append("\n");

                    }
                    result=stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
        else if(type.equals("trip")){
            String cin=strings[1];
            String dateDeb= strings[2];
            String dateFin=strings[3];
            String vin=strings[4];


            try{
                URL url= new URL(tripURL);
                try{
                    HttpURLConnection httpURLConnection= (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream= httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter= new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String insert_trip = URLEncoder.encode("cin", "UTF-8")+"="+URLEncoder.encode(cin, "UTF-8")+
                            "&"+URLEncoder.encode("dateDeb", "UTF-8")+"="+URLEncoder.encode(dateDeb, "UTF-8")+
                            "&"+URLEncoder.encode("vin", "UTF-8")+"="+URLEncoder.encode(vin, "UTF-8")+
                            "&"+URLEncoder.encode("dateFin", "UTF-8")+"="+URLEncoder.encode(dateFin, "UTF-8");
                    bufferedWriter.write(insert_trip);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream= httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //String result="";
                    String line="";
                    StringBuilder stringBuilder= new StringBuilder();
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuilder.append(line).append("\n");

                    }
                    result=stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals("updSpec2")){
            String time=strings[1];
            String location=strings[2];
            try{
                URL url= new URL(UpdSpecURL2);
                try{
                    HttpURLConnection httpURLConnection= (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream= httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter= new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String updSpec2_data = URLEncoder.encode("time", "UTF-8")+"="+URLEncoder.encode(time, "UTF-8")+
                            "&"+URLEncoder.encode("location", "UTF-8")+"="+URLEncoder.encode(location, "UTF-8");
                    bufferedWriter.write(updSpec2_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream= httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //String result="";
                    String line="";
                    StringBuilder stringBuilder= new StringBuilder();
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuilder.append(line).append("\n");

                    }
                    result=stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
        else if(type.equals("car")) {
            String cin = strings[1];
            String vin = strings[2];
            try {
                URL url = new URL(carURL);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String car_data = URLEncoder.encode("cin", "UTF-8") + "=" + URLEncoder.encode(cin, "UTF-8") +
                            "&" + URLEncoder.encode("vin", "UTF-8") + "=" + URLEncoder.encode(vin, "UTF-8");
                    bufferedWriter.write(car_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //String result = "";
                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");

                    }
                    result = stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else if(type.equals("addLive")) {
            String cin = strings[1];
            String vin = strings[2];
            String dateDebut=strings[3];
            try {
                URL url = new URL(addLiveURL);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String live_data = URLEncoder.encode("cin", "UTF-8") + "=" + URLEncoder.encode(cin, "UTF-8") +
                            "&" + URLEncoder.encode("vin", "UTF-8") + "=" + URLEncoder.encode(vin, "UTF-8")+
                            "&" + URLEncoder.encode("dateDeb", "UTF-8") + "=" + URLEncoder.encode(dateDebut, "UTF-8");
                    bufferedWriter.write(live_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //String result = "";
                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");

                    }
                    result = stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else if(type.equals("deleteLive")) {
            String cin = strings[1];
            try {
                URL url = new URL(deleteLiveURL);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    String live_delete = URLEncoder.encode("cin", "UTF-8") + "=" + URLEncoder.encode(cin, "UTF-8");
                    bufferedWriter.write(live_delete);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    //String result = "";
                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");

                    }
                    result = stringBuilder.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }
}
