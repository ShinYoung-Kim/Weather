package com.ksy.mpl;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout weatherLayout;
    TextView stateTextview;
    TextView tempTextView;

    private String weatherState;
    private String weatherTemp;
    private WeatherData weatherData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherData = new WeatherData();
        weatherLayout = (ConstraintLayout) findViewById(R.id.weatherLayout);
        stateTextview = (TextView) findViewById(R.id.weatherState);
        tempTextView = (TextView) findViewById(R.id.temperature);

        new Thread() {
            public void run() {
                String[] temp = new String[2];
                try {
                    temp = getWeatherData(weatherData);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                weatherState = temp[0];
                weatherTemp = temp[1];
                Log.d("thread", weatherState);
                Bundle bundle = new Bundle();
                bundle.putString("State", weatherState);

                Message msg = handler.obtainMessage();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }.start();

    }

    private String[] getWeatherData(WeatherData weatherData) throws JSONException, IOException {
        final String[] dataString = new String[2];
        String[] temp = weatherData.lookUpWeather();
        Log.d("getWeatherData", temp.toString());
        dataString[0] = temp[0];
        dataString[1] = temp[1];
        return dataString;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String msgString = bundle.getString("state");
            Log.d("handle", weatherState);
            changeWeatherStateBackground(weatherState);
            tempTextView.setText(weatherTemp + "도");
        }
    };

    private void changeWeatherStateBackground(String state) {
        Log.d("function", state);
        if (state.equals("맑음")) {
            weatherLayout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.sunny));
            stateTextview.setText(getApplicationContext().getResources().getString(R.string.sunny));
        } else if (state.equals("흐림")) {
            weatherLayout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.dark));
            stateTextview.setText(getApplicationContext().getResources().getString(R.string.dark));
        } else if (state.equals("구름많음")) {
            weatherLayout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.cloudy));
            stateTextview.setText(getApplicationContext().getResources().getString(R.string.cloudy));
        } else {
            weatherLayout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.rain));
            stateTextview.setText(getApplicationContext().getResources().getString(R.string.rain));
        }
    }
}