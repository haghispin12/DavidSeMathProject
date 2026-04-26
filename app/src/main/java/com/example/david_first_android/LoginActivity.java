package com.example.david_first_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.david_first_android.tiktak.MainTikTak;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sp;
    EditText ET_username;
    EditText ET_password;
    Button btnLogin;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login2);

        ET_username = findViewById(R.id.username);
        ET_password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);

        sp = getSharedPreferences("user", MODE_PRIVATE);


        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            startGame();
        }



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sginIn(view);
            }
        });







    }

    private void startGame(){
        Intent intent = new Intent(LoginActivity.this, MainTikTak.class);
        startActivity(intent);

    }

    public void sginIn(View view){
        sendAuth(ET_username.getText().toString(), ET_password.getText().toString());

    }

    private void sendAuth(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = auth.getCurrentUser();
                        Log.d("TAG", "");

                        if (user != null) {
                            SharedPreferences.Editor editShare = sp.edit();
                            editShare.putString("email", user.getEmail());
                            editShare.apply();
                        }
                        Toast.makeText(LoginActivity.this, "Authentication success.",
                                Toast.LENGTH_SHORT).show();
                        startGame();

                    }

                    else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });


        


    }


}