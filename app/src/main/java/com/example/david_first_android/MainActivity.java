package com.example.david_first_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.Queue;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button Btn_LH;
    private Button Btn_To20;
    private Button Btn_Etgar;
    private TextView TV1;
    private TextView TV_X;
    private TextView TV2;
    private TextView ET_Answer;
    private Button Btn_Check;
    private Button Btn_Save;
    private Button Btn_ShowAllUsers;
    private Button Btn_Rate;
    private int result;
    private Interface inface;
    Exercise ex;
    private boolean LH;
    private boolean Etgar;
    private boolean To20;
    private User myUser;
    private String username;


    public void resetBooleans(){
        LH = false;
        To20 = false;
        Etgar = false;
    }

    public void check(){
        if (ex.isCorrect(ET_Answer.getText()+ "")){
            Toast.makeText(this,"success",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
        }
    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                //האזנה לסגירה של אקטיביטי
                @Override

                public void onActivityResult(ActivityResult result) {
                    int myRate = result.getData().getIntExtra("RateKey", -1);
                    Toast.makeText(MainActivity.this, "your Rate: " + myRate, Toast.LENGTH_SHORT).show();
                    //אני שם את הרייט שקיבלתי בתוך המשתנה MYUSER
                    myUser.setRate(myRate);

                }
            }
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Btn_Etgar = findViewById(R.id.Btn_Etgar);
        Btn_To20 = findViewById(R.id.Btn_To20);
        Btn_LH = findViewById(R.id.Btn_LH);
        TV1 = findViewById(R.id.TV_1);
        TV_X = findViewById(R.id.TV_x);
        TV2 = findViewById(R.id.TV_2);
        ET_Answer = findViewById(R.id.ET_Answer);
        Btn_Check = findViewById(R.id.Btn_Check);
        Btn_Save = findViewById(R.id.Btn_Save);
        Btn_ShowAllUsers = findViewById(R.id.Btn_ShowAllUsers);
        Btn_Rate = findViewById(R.id.Btn_Rate);




        Intent intent = getIntent();
        String UserName=intent.getStringExtra("UserName");

        Toast.makeText(this,"Welcome" + " " + UserName,Toast.LENGTH_SHORT).show();

        myUser = new User(UserName);





        inface = new Interface() {


            @Override
            public void showNumbers(int num1, int num2) {

                TV1.setText(num1+"");
                TV2.setText(num2+"");

            }
        };

        ex = new Exercise(inface);



        Btn_LH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBooleans();
                LH = true;
                ex.generateLH();
            }
        });

        Btn_To20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBooleans();
                To20 = true;
                ex.generateTo20();
            }
        });

        Btn_Etgar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBooleans();
                Etgar = true;
                ex.generateEtgar();

            }
        }
        );



        Btn_Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ex.isCorrect(ET_Answer.getText()+ "")){
                    myUser.setScore(LH,To20,Etgar);
                }
                resetBooleans();

                check();
                ET_Answer.setText("");
            }
        });

        Btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Btn_ShowAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //הופך לי את myuser לסטרינג באמצעות ג'ייסון
                Gson gson = new Gson();
                String json = gson.toJson(myUser);

                //מכניס את הג'ייסון לתוך הבנדל בתור סטריגנ כי ג'ייסון הופך אובייקט לסטרינג
                Bundle bundle = new Bundle();
                bundle.putString("myUserJson", json);



                showusers fragment = new showusers();
                //מקשר את הבנדל לפרגמנט
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment, "showusers").commit();

            }
        });

        Btn_Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RateActivity.class);
                activityResultLauncher.launch(intent);

            }
        });




    }






}