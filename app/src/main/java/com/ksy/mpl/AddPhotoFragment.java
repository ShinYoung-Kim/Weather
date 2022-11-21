package com.ksy.mpl;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddPhotoFragment extends BottomSheetDialogFragment {

    Button saveBtn;
    TextView dateText;
    TextView temperatureText;

    public String temperature;
    public String state;

    Fragment fragment;
    Context context;

    ChipGroup upChipGroup, downChipGroup, outerChipGroup, rateChipGroup;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

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

        upChipGroup = (ChipGroup) view.findViewById(R.id.upChipGroup);
        downChipGroup = (ChipGroup) view.findViewById(R.id.downChipGroup);
        outerChipGroup = (ChipGroup) view.findViewById(R.id.outerChipGroup);
        rateChipGroup = (ChipGroup) view.findViewById(R.id.rateChipGroup);

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

                Data data = new Data();
                data.date = baseDate;
                //data.photoURL = url.getText().toString();
                //data.rate = Integer.parseInt(rate.getText().toString());
                data.weather = temperatureText.getText().toString();
                firebaseDatabase.getReference("/data").child(baseDate).push().setValue(data);

                Fashion fashion = new Fashion();

                fashion.date = baseDate;

                List<Integer> upIds = upChipGroup.getCheckedChipIds();
                List<Integer> downIds = downChipGroup.getCheckedChipIds();
                List<Integer> outerIds = outerChipGroup.getCheckedChipIds();
                int rateId = rateChipGroup.getCheckedChipId();

                List<Cloth> clothList = new ArrayList<>();

                for (Integer id : upIds) {
                    Chip chip = upChipGroup.findViewById(id);
                    Cloth cloth = new Cloth("up", chip.getText().toString());
                    clothList.add(cloth);
                }

                for (Integer id : downIds) {
                    Chip chip = downChipGroup.findViewById(id);
                    Cloth cloth = new Cloth("down", chip.getText().toString());
                    clothList.add(cloth);
                }

                for (Integer id : outerIds) {
                    Chip chip = outerChipGroup.findViewById(id);
                    Cloth cloth = new Cloth("outer", chip.getText().toString());
                    clothList.add(cloth);
                }

                fashion.clothList = clothList;

                Chip chip = rateChipGroup.findViewById(rateId);
                String rateContent = chip.getText().toString();
                int intTemperature = Integer.parseInt(temperatureText.getText().toString().substring(0, temperature.length() - 2));
                if (rateContent.equals("적당함")) {
                    for (Cloth currentCloth: clothList) {
                        Statistics statistics = new Statistics();
                        statistics.temperature = intTemperature;
                        statistics.cloth = currentCloth;
                        statistics.wearCount = 1;
                        //wearCount 증가시키는 코드
                        firebaseDatabase.getReference("/statistics").child(currentCloth.clothName + ", " + intTemperature).push().setValue(statistics);
                    }
                }

                firebaseDatabase.getReference("/fashion").child(baseDate).push().setValue(fashion);

                fragment = getFragmentManager().findFragmentById(R.id.mainLayout);
                dismiss();
            }
        });

        return view;
    }

    public void setTemperatureText() {
        temperatureText.setText(temperature + "도");
    }
}