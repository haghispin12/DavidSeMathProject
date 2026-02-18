package com.example.david_first_android.tiktak;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.david_first_android.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainTikTak extends AppCompatActivity {

     Tile tile1_1, tile1_2, tile1_3;
     Tile tile2_1, tile2_2, tile2_3;
     Tile tile3_1, tile3_2, tile3_3;
     TextView TV_winner;

    private boolean isWinner(int playerType) {
        // שורות
        if (tile1_1.getType() == playerType && tile1_2.getType() == playerType && tile1_3.getType() == playerType)
            return true;
        if (tile2_1.getType() == playerType && tile2_2.getType() == playerType && tile2_3.getType() == playerType)
            return true;
        if (tile3_1.getType() == playerType && tile3_2.getType() == playerType && tile3_3.getType() == playerType)
            return true;

        // עמודות
        if (tile1_1.getType() == playerType && tile2_1.getType() == playerType && tile3_1.getType() == playerType)
            return true;
        if (tile1_2.getType() == playerType && tile2_2.getType() == playerType && tile3_2.getType() == playerType)
            return true;
        if (tile1_3.getType() == playerType && tile2_3.getType() == playerType && tile3_3.getType() == playerType)
            return true;

        // אלכסונים
        if (tile1_1.getType() == playerType && tile2_2.getType() == playerType && tile3_3.getType() == playerType)
            return true;
        if (tile1_3.getType() == playerType && tile2_2.getType() == playerType && tile3_1.getType() == playerType)
            return true;

        return false; // אין מנצח
    }






    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tik_tak);


        TV_winner = findViewById(R.id.TV_winner);
        tile1_1 = new Tile(findViewById(R.id.tile1_1));
        tile1_2 = new Tile(findViewById(R.id.tile1_2));
        tile1_3 = new Tile(findViewById(R.id.tile1_3));

        tile2_1 = new Tile(findViewById(R.id.tile2_1));
        tile2_2 = new Tile(findViewById(R.id.tile2_2));
        tile2_3 = new Tile(findViewById(R.id.tile2_3));

        tile3_1 = new Tile(findViewById(R.id.tile3_1));
        tile3_2 = new Tile(findViewById(R.id.tile3_2));
        tile3_3 = new Tile(findViewById(R.id.tile3_3));


// --- ONCLICK ---

        tile1_1.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tile1_1.getType() == 0) {
                    tile1_1.getIv().setImageResource(R.drawable.goodx);
                    tile1_1.setType(1);
                }
                if(isWinner(tile1_1.getType())){
                    TV_winner.setText("YOU WIN!!!");
                }


            }
        });

        tile1_2.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tile1_2.getType() == 0) {
                    tile1_2.getIv().setImageResource(R.drawable.goodx);
                    tile1_2.setType(1);
                }
                if(isWinner(tile1_2.getType())){
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile1_3.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tile1_3.getType() == 0) {
                    tile1_3.getIv().setImageResource(R.drawable.goodx);
                    tile1_3.setType(1);
                }
                if(isWinner(tile1_3.getType())){
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile2_1.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tile2_1.getType() == 0) {
                    tile2_1.getIv().setImageResource(R.drawable.goodx);
                    tile2_1.setType(1);
                }
                if(isWinner(tile2_1.getType())){
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile2_2.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tile2_2.getType() == 0) {
                    tile2_2.getIv().setImageResource(R.drawable.goodx);
                    tile2_2.setType(1);
                }
                if(isWinner(tile2_2.getType())){
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile2_3.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tile2_3.getType() == 0) {
                    tile2_3.getIv().setImageResource(R.drawable.goodx);
                    tile2_3.setType(1);
                }
                if(isWinner(tile2_3.getType())){
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile3_1.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tile3_1.getType() == 0) {
                    tile3_1.getIv().setImageResource(R.drawable.goodx);
                    tile3_1.setType(1);
                }
                if(isWinner(tile3_1.getType())){
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile3_2.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tile3_2.getType() == 0) {
                    tile3_2.getIv().setImageResource(R.drawable.goodx);
                    tile3_2.setType(1);
                }
                if(isWinner(tile3_2.getType())){
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        tile3_3.getIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tile3_3.getType() == 0) {
                    tile3_3.getIv().setImageResource(R.drawable.goodx);
                    tile3_3.setType(1);
                }
                if(isWinner(tile3_3.getType())){
                    TV_winner.setText("YOU WIN!!!");
                }
            }
        });

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        int n=0;

    }
}