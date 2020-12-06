package com.example.rommies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAccount extends AppCompatActivity {

    DatabaseReference Dref;
    FirebaseAuth mAuth;
    TextView myaccount;
    TextView message;
    ArrayList<String> names;
    int index;
    String Myname="";
    StringBuilder stringBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        stringBuilder=new StringBuilder();
        myaccount=(TextView) findViewById(R.id.textViewAccount);
        message=(TextView)findViewById(R.id.textviewmsg);
        names=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();
        Dref= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        Dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Myname=snapshot.child("name").getValue().toString();

                SetUpAccount(Myname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void SetUpAccount(String curent_name)
    {
        if(getIntent().hasExtra("position_rommie")&&getIntent().hasExtra("name_roomie"))
        {
            index=getIntent().getIntExtra("position_rommie",-1);
            names=getIntent().getStringArrayListExtra("name_roomie");
        }

        if(index!=-1&&!names.isEmpty())
        {

            if(curent_name.equals(names.get(index)))
            {
                myaccount.setText("My Account");
                for(int i=0; i<names.size(); i++)
                {
                    if(i!=index)
                        stringBuilder.append("You owe to " + names.get(i) + " : 0 $" +"\n");
                }

                for(int i=0; i<names.size(); i++)
                {
                    if(i!=index)
                        stringBuilder.append(names.get(i) + " owe to Me : 0 $" +"\n");
                }
            }
            if(!curent_name.equals(names.get(index)))
            {
                myaccount.setText(names.get(index) + " Account");
                for(int i=0; i<names.size(); i++)
                {
                    if(i!=index && names.get(i).equals(curent_name))
                    {
                        stringBuilder.append("You owe to " + names.get(index) + ": 0 $" +"\n");
                    }

                }

                for(int i=0; i<names.size(); i++)
                {
                    if(i!=index && names.get(i).equals(curent_name))
                    {

                        stringBuilder.append(names.get(index) +" owe to Me : 0 $" +"\n");
                    }

                }

                for(int i=0; i<names.size(); i++)
                {
                    if(i!=index && !names.get(i).equals(curent_name))
                        stringBuilder.append(names.get(i) +" owe to " + names.get(index) + ": 0 $" +"\n");
                }

                for(int i=0; i<names.size(); i++)
                {
                    if(i!=index && !names.get(i).equals(curent_name))
                        stringBuilder.append(names.get(index) +" owe to " + names.get(i) + ": 0 $" +"\n");
                }
            }



            message.setText(stringBuilder.toString());

        }
    }
}