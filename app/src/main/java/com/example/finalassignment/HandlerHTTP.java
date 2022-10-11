package com.example.finalassignment;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HandlerHTTP {

    private static final String TAG = HandlerHTTP.class.getSimpleName();

    public HandlerHTTP(){}

    public InputStream makeServiceCall (String reqUrl) {

        InputStream in = null;
        try{
            URL url = new URL(reqUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            in = new BufferedInputStream(conn.getInputStream());

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " +e.getMessage());
        } catch (ProtocolException e){
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e){
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log. e(TAG, "Exception: " + e.getMessage());
        }

        return in;
    }

}
