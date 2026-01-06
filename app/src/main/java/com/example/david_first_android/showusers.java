package com.example.david_first_android;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;


public class showusers extends Fragment {


    EditText ET_user;
    TextView TV_score;
    TextView TV_rating;
    Button Btn_addPicture;
    ImageView ImgV_imageView;
    Button Btn_addUser;
    User myUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_showusers,container,false);

        ET_user = v.findViewById(R.id.ET_user);
        TV_score = v.findViewById(R.id.TV_score);
        TV_rating = v.findViewById(R.id.TV_rating);
        Btn_addPicture = v.findViewById(R.id.Btn_addPicture);
        ImgV_imageView = v.findViewById(R.id.ImgV_imageView);
        Btn_addUser = v.findViewById(R.id.Btn_addUser);

        //משתנה בנדל שאוחז את הערכים שהתקבלו מהמיין אקטיביטי
        String userStr = getArguments().getString("myUserJson");

        Gson gson = new Gson();
        myUser = gson.fromJson(userStr, User.class);

        ET_user.setText(myUser.getUserName());
        TV_score.setText("SCORE: " + String.valueOf(myUser.getScore()));
        TV_rating.setText("RATING: " + String.valueOf(myUser.getRate()));

        return v;
    }
}