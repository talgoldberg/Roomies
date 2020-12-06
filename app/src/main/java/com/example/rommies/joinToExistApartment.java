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

    Button join;
    EditText aprtCode;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_to_exist_apartment);
        join = (Button) findViewById(R.id.joinButton);
        aprtCode = (EditText) findViewById(R.id.CODE);



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




        });






    }
}