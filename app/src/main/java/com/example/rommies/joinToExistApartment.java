package com.example.rommies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class joinToExistApartment extends AppCompatActivity {
    //private FirebaseDatabase mDatabase;
    // DatabaseReference dbRootRef;

    Button join;
    EditText aprtCode;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_to_exist_apartment);
        join = (Button) findViewById(R.id.joinButton);
        aprtCode = (EditText) findViewById(R.id.CODE);
//        join.setOnClickListener((View.OnClickListener) this);


        Intent intentOld=getIntent();
        Intent intent=new Intent(this,mytest.class);
       String UserId=intentOld.getExtras().getString("Uid");

        join.setOnClickListener(v->
        {
            String TheCode=aprtCode.getText().toString();

            System.out.println("------------------aprt code is"+ TheCode +
                    " userid is "+UserId);
            dbRef.child("Apartments").child(TheCode).child("roommates").push().setValue(UserId);
            Toast.makeText(joinToExistApartment.this, "successfully", Toast.LENGTH_SHORT).show();
            startActivity(intent);
           // finish();



        });





/*
    @Override
    public void onClick(View v) {
        String codeForAprt = aprtCode.getText().toString();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/Apartments");
        String qq=dbRef.push().getKey();

        if (v == join) {
            Intent intent = new Intent(this, mytest.class);
            intent.putExtra("code",qq);



          *//*  if (codeForAprt == aprKey) {
                Toast.makeText(this, "successfully!", Toast.LENGTH_SHORT).show();

            }*//*
            startActivity(intent);


        }*/

    }
}