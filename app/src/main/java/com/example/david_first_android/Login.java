package com.example.david_first_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {


    private EditText ET_Enter_username;
    private Button Btn_Submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        Btn_Submit = findViewById(R.id.Btn_Submit);
        ET_Enter_username = findViewById(R.id.ET_Enter_username);


        SharedPreferences sh = getSharedPreferences("SharedPref",MODE_PRIVATE);

        String sh1 = sh.getString("username", "");
        ET_Enter_username.setText(sh1);


        Btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sh = getSharedPreferences("SharedPref",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sh.edit();
                myEdit.putString("username", ET_Enter_username.getText().toString());
                myEdit.apply();



                Intent intent = new Intent(Login.this,MainActivity.class);
                intent.putExtra("UserName",ET_Enter_username.getText().toString());
                startActivity(intent);


            }
            });







    }


}