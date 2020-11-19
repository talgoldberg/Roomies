package com.example.rommies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    Button login;
    FirebaseDatabase db;
    FirebaseAuth fAuth;
    EditText etEmail, etPass;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button)findViewById(R.id.login);
        etEmail = (EditText)findViewById(R.id.email);
        etPass = (EditText)findViewById(R.id.password);
        fAuth = FirebaseAuth.getInstance();
        login.setOnClickListener((v) ->
        {

            String email = etEmail.getText().toString();
            String password = etPass.getText().toString();
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task)->
            {
                if(!task.isSuccessful())
                {
                    Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(Login.this, "login succeed", Toast.LENGTH_SHORT).show();


            });

        });
    }
}