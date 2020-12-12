package com.example.rommies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.fonts.SystemFonts;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAccount extends AppCompatActivity {

    DatabaseReference Dref, mRaf;
    FirebaseAuth mAuth;
    TextView myaccount;
    TextView message;
    ArrayList<String> names;
    int index;
    String Myname = "";
    StringBuilder stringBuilder;
    private String key_ap;
    private Map<String, String> users = new HashMap<>();
    private HashMap<String, HashMap<String, Double>> Balance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        stringBuilder = new StringBuilder();
        myaccount = (TextView) findViewById(R.id.textViewAccount);
        message = (TextView) findViewById(R.id.textviewmsg);
        names = new ArrayList<>();
        Balance = new HashMap<>();
        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//        String userID = user.getUid();

        Dref = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        key_ap = getIntent().getExtras().getString("com.app.java.acc.key");
        users = (Map<String, String>) getIntent().getExtras().get("com.app.java.acc.users");
        users.put(mAuth.getUid(), "12");
        // System.out.print("maxim123  : " + userID + "   :  "+key_ap  );

        mRaf = FirebaseDatabase.getInstance().getReference("/Apartments/" + key_ap).child("Balance");
        mRaf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (Map.Entry<String, String> user1 : users.entrySet()) {
                    String uid1 = user1.getKey();
                    HashMap<String, Double> temp = new HashMap<>();
                    for (Map.Entry<String, String> user2 : users.entrySet()) {
                        String uid2 = user2.getKey();
                        if (!uid1.equals(uid2)) {
                            temp.put(uid2, snapshot.child(uid1).child(uid2).getValue(Double.class));
                        }
                    }
                   Balance.put(uid1, temp);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Myname = snapshot.child("name").getValue().toString();

                SetUpAccount(Myname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void SetUpAccount(String curent_name) {
        if (getIntent().hasExtra("position_rommie") && getIntent().hasExtra("name_roomie")) {
            index = getIntent().getIntExtra("position_rommie", -1);
            names = getIntent().getStringArrayListExtra("name_roomie");
        }

        if (index != -1 && !names.isEmpty()) {

            if (curent_name.equals(names.get(index))) {
                myaccount.setText("My Account");
               // for (Map.Entry<String, HashMap<String, Double>> user1 : Balance.entrySet()) {
                    //String ds=user1.getKey();
                    for (Map.Entry<String, Double> us : Balance.get(mAuth.getUid()).entrySet()) {
                        for(Map.Entry<String,String> name: users.entrySet() )
                        {
                            if(us.getKey().equals(name.getKey())) {

                                DecimalFormat df = new DecimalFormat("#.##");
                                final Double Money=us.getValue();
                                stringBuilder.append("Your Balance with " + name.getValue() + " is : " + df.format(Money) + "\n");
                            }
                        }
                    }


           }
                if (!curent_name.equals(names.get(index))) {
                    for(Map.Entry<String,String> name: users.entrySet() ){
                        if(name.getValue().equals(names.get(index))){
                            stringBuilder.append(names.get(index)+ " Own you  "+ Balance.get(name.getKey()).get(mAuth.getUid()).doubleValue()  +"\n");
                        }
                    }

                    myaccount.setText(names.get(index) + " Account");

                }
                message.setText(stringBuilder.toString());

            }
        }}

