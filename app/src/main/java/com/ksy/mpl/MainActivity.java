package com.ksy.mpl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout weatherLayout;
    TextView stateTextview;
    TextView tempTextView;
    ImageView weatherIcon;

    private String weatherState;
    private String weatherTemp;
    private WeatherData weatherData;

    FloatingActionButton fab, fab1, fab2;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;

    boolean isOpen = false;

    private FragmentManager fragmentManager;
    private AddPhotoFragment addPhotoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherData = new WeatherData();
        weatherLayout = (ConstraintLayout) findViewById(R.id.weatherLayout);
        stateTextview = (TextView) findViewById(R.id.weatherState);
        tempTextView = (TextView) findViewById(R.id.temperature);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);

        fragmentManager = getSupportFragmentManager();

        addPhotoFragment = new AddPhotoFragment(getApplicationContext());

        DrawerLayout drawerLayout = findViewById(R.id.mainLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        //NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        //NavigationUI.setupWithNavController(navigationView, navController);

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
                Bundle bundle = new Bundle();
                bundle.putString("State", weatherState);

                Message msg = handler.obtainMessage();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }.start();

        ImageSlider imageSlider = findViewById(R.id.slider);

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://i.pinimg.com/originals/21/42/0b/21420b241e3d4bb084c9200ff50c947a.jpg"));
        slideModels.add(new SlideModel("https://i.pinimg.com/originals/29/54/e0/2954e0d566fa420c1a449c04b4123464.jpg"));
        slideModels.add(new SlideModel("https://i.pinimg.com/originals/fb/2e/c3/fb2ec3b24144c92a25ce186fbe509bf2.jpg"));
        slideModels.add(new SlideModel("https://i.pinimg.com/originals/e6/ae/44/e6ae4467903e271f31f4e0d64ce29f69.jpg"));
        imageSlider.setImageList(slideModels, true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainLayout, addPhotoFragment);
                fragmentTransaction.addToBackStack("addPhoto");
                fragmentTransaction.commit();
                 */
                addPhotoFragment.show(getSupportFragmentManager(), addPhotoFragment.getTag());
                animateFab();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                Toast.makeText(MainActivity.this, "gallery upload", Toast.LENGTH_SHORT).show();
                addPhotoFragment.show(getSupportFragmentManager(), addPhotoFragment.getTag());
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                Toast.makeText(MainActivity.this, "take a photo", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, DBCheckActivity.class);
                startActivity(intent);
            }
        });

    }

    private String[] getWeatherData(WeatherData weatherData) throws JSONException, IOException {
        final String[] dataString = new String[2];
        String[] temp = weatherData.lookUpWeather();
        dataString[0] = temp[0];
        dataString[1] = temp[1];
        return dataString;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String msgString = bundle.getString("state");
            changeWeatherStateBackground(weatherState);
            tempTextView.setText(weatherTemp + "도");
            addPhotoFragment.temperature = weatherTemp;
            addPhotoFragment.state = weatherState;
        }
    };

    private void changeWeatherStateBackground(String state) {
        if ("맑음".equals(state)) {
            weatherLayout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.sunny));
            stateTextview.setText(getApplicationContext().getResources().getString(R.string.sunny));
            weatherIcon.setImageResource(R.drawable.sunny);
        } else if ("흐림".equals(state)) {
            weatherLayout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.dark));
            stateTextview.setText(getApplicationContext().getResources().getString(R.string.dark));
            weatherIcon.setImageResource(R.drawable.dark);
        } else if ("구름많음".equals(state)) {
            weatherLayout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.cloudy));
            stateTextview.setText(getApplicationContext().getResources().getString(R.string.cloudy));
            weatherIcon.setImageResource(R.drawable.cloudy);
        } else {
            weatherLayout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.rain));
            stateTextview.setText(getApplicationContext().getResources().getString(R.string.rain));
            weatherIcon.setImageResource(R.drawable.rain);
        }
    }

    private void animateFab() {
        if(isOpen) {
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isOpen=false;
        }
        else {
            fab.startAnimation(rotateBackward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isOpen=true;
        }
    }

    public void closeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}