package com.example.david_first_android.mathproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.david_first_android.R;

public class RateActivity extends AppCompatActivity {

    private Button Btn_Save;
    private SeekBar SB_seekbar;
    private TextView TV_seekbarValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate);

        Btn_Save = findViewById(R.id.Btn_SaveRate);
        SB_seekbar = findViewById(R.id.SB_seekbar);
        TV_seekbarValue = findViewById(R.id.TV_seekbarValue);

        Btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("RateKey", SB_seekbar.getProgress());
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        SB_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TV_seekbarValue.setText(String.valueOf(progress));

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });







    }
}