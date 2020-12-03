package com.example.rommies;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class Register extends AppCompatActivity
{

    private Button regbtn;
    private FirebaseAuth fAuth;
    private EditText etEmail, etName, etPass;
    private String Uid = "";
    private ProgressBar pb;
    private User u;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regbtn = (Button)findViewById(R.id.register_button);
        etName = (EditText)findViewById(R.id.Name);
        etEmail = (EditText)findViewById(R.id.Email);
        etPass = (EditText)findViewById(R.id.Password);
        pb = (ProgressBar)findViewById(R.id.PrgrsBar);
        fAuth = FirebaseAuth.getInstance();

        regbtn.setOnClickListener(v ->
        {
            u = new User(etEmail.getText().toString(), etName.getText().toString());
            pb.setVisibility(View.VISIBLE);
            String password = etPass.getText().toString();
            System.out.println("------------------ password "+password);
                    fAuth.createUserWithEmailAndPassword(u.getEmail(), password).addOnCompleteListener(task -> {
                if (!task.isSuccessful())
                {
                    Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                Uid = task.getResult().getUser().getUid();
                u.setUid(Uid);
                dbRef.child("Users").child(Uid).setValue(u);
                pb.setVisibility(View.GONE);
                Toast.makeText(Register.this, "user created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, afterRegister.class);
                intent.putExtra("Uid",Uid);

                startActivity(intent);
                finish();
            });
        });
    }
}