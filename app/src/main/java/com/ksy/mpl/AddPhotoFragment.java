package com.ksy.mpl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPhotoFragment extends Fragment {

    Button saveBtn;
    TextView dateText;
    TextView temperatureText;

    public String temperature;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_photo, container, false);

        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        dateText = (TextView) view.findViewById(R.id.dateText);
        temperatureText = (TextView) view.findViewById(R.id.temperatureText);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String baseDate = dateFormat.format(date);

        dateText.setText(baseDate);
        setTemperatureText();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = getFragmentManager().findFragmentById(R.id.mainLayout);
                ((MainActivity)getActivity()).closeFragment(fragment);
            }
        });

        return view;
    }

    public void setTemperatureText() {
        temperatureText.setText(temperature + "도");
    }
}