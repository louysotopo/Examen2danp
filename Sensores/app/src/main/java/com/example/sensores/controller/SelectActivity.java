package com.example.sensores.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sensores.R;

public class SelectActivity extends AppCompatActivity {
    private CardView cardViewPodometro;
    private CardView cardViewPlanchas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        initComponents();
        initButtons();
    }
    private void initComponents() {
        cardViewPodometro = findViewById(R.id.cardView_podometro);
        cardViewPlanchas = findViewById(R.id.cardView_panchas);
    }
    private void initButtons() {
        cardViewPodometro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { StartPodometerActivity(); }
        });
        cardViewPlanchas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { StartPlanchasActivity(); }
        });

    }
    public  void StartPodometerActivity(){
        Intent intent = new Intent(this, DistanceTraveledActivity.class);
        startActivity(intent);
    }
    public  void StartPlanchasActivity(){
        Intent intent = new Intent(this, ABSActivity.class);
        startActivity(intent);
    }
}