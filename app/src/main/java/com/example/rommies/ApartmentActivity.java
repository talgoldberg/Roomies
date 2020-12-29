package com.example.rommies;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ApartmentActivity extends AppCompatActivity {

    private ListView listViewRoomate;
    private ArrayAdapter<String> adapter;
    private TextView aprName;
    private TextView total_balance;
    private TextView total_price;
    private String aprKey;
    private static ArrayList<String> roommates;
    ArrayList<String> withoutmanager;
    private FirebaseAuth mAuth;
    String manager="";
    String uidManager="";
    double number=0;
    double totalBalance=0;
    private DatabaseReference get_roomies;
    private DatabaseReference get_room_name;
    private DatabaseReference balance;
    private boolean isManager = false;
    private MenuItem adminBtn;
    private HashMap<String, String> usersMap = new HashMap<>();
    BottomNavigationView btn_nagativ;
    ScrollView scroll;
    private Button btinf;
    MyListAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment);
        scroll=(ScrollView)findViewById(R.id.linearLayoutScroll);
        /*handle back button press */
        btn_nagativ=(BottomNavigationView)findViewById(R.id.bottomNavigationView);
        btn_nagativ.setOnNavigationItemSelectedListener(listener);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true )
        {
            @Override
            public void handleOnBackPressed()
            {
                new AlertDialog.Builder(ApartmentActivity.this).setTitle("Exit").
                        setMessage("Do you want to exit from Rommies?").
                        setCancelable(false).
                        setPositiveButton("Yes", (dialog, id)-> finish()).
                        setNegativeButton("No", (dialog, id)-> {dialog.cancel();return;}).
                        create().
                        show();
            }
        });


        if(getIntent().hasExtra("com.example.rommies.aprKey"))
        {
            aprKey = getIntent().getStringExtra("com.example.rommies.aprKey");
            get_room_name=FirebaseDatabase.getInstance().getReference().child("Apartments").child(aprKey);
            get_room_name.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println("******+++***** "+snapshot.child("Name").getValue().toString());
                    aprName.setText(snapshot.child("Name").getValue().toString());
                    if(mAuth.getUid().equals(snapshot.child("Manager").getValue())){
                        uidManager=snapshot.child("Manager").getValue().toString();
                        adminBtn.setVisible(true);

                    }
                    updateRoommates(aprKey);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        aprName = findViewById(R.id.ApartamenTextView);
        listViewRoomate = findViewById(R.id.listApart);
        roommates=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();


        listViewRoomate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent in=new Intent(getApplicationContext(),MyAccount.class);
                in.putExtra("position_rommie",position);
                in.putExtra("com.app.java.acc.key",aprKey);
                in.putStringArrayListExtra("name_roomie",roommates);
                in.putExtra("com.app.java.acc.users",usersMap);
                startActivity(in);
            }
        });
    }
    private  BottomNavigationView.OnNavigationItemSelectedListener listener=new BottomNavigationView.OnNavigationItemSelectedListener(){

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.friends_btn:
                {
                    scroll.setVisibility(View.GONE);
                    listViewRoomate.setVisibility(View.VISIBLE);
                    break;
                }
                
                case R.id.balance_btn:
                {
                    listViewRoomate.setVisibility(View.GONE);
                    scroll.setVisibility(View.VISIBLE);
                    break;
                }

            }

            return true;
        }
    };



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        adminBtn = menu.getItem(0);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.newPurchase:
            {
                Intent intent = new Intent(this, PurchaseActivity.class);
                intent.putExtra("com.app.java.Purchase.key",aprKey);
                intent.putExtra("com.app.java.Purchase.users",usersMap);
                startActivity(intent);
                break;
            }
            case R.id.adminBtn:
            {
                withoutmanager=new ArrayList<>();
                Intent in=new Intent(ApartmentActivity.this, ManagerActivity.class);
                in.putExtra("keyaprt",aprKey);
                System.out.println(manager+ "--------->>>>>>");
                for(int i=0; i<roommates.size(); i++)
                {

                    if(!manager.equals(roommates.get(i)))
                        withoutmanager.add(roommates.get(i));
                }
                in.putStringArrayListExtra("list",withoutmanager);

                in.putExtra("hash",usersMap);
                startActivityForResult(in,1001);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1001)
        {
            if(resultCode==RESULT_OK)
            {
                String n=data.getStringExtra("change_name_aprt");
                aprName.setText(n);

            }

            if(resultCode==RESULT_FIRST_USER)
            {
                String u=data.getStringExtra("change_manager_aprt");
                if(!u.equals(uidManager))
                {
                    adminBtn.setVisible(false);
                }

            }
        }
    }

    private void updateRoommates(String key)
    {
        get_roomies= FirebaseDatabase.getInstance().getReference().child("Apartments").child(key).child("roommates");
        get_roomies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roommates.clear();
                for(DataSnapshot snap : snapshot.getChildren())
                {

                    if(!snap.getKey().equals(mAuth.getUid()))
                    {
                        roommates.add(snap.getValue().toString());
                        usersMap.put(snap.getKey(), (String)snap.getValue());
                    }

                    if(snap.getKey().equals(uidManager))
                        manager=snap.getValue().toString();

                }
                if(roommates.isEmpty() && usersMap.isEmpty())
                {
                    listViewRoomate.setBackground(null);
                }
                else
                {
                    listAdapter=new MyListAdapter(ApartmentActivity.this,roommates,usersMap,key);
                    listViewRoomate.setAdapter(listAdapter);

                    Total_Balance(key);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void Total_Balance(String key)
    {

        total_balance=(TextView) findViewById(R.id.totalbalanceprice);
        total_price=(TextView)findViewById(R.id.shekelprice);
        balance=FirebaseDatabase.getInstance().getReference().child("Apartments").child(key).child("Balance").child(mAuth.getUid());
        balance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalBalance=0;
                for(DataSnapshot snap : snapshot.getChildren())
                {
                    for(Map.Entry<String,String> entry : usersMap.entrySet())
                    {
                        if(snap.getKey().equals(entry.getKey()))
                        {

                            number=snap.getValue(Double.class);

                                System.out.println("++!!++!! "+ number);
                               totalBalance+=number;


                            break;
                        }
                    }
                }

                if(totalBalance>0)
                {
                    DecimalFormat df = new DecimalFormat("#.##");

                    total_balance.setText("You owed");
                    total_price.setText(df.format(totalBalance)+" \u20AA");
                    total_balance.setTextColor(Color.parseColor("#49911D"));
                    total_price.setTextColor(Color.parseColor("#49911D"));
                }
                if(totalBalance<0)
                {
                    DecimalFormat df = new DecimalFormat("#.##");
                    total_balance.setText("You owe"+ df.format(-totalBalance)+" \u20AA");
                    total_balance.setTextColor(Color.parseColor("#D17F06"));
                }
                if(totalBalance==0)
                {
                    DecimalFormat df = new DecimalFormat("#.##");
                    total_balance.setText("You are even"+ df.format(totalBalance)+" \u20AA");
                    total_balance.setTextColor(Color.parseColor("#1b66b1"));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}