package com.sohrab.obd.reader;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Http {

    Context context;
    private String url,method="GET",data=null,response=null;
    private Integer statusCode=0;
    private Boolean token=false;

    public Http(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    public String getResponse() {
        return response;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getData() {
        return data;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setToken(Boolean token) {
        this.token = token;
    }

    public void sendData(String token_key){

        try {
            URL surl= new URL(url);
            HttpURLConnection httpURLConnection= (HttpURLConnection)surl.openConnection();
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setRequestProperty("Content-Type","application/json");
            httpURLConnection.setRequestProperty("X-Request-With","XMLHttpRequest");
            if(token){
                httpURLConnection.setRequestProperty("Authorization","Bearer "+ token_key);
            }

            /*if(!method.equals("GET"))
                httpURLConnection.setDoOutput(true);*/
            if(data!=null){
                OutputStream outputStream=httpURLConnection.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            statusCode=httpURLConnection.getResponseCode();
            InputStreamReader isr;
            if(statusCode>=200 || statusCode<=299){
                isr=new InputStreamReader(httpURLConnection.getInputStream());
            }
            else{
                isr=new InputStreamReader(httpURLConnection.getErrorStream());
            }
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuffer sb=new StringBuffer();
            String line;
            while ((line=bufferedReader.readLine())!=null){
                sb.append(line);
            }
            bufferedReader.close();
            response= sb.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
