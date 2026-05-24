package com.example.david_first_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david_first_android.tiktak.MainTikTak;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sp;
    EditText ET_username;
    EditText ET_password;
    Button btnLogin;
    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        ET_username = findViewById(R.id.username);
        ET_password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);

        sp = getSharedPreferences("user", MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() != null) {
            startGame();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sginIn(view);
            }
        });
    }

    private void startGame() {
        String uid = auth.getCurrentUser().getUid();

        db.child("users").child(uid).child("online")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Boolean isOnline = snapshot.getValue(Boolean.class);

                        if (isOnline != null && isOnline) {
                            // אם הוא כבר מסומן כאונליין, כנראה מדובר בבאג של משחק קודם שלא נסגר טוב.
                            // אנחנו דורסים את זה, מסמנים אותו כאונליין ומכניסים אותו למשחק
                            db.child("users").child(uid).child("online").setValue(true);
                            db.child("users").child(uid).child("email").setValue(auth.getCurrentUser().getEmail());
                            askForNickname();
                        } else {
                            db.child("users").child(uid).child("online").setValue(true);
                            db.child("users").child(uid).child("email").setValue(auth.getCurrentUser().getEmail());
                            askForNickname();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void askForNickname() {
        String uid = auth.getCurrentUser().getUid();

        // בודקים אם יש כבר כינוי שמור
        db.child("users").child(uid).child("nickname")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String existingNickname = snapshot.getValue(String.class);

                        if (existingNickname != null && !existingNickname.isEmpty()) {
                            // יש כבר כינוי — עובר ישר למשחק
                            Intent intent = new Intent(LoginActivity.this, MainTikTak.class);
                            startActivity(intent);
                        } else {
                            // אין כינוי — מבקש מהמשתמש
                            EditText ET_nickname = new EditText(LoginActivity.this);
                            ET_nickname.setHint("בחר כינוי");

                            new android.app.AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("איך לקרוא לך?")
                                    .setView(ET_nickname)
                                    .setPositiveButton("אישור", (dialog, which) -> {
                                        String nickname = ET_nickname.getText().toString().trim();
                                        if (!nickname.isEmpty()) {
                                            db.child("users").child(uid).child("nickname").setValue(nickname);
                                        }
                                        Intent intent = new Intent(LoginActivity.this, MainTikTak.class);
                                        startActivity(intent);
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    public void sginIn(View view) {
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

                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}