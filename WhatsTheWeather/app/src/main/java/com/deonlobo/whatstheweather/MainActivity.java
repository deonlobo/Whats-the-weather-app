package com.deonlobo.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;

    public void findWeather(View view){

        InputMethodManager mgr =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);

        String encodedCityName = null;
        try {
            encodedCityName = URLEncoder.encode(cityName.getText().toString(),"UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" +encodedCityName+"&appid=944e964d5013ea2e877c30345e647003");


        } catch (UnsupportedEncodingException e) {
            Toast.makeText(MainActivity.this, "Could Not Find Weather", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public class DownloadTask extends AsyncTask<String , Void , String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url ;
            HttpURLConnection connection = null;

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in= connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data !=-1){
                    char curr = (char) data;
                    result+= curr;
                    data=reader.read();

                }
                return result;
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Could Not Find Weather", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                String message = "";

                System.out.println(jsonObject);

                String weather = jsonObject.getString("weather");

                JSONArray jsonArray = new JSONArray(weather);

                for (int  i=0;i<jsonArray.length();i++){
                    JSONObject part = jsonArray.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main =part.getString("main");
                    description = part.getString("description");

                    if(main!="" && description !=""){
                        message += main +": " + description +"\r\n";
                    }
                }

                if (message != ""){
                    resultTextView.setText(message);
                }else{
                    Toast.makeText(MainActivity.this, "Could Not Find Weather", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, "Could Not Find Weather", Toast.LENGTH_SHORT).show();
            }


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

    }
}
