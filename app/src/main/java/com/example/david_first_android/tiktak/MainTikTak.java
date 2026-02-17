package com.example.david_first_android.tiktak;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.david_first_android.R;

public class MainTikTak extends AppCompatActivity {

     Tile tile1_1, tile1_2, tile1_3;
     Tile tile2_1, tile2_2, tile2_3;
     Tile tile3_1, tile3_2, tile3_3;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tik_tak);

        ImageView IM1 = findViewById(R.id.tile1_1);
        IM1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // type = 0, res = 0
        tile1_1 = new Tile(IM1);
        tile1_2 = new Tile(findViewById(R.id.tile1_2));
        tile1_3 = new Tile(findViewById(R.id.tile1_3));

        tile2_1 = new Tile(findViewById(R.id.tile2_1));
        tile2_2 = new Tile(findViewById(R.id.tile2_2));
        tile2_3 = new Tile(findViewById(R.id.tile2_3));

        tile3_1 = new Tile(findViewById(R.id.tile3_1));
        tile3_2 = new Tile(findViewById(R.id.tile3_2));
        tile3_3 = new Tile(findViewById(R.id.tile3_3));









    }
}