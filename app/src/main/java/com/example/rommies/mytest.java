package com.example.rommies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class mytest extends AppCompatActivity {

    TextView code1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytest);
        code1=(TextView)findViewById(R.id.ccc);

    //    Intent intent1=getIntent();
//        String forcode = intent1.getExtras().getString("code");
    //    code1.setText("  this will be the main screen -- " +
  //              "your code is " + forcode);
    }





}