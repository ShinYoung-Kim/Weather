package com.ksy.mpl;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPhotoFragment extends BottomSheetDialogFragment {

    Button saveBtn;
    TextView dateText;
    TextView temperatureText;

    public String temperature;
    public String state;

    Fragment fragment;
    Context context;

    public AddPhotoFragment(Context context) {
        this.context = context;
    }

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
        //setStateBackGround(state);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = getFragmentManager().findFragmentById(R.id.mainLayout);
                //((MainActivity)getActivity()).closeFragment(fragment);
                dismiss();
            }
        });

        return view;
    }

    public void setTemperatureText() {
        temperatureText.setText(temperature + "도");
    }

    public void setStateBackGround(String state) {
        fragment = getFragmentManager().findFragmentById(R.id.mainLayout);
        if ("맑음".equals(state)) {
            fragment.getView().setBackgroundColor(getResources().getColor(R.color.sunny));
        } else if ("흐림".equals(state)) {
            fragment.getView().setBackgroundColor(getResources().getColor(R.color.dark));
        } else if ("구름많음".equals(state)) {
            fragment.getView().setBackgroundColor(getResources().getColor(R.color.cloudy));
        } else {
            fragment.getView().setBackgroundColor(getResources().getColor(R.color.rain));
        }
    }
}