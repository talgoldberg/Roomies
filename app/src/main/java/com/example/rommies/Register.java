package com.example.rommies;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcelable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class Register extends AppCompatActivity
{

    Button send;
    FirebaseAuth fAuth;
    EditText etEmail, etName, etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        send = (Button)findViewById(R.id.send_button);
        etName = (EditText)findViewById(R.id.Name);
        etEmail = (EditText)findViewById(R.id.Email);
        etPass = (EditText)findViewById(R.id.Password);
        fAuth = FirebaseAuth.getInstance();
        AtomicReference<String> Uid = new AtomicReference<>("");
        send.setOnClickListener((v)->
        {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPass.getText().toString();
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((task) ->
                {
                    if(!task.isSuccessful())
                    {
                        Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                    Uid.set(task.getResult().getUser().getUid());
                    Map<String, String> map = new HashMap<String, String>() {};
                    map.put("Email", email);
                    map.put("Name", name);
                    dbRef.child("Users").child(Uid.get()).setValue(map);
                    Toast.makeText(Register.this, "user created successfully", Toast.LENGTH_SHORT).show();
                });
                Intent intent = new Intent(this, afterRegister.class);
                intent.putExtra("Uid",Uid);
                startActivity(intent);
//                new DatabaseReference.CompletionListener()
//                {
//                    @Override
//                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref)
//                    {
//                        if(error != null) {
//                            Toast.makeText(Register.this, "error! " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        else
//                            Toast.makeText(Register.this, "user saved successfully", Toast.LENGTH_SHORT).show();
//                    }
//                });

        });
    }
    public static boolean isValidEmail(CharSequence target)
    {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}