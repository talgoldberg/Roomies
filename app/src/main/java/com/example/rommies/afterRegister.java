package com.example.rommies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class afterRegister extends AppCompatActivity {

    Button create;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_register);
        create = (Button)findViewById(R.id.create);
        create.setOnClickListener((v)->
        {

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/Apartments");
            dbRef.child("/"+dbRef.push().getKey()+"/roommates").setValue("Uid", getIntent().getStringExtra("Uid"), new DatabaseReference.CompletionListener()
            {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {
                            if (databaseError != null) {
                                Toast.makeText(afterRegister.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(afterRegister.this, "Apartment Saved!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            });
    }
}