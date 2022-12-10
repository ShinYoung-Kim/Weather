package com.ksy.mpl;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPhotoFragment extends BottomSheetDialogFragment {

    Button saveBtn;
    TextView dateText;
    TextView temperatureText;
    ImageView addPhoto;

    public String temperature;
    public String state;

    Fragment fragment;
    Context context;

    ChipGroup upChipGroup, downChipGroup, outerChipGroup, accChipGroup, rateChipGroup;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference userDatabase;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;

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
        accChipGroup = (ChipGroup) view.findViewById(R.id.accChipGroup);
        rateChipGroup = (ChipGroup) view.findViewById(R.id.rateChipGroup);

        addPhoto = (ImageView) view.findViewById(R.id.photo);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String baseDate = dateFormat.format(date);

        dateText.setText(baseDate);
        setTemperatureText();
        //setStateBackGround(state);

        ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if( result.getResultCode() == RESULT_OK && result.getData() != null){
                            imageUri = result.getData().getData();
                            addPhoto.setImageURI(imageUri);
                        }
                    }
                });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        String uid = null;
        User userInstance = User.getInstance(uid);
        uid = userInstance.getUid();

        userDatabase = firebaseDatabase.getReference("Users").child(uid);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(getActivity(), "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }

                Data data = new Data();
                data.date = baseDate;
                data.photoURL = imageUri.toString();
                //data.rate = Integer.parseInt(rate.getText().toString());
                data.weather = temperatureText.getText().toString();
                userDatabase.child("data").child(baseDate).push().setValue(data);

                Fashion fashion = new Fashion();
                fashion.date = baseDate;
                fashion.photoURL = imageUri.toString();
                fashion.weather = temperatureText.getText().toString();

                List<Integer> upIds = upChipGroup.getCheckedChipIds();
                List<Integer> downIds = downChipGroup.getCheckedChipIds();
                List<Integer> outerIds = outerChipGroup.getCheckedChipIds();
                List<Integer> accIds = accChipGroup.getCheckedChipIds();
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

                for (Integer id : accIds) {
                    Chip chip = accChipGroup.findViewById(id);
                    Cloth cloth = new Cloth("acc", chip.getText().toString());
                    clothList.add(cloth);
                }

                fashion.clothList = clothList;

                Chip chip = rateChipGroup.findViewById(rateId);
                String rateContent = chip.getText().toString();
                fashion.rate = rateContent;

                int intTemperature = Integer.parseInt(temperatureText.getText().toString().substring(0, temperature.length() - 2));
                if (rateContent.equals("적당함")) {
                    //HashMap<String, Object> statisticHashMap = new HashMap();
                    for (Cloth currentCloth: clothList) {
                        //Statistics statistics = new Statistics((intTemperature / 5) * 5, currentCloth, 1);
                        Map<String, Object> updates = new HashMap<>();
                        //updates.put("Statistics/" + currentCloth.clothName +"(" + currentCloth.category + ")" + ", " + (intTemperature / 5) * 5 + "/wearCount", ServerValue.increment(1));
                        updates.put("Statistics/" + (intTemperature / 5) * 5 + "/" + currentCloth.category + "/" + currentCloth.clothName + "/wearCount", ServerValue.increment(1));
                        userDatabase.updateChildren(updates);
                        //wearCount 증가시키는 코드
                        //int countType = userDatabase.child("Statistics").child(currentCloth + ", " + (intTemperature / 5) * 5 + "/wearCount").getValue();
                        //statisticHashMap.put(currentCloth + ", " + (intTemperature / 5) * 5 + "/wearCount", 1);
                    }
                    //userDatabase.child("Statistics").updateChildren(statisticHashMap);
                }

                userDatabase.child("fashion").child("date").child(baseDate).push().setValue(fashion);
                userDatabase.child("fashion").child("temperature").child(String.valueOf((intTemperature / 5) * 5)).push().setValue(fashion);

                fragment = getFragmentManager().findFragmentById(R.id.mainLayout);
                dismiss();
            }
        });

        return view;
    }

    public void setTemperatureText() {
        temperatureText.setText(temperature + "도");
    }

    private void uploadToFirebase(Uri uri) {
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //이미지 모델에 담기
                        UploadModel model = new UploadModel(uri.toString());

                        //키로 아이디 생성
                        //String modelId = root.push().getKey();

                        //데이터 넣기
                        //root.child(modelId).setValue(model);

                        //addPhoto.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //파일타입 가져오기
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}