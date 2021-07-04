package com.example.todo.util;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class Connection extends AsyncTask<String, Boolean, Boolean> {

    @Override
    public Boolean doInBackground(String... strings) {
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL("http://10.0.2.2:8080").openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            conn.getInputStream();

            System.out.println("Successful connection");
            return true;
        } catch (Exception e){
            System.out.println("Error in connection" + e);
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
