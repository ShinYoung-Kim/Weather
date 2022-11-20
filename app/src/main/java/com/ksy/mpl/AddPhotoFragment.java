package com.ksy.mpl;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPhotoFragment extends BottomSheetDialogFragment {

    Button saveBtn;
    TextView dateText;
    TextView temperatureText;

    public String temperature;
    public String state;

    Fragment fragment;
    Context context;

    private static final int PICK_IMAGE_REQUEST = 9544;
    ImageView image;
    Uri selectedImage;
    String part_image;

    Chip upNone, upSleeveless, upShort, upOnePiece, upShirt,
            upLong, upCardigan, upHoodie, upManToMan, upExercise,
            upUniform, upFormal, upTagAdd;

    Chip outerNone, outerJacket, outerJumper, outerCoat, outerLeather,
            outerPadding, outerFleece, outerHoodie, outerRainCoat,
            outerMustang, outerVest, outerAdd;

    Chip rateCold, rateGood, rateHot;

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
        image = view.findViewById(R.id.photo);

        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        dateText = (TextView) view.findViewById(R.id.dateText);
        temperatureText = (TextView) view.findViewById(R.id.temperatureText);

        upNone = (Chip) view.findViewById(R.id.upNone);
        upSleeveless = (Chip) view.findViewById(R.id.upSleeveless);
        upShort = (Chip) view.findViewById(R.id.upShort);
        upOnePiece = (Chip) view.findViewById(R.id.upOnePiece);
        upShirt = (Chip) view.findViewById(R.id.upShirt);
        upLong = (Chip) view.findViewById(R.id.upLong);
        upCardigan = (Chip) view.findViewById(R.id.upCardigan);
        upHoodie = (Chip) view.findViewById(R.id.upHoodie);
        upManToMan = (Chip) view.findViewById(R.id.upManToMan);
        upExercise = (Chip) view.findViewById(R.id.upExercise);
        upUniform = (Chip) view.findViewById(R.id.upUniform);
        upFormal = (Chip) view.findViewById(R.id.upFormal);
        upTagAdd = (Chip) view.findViewById(R.id.upTagAdd);

        outerNone = (Chip) view.findViewById(R.id.outerNone);
        outerJacket = (Chip) view.findViewById(R.id.outerJacket);
        outerJumper = (Chip) view.findViewById(R.id.outerJumper);
        outerCoat = (Chip) view.findViewById(R.id.outerCoat);
        outerLeather = (Chip) view.findViewById(R.id.outerLeather);
        outerPadding = (Chip) view.findViewById(R.id.outerPadding);
        outerFleece = (Chip) view.findViewById(R.id.outerFleece);
        outerHoodie = (Chip) view.findViewById(R.id.outerHoodie);
        outerRainCoat = (Chip) view.findViewById(R.id.outerRainCoat);
        outerMustang = (Chip) view.findViewById(R.id.outerMustang);
        outerVest = (Chip) view.findViewById(R.id.outerVest);
        outerAdd = (Chip) view.findViewById(R.id.outerAdd);

        rateCold = (Chip) view.findViewById(R.id.rateCold);
        rateGood = (Chip) view.findViewById(R.id.rateGood);
        rateHot = (Chip) view.findViewById(R.id.rateHot);

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

        upTagAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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

        pick(view);

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

    public void pick(View view) {
        verifyStoragePermissions(getActivity());
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Open Gallery"), PICK_IMAGE_REQUEST);
    }

    // Method to get the absolute path of the selected image from its URI
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                selectedImage = data.getData();                                                         // Get the image file URI
                String[] imageProjection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, imageProjection, null, null, null);
                if(cursor != null) {
                    cursor.moveToFirst();
                    int indexImage = cursor.getColumnIndex(imageProjection[0]);
                    part_image = cursor.getString(indexImage);
                    // Get the image file absolute path
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image.setImageBitmap(bitmap);                                                       // Set the ImageView with the bitmap of the image
                }
            }
        }
    }

    // Upload the image to the remote database
    public void uploadImage(View view) {
        File imageFile = new File(part_image);                                                          // Create a file using the absolute path of the image
        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imageFile);
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", imageFile.getName(), reqBody);
        API api = RetrofitClient.getInstance().getAPI();
        Call<ResponseBody> upload = api.uploadImage(partImage);
        upload.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                    Intent main = new Intent(getActivity(), MainActivity.class);
                    main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(main);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}