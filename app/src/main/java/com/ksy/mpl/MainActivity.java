package com.ksy.mpl;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private Chip tagOuter, tagTop, tagBottom, tagAcc;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference userDatabase;

    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherData = new WeatherData();
        weatherLayout = (ConstraintLayout) findViewById(R.id.weatherLayout);
        stateTextview = (TextView) findViewById(R.id.weatherState);
        tempTextView = (TextView) findViewById(R.id.temperature);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);

        tagOuter = (Chip) findViewById(R.id.tagOuter);
        tagTop = (Chip) findViewById(R.id.tagTop);
        tagBottom = (Chip) findViewById(R.id.tagBottom);
        tagAcc = (Chip) findViewById(R.id.tagAcc);

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

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        View navHeaderView= navigationView.inflateHeaderView(R.layout.navigation_header);

        userName = (TextView) navHeaderView.findViewById(R.id.userName);

        String uid = null;
        String id = null;
        User userInstance = User.getInstance(uid, id);
        uid = userInstance.getUid();
        id = userInstance.getId();

        userName.setText(id);

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

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoFragment.show(getSupportFragmentManager(), addPhotoFragment.getTag());
                animateFab();
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
            float temp = Float.parseFloat(weatherTemp);
            changeTags(temp);
            tempTextView.setText(weatherTemp + "???");
            addPhotoFragment.temperature = weatherTemp;
            addPhotoFragment.state = weatherState;
        }
    };

    private void changeWeatherStateBackground(String state) {
        if ("??????".equals(state)) {
            weatherLayout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.sunny));
            stateTextview.setText(getApplicationContext().getResources().getString(R.string.sunny));
            weatherIcon.setImageResource(R.drawable.sunny);
        } else if ("??????".equals(state)) {
            weatherLayout.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.dark));
            stateTextview.setText(getApplicationContext().getResources().getString(R.string.dark));
            weatherIcon.setImageResource(R.drawable.dark);
        } else if ("????????????".equals(state)) {
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
            isOpen=false;
        }
        else {
            fab.startAnimation(rotateBackward);
            isOpen=true;
        }
    }

    private void changeTags(float temperature) {
        String uid = null;
        String id = null;
        User userInstance = User.getInstance(uid, id);
        uid = userInstance.getUid();
        id = userInstance.getId();

        int temp = (int) ((int) (temperature / 5) * 5);

        //userDatabase = firebaseDatabase.getReference("Users").child("OVUC3LwGHlNvMFbVsFz0fVHhheu1");
        userDatabase = firebaseDatabase.getReference("Users").child(uid);
        DatabaseReference statistics = userDatabase.child("Statistics").child(String.valueOf(temp));
        DatabaseReference outers = statistics.child("outer");
        DatabaseReference ups = statistics.child("up");
        DatabaseReference downs = statistics.child("down");
        DatabaseReference accs = statistics.child("acc");

        String[] downClothes = {"??????", "?????????", "?????????", "?????????", "?????????", "????????????", "????????????", "?????????", "?????????", "?????????", "?????????", "??????", "???????????????", "?????????"};
        String[] outerClothes = {"??????", "??????", "??????", "??????", "????????????", "??????", "?????????", "????????????", "??????", "?????????", "??????"};
        String[] accessaryClothes = {"??????", "?????????", "?????????", "??????", "?????????", "??????", "????????????", "??????"};
        String[] upClothes = {"??????", "?????????", "??????", "?????????", "??????", "??????", "?????????", "?????????", "?????????", "?????????", "?????????", "??????", "??????"};

        changeTag(ups, upClothes, tagTop);
        changeTag(downs, downClothes, tagBottom);
        changeTag(outers, outerClothes, tagOuter);
        changeTag(accs, accessaryClothes, tagAcc);
    }

    private void changeTag(DatabaseReference statisticsByCategory, String[] clothes, Chip tag) {
        statisticsByCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                String string = "";

                for (int i = 0; i < clothes.length; i++) {
                    int currentCount = dataSnapshot.child(clothes[i]).child("wearCount").getValue(Integer.class);

                    if (currentCount > count) {
                        count = currentCount;
                        string = clothes[i];
                    }
                    tag.setText(string);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("failed change tags", "Failed to read value.", error.toException());
            }
        });
    }
}