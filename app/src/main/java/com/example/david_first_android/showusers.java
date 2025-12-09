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


public class showusers extends Fragment {


    EditText ET_user;
    TextView TV_score;
    TextView TV_rating;
    Button Btn_addPicture;
    ImageView ImgV_imageView;
    Button Btn_addUser;


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


        return v;
    }
}