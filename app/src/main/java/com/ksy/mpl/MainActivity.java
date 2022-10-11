package com.ksy.mpl;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private String weatherState = "";
    private Double weatherTemp = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WeatherData weatherData = new WeatherData();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    weatherData.lookUpWeather();
                } catch(IOException| JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        weatherState = weatherData.getWeatherState();
        weatherTemp = weatherData.getWeatherTemp();
        TextView stateTextview = (TextView) findViewById(R.id.weatherState);
        TextView tempTextView = (TextView) findViewById(R.id.temperature);

        stateTextview.setText(weatherState);
        tempTextView.setText(weatherTemp + "도");
    }

}