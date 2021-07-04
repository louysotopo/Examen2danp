package com.example.sensores;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sensores.controller.SelectActivity;

public class MainActivity extends AppCompatActivity {
    private Button inicio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inite();
    }

    private void inite() {
        inicio =findViewById(R.id.button);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartRegisterActivity();
            }
        });
    }

    public  void StartRegisterActivity(){
        Intent intent = new Intent(this, SelectActivity.class);
        startActivity(intent);
    }
}