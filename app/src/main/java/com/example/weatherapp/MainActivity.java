package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
  EditText cityField;
  TextView outputView;

  public void getWeather(View view) {
    String city = cityField.getText().toString();
    try {
      city = URLEncoder.encode(city, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    //hide keyboard after button click
    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    mgr.hideSoftInputFromWindow(cityField.getWindowToken(), 0);

    DownloadWeather weather = new DownloadWeather();
    if (city != "") {
      weather.execute("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=373fbc6e4bf925e474d8ef40ad84bf31");
    }
  }

  public class DownloadWeather extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
      String result = "";
      URL url;
      HttpURLConnection httpURLConnection = null;
      try {
        url = new URL(urls[0]);
        httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream in = httpURLConnection.getInputStream();
        InputStreamReader reader = new InputStreamReader(in);

        int data = reader.read();
        while (data != -1) {
          result += (char) data;
          data = reader.read();
        }
        System.out.println("RESULT: " + result);
        return result;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      try {
        JSONObject jsonObject = new JSONObject(result);
        String weatherInfo = jsonObject.getString("weather");

        JSONArray jsonArray = new JSONArray(weatherInfo);
        String output = "";
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonpart = jsonArray.getJSONObject(i);
          output += "Main: " + jsonpart.getString("main") + "\n";
          output += "Description: " + jsonpart.getString("description");
        }
        System.out.println(output);
        outputView.setText(output);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    cityField = (EditText) findViewById(R.id.city);
    outputView = (TextView) findViewById(R.id.weather);
  }

}
