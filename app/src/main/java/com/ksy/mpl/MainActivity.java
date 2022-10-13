package com.ksy.mpl;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private String weatherState;
    private String weatherTemp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WeatherData weatherData = new WeatherData();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    String[] dataString = weatherData.lookUpWeather();
                    weatherState = dataString[0];
                    weatherTemp = dataString[1];
                    Log.d("stringArray", dataString[0]);
                    TextView stateTextview = (TextView) findViewById(R.id.weatherState);
                    TextView tempTextView = (TextView) findViewById(R.id.temperature);

                    stateTextview.setText(weatherState);
                    tempTextView.setText(weatherTemp + "도");
                } catch(IOException| JSONException e) {
                    e.printStackTrace();
                }
            }
        });
}

}