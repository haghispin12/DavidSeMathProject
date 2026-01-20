package com.example.david_first_android;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import javax.xml.transform.Result;


public class showusers extends Fragment {


    EditText ET_user;
    TextView TV_score;
    TextView TV_rating;
    Button Btn_addPicture;
    ImageView IV_imageView;
    Button Btn_addUser;
    User myUser;
    Uri uri;
    ArrayList<User> arrayList;


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK) {

                        IV_imageView.setImageURI(uri);
                        myUser.setUri(uri);
                    }
                }
            });


    private void openCamera(){
        //אובייקט שאוחז פרטים של תמונה
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        //הכנסתי את הערכים של מקודם על URI
        uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //האובייקט שמאזין הוא זה שפותח את המצלמה כדי שהוא יאזין אליו
        activityResultLauncher.launch(cameraIntent);

    }

    //פעולה שמוסיפה יוזר לטבלה של הDBHELPER ששומרת את כל השמתמשים
    private void dbAddUser(){
        DBHelper dbHelper = new DBHelper(getActivity());
        long id = dbHelper.insert(myUser , getActivity());
        if(id>0){
            myUser.setId(id);
        }
    }

    private void dbSelectUsers(){
        DBHelper dbHelper = new DBHelper(getActivity());
        arrayList = dbHelper.selectAll();
    }








    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //שמה במערך שלי את המשתמש החדש שהוכנס ביחד עם פתיחת הפרגמנט
        dbSelectUsers();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_showusers,container,false);

        ET_user = v.findViewById(R.id.ET_user);
        TV_score = v.findViewById(R.id.TV_score);
        TV_rating = v.findViewById(R.id.TV_rating);
        Btn_addPicture = v.findViewById(R.id.Btn_addPicture);
        IV_imageView = v.findViewById(R.id.ImgV_imageView);
        Btn_addUser = v.findViewById(R.id.Btn_addUser);

        //משתנה בנדל שאוחז את הערכים שהתקבלו מהמיין אקטיביטי
        String userStr = getArguments().getString("myUserJson");

        Gson gson = new Gson();
        myUser = gson.fromJson(userStr, User.class);

        ET_user.setText(myUser.getUserName());
        TV_score.setText("SCORE: " + String.valueOf(myUser.getScore()));
        TV_rating.setText("RATING: " + String.valueOf(myUser.getRate()));



        Btn_addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        Btn_addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbAddUser();
                dbSelectUsers();
            }
        });


        return v;
    }
}