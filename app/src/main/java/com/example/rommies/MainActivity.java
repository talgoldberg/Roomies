package com.example.rommies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void register(View v)
    {
        startActivity(new Intent(this, Register.class));
    }
    public void login(View v)
    {
        startActivity(new Intent(this, Login.class));
    }
}