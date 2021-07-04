package com.example.todo.util;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectivityTask extends AsyncTask<Void, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Void... voids) {
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL("http://10.0.2.2:8080/").openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setRequestMethod("GET");

            conn.connect();
            conn.getInputStream();

            return true;
        } catch (Exception e){
            System.out.println("ASDF" + e);
            return false;
        }
    }
}
