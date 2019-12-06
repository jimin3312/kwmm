package com.kwmm0;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kwmm0.CallBackInterface.HttpCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection extends AsyncTask<JSONObject, Void, JSONObject> {
    public static String profileURL = "http://kwmm.kr:8080/kwMM/img/profile/default";
    public static String reviewURL = "http://kwmm.kr:8080/kwMM/img/review/default";
    String serverURL = "http://kwmm.kr:8080/kwMM/Main2";

    Context currentContext;
    HttpURLConnection httpURLConnection;
    HttpCallBack callBack;


    public HttpConnection(Context currentContext, HttpCallBack callBack)
    {
        this.callBack = callBack;
        this.currentContext = currentContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(JSONObject ... jsonObjects) {

        JSONObject jsonObject= jsonObjects[0];
        try {

            URL url = new URL(serverURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            httpURLConnection.setDoOutput(true);

            httpURLConnection.setDoInput(true);

            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            int responseStatusCode = httpURLConnection.getResponseCode();

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }


            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
            Log.d("TAG",sb.toString());
            bufferedReader.close();
            inputStreamReader.close();
            httpURLConnection.disconnect();
            return (new JSONObject(sb.toString()));

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jo = new JSONObject();
            try {
                jo.put("data"," 에러");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return jo;
        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(JSONObject resultJSON) {
        super.onPostExecute(resultJSON);
        callBack.callBack(resultJSON);
    }
}