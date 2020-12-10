package com.example.rommies;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManagerActivity extends AppCompatActivity {

    private ListView listViewRoomate;
    private ArrayAdapter<String> adapter;
    private Dialog d;
    private Map<String, String> contacts = new HashMap<>();
    private Map<String, String> usersMap = new HashMap<>();
    private Map<String, String> deleteMapUser = new HashMap<>();
    private static final int CONTACT_PICKER_RESULT = 1001;
    private static final int REQUEST_READ_CONTACTS = 1;
    private static final int REQUEST_SEND_SMS = 2;
    private static final String DEBUG_TAG = "0";
     ArrayList<String> roommates;
    DatabaseReference dbf;
     DatabaseReference delete_roomies;
    DatabaseReference delete_Idaprt_from_users;
    FirebaseAuth fAuth;
    Button addrommies;
    Button deleterommies;
    Button changenameaprt;
    TextView textmanage;
    String aprkey="";
    String manager="";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        textmanage=(TextView)findViewById(R.id.textViewmanager);
        addrommies=(Button)findViewById(R.id.buttonAddRommies);
        deleterommies=(Button)findViewById(R.id.buttonDeleteRommies);
        changenameaprt=(Button)findViewById(R.id.buttonNameApartament);

        roommates=new ArrayList<>();
        fAuth=FirebaseAuth.getInstance();

        if(getIntent().hasExtra("keyaprt") && getIntent().hasExtra("list") && getIntent().hasExtra("hash"))
        {
            aprkey=getIntent().getStringExtra("keyaprt");
            roommates=getIntent().getStringArrayListExtra("list");
            usersMap=(HashMap<String, String>)getIntent().getSerializableExtra("hash");
            for(int i=0; i<roommates.size(); i++)
            {
                System.out.println(roommates.get(i)+ " !!!!!!");
            }
            for(Map.Entry<String,String> entry: usersMap.entrySet())
            {
                System.out.println(entry.getKey() + " ????" + entry.getValue());
            }
        }
        dbf=FirebaseDatabase.getInstance().getReference().child("Apartments").child(aprkey).child("roommates").child(fAuth.getUid());
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                manager=snapshot.getValue().toString();
                textmanage.setText("Hello "+manager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addrommies.setOnClickListener((v)->{


            d = new Dialog(this);
            d.setContentView(R.layout.invite_friends);
            d.setTitle("invite_friends");
            d.setCancelable(true);
            d.show();
            ((Button)d.findViewById(R.id.SendSms)).setOnClickListener((v1 ->
            {

                if(!contacts.isEmpty())
                {
                    if(checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
                    }
                    else
                    {

                        Send_SMS();

                    }
                }
                else
                {
                    Toast.makeText(ManagerActivity.this,"you need to choose a friend",Toast.LENGTH_SHORT).show();
                }
                d.dismiss();

            }));


        });

        deleterommies.setOnClickListener((v)->{
            Button cancel;
            d = new Dialog(this);
            d.setContentView(R.layout.delete_roomies);
            d.setTitle("Long press to delete roomies");
            d.setCancelable(true);
            d.show();
            cancel=(Button)d.findViewById(R.id.BtnCancele);
            listViewRoomate=(ListView)d.findViewById(R.id.listviewchoose);
            adapter=new ArrayAdapter<>(ManagerActivity.this, android.R.layout.simple_list_item_1,roommates);
            listViewRoomate.setAdapter(adapter);
            listViewRoomate.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    final int item=position;

                        new AlertDialog.Builder(ManagerActivity.this)
                                .setIcon(android.R.drawable.ic_delete)
                                .setTitle("Are you sure ?")
                                .setMessage("Do you want to delete this roomie")
                                .setPositiveButton("Yes",new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        System.out.println("111111111");
                                        for(Map.Entry<String,String> entry: usersMap.entrySet())
                                        {
                                            System.out.println("000000000"+entry.getKey());
                                            String uid=entry.getKey();
                                            String name=entry.getValue();
                                            if(usersMap.containsKey(uid) && name.equals(roommates.get(item)))
                                            {

                                                deleteMapUser.put(uid,name);
                                                usersMap.remove(uid);
                                                break;

                                            }
                                        }
                                        roommates.remove(item);
                                        adapter.notifyDataSetChanged();

                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();

                    return true;
                }
            });


            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_from_firebase();
                    d.dismiss();
                }
            });




        });




    }



    private void delete_from_firebase()
    {
        if(!deleteMapUser.isEmpty())
        {
            delete_roomies=FirebaseDatabase.getInstance().getReference().child("Apartments").child(aprkey).child("roommates");

            delete_roomies.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot snap : snapshot.getChildren())
                    {
                        String uidkey=snap.getKey();
                        String username=snap.getValue().toString();

                        for(Map.Entry<String,String> entry : deleteMapUser.entrySet())
                        {
                            if(entry.getKey().equals(uidkey) && entry.getValue().equals(username))
                                snap.getRef().removeValue();
                        }

                    }

                    deleteUsersIdApart();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void deleteUsersIdApart()
    {
        if(!deleteMapUser.isEmpty())
        {
            for(Map.Entry<String,String> entry : deleteMapUser.entrySet())
            {
                String uid=entry.getKey();

                delete_Idaprt_from_users=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                delete_Idaprt_from_users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild("Apartment_key"))
                        {
                            snapshot.getRef().child("Apartment_key").removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void handleAdd(View v)//check whether read contacts permission is enabled
    {
        if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        else
            LaunchContactPicker();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ResourceType")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)//read the details of selected contact
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case CONTACT_PICKER_RESULT:
                    Cursor phones = null, names = null;
                    String number = null, name;
                    try {
                        Uri result = data.getData();
                        Log.v(DEBUG_TAG, "Got a contact result: " + result.toString());
                        String id = result.getLastPathSegment();
                        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                        if(phones.moveToFirst())
                        {
                            name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Log.v(DEBUG_TAG, "Got phone number: " + number);
                            Log.v(DEBUG_TAG, "Got User name: " + name);
                            if(contacts.containsValue(number)) {
                                Toast.makeText(this, "This phone number is already on the list", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            contacts.put(name, number);

                            LinearLayout ll = d.findViewById(R.id.linearLayout);
                            RelativeLayout rl = new RelativeLayout(d.getContext());
                            //TextView 1
                            TextView tv1 = new TextView(d.getContext());
                            tv1.setText(name);
                            tv1.setTextAppearance(getApplicationContext(),R.style.TextAppearance_AppCompat_Medium);
                            tv1.setId(1);
                            //remove "button"

                            Button bt = new Button(d.getContext(),null,android.R.style.Widget_Material_Light_Button_Small);
                            bt.setBackgroundResource(R.drawable.ic_baseline_delete_24);
                            bt.setOnClickListener((v)->
                            {
                                contacts.remove(name);
                                ll.removeView(rl);
                            });
                            RelativeLayout.LayoutParams rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            RelativeLayout.LayoutParams tv_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            RelativeLayout.LayoutParams bt_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            tv_lp.setMargins(40, 0,0,0);
                            bt_lp.setMargins(30,0,0,0);
                            bt_lp.addRule(RelativeLayout.RIGHT_OF, 1);
                            rl.addView(tv1,tv_lp);
                            rl.addView(bt,bt_lp);
                            ll.addView(rl, rl_lp);
                        } else {
                            Log.w(DEBUG_TAG, "No results");
                        }

                    } catch (Exception e) {
                        Log.e(DEBUG_TAG, "Failed to get phone number data", e);
                    } finally {
                        if (phones != null) {
                            phones.close();
                        }
                        if (number.length() == 0) {
                            Toast.makeText(this, "No phone number found for contact.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
        else {
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LaunchContactPicker();
                }
                break;
            }
            case REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Send_SMS();
                }
                break;
            }

        }
    }
    private void LaunchContactPicker()//open contacts
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }
    private void Send_SMS()
    {


        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        ArrayList<PendingIntent> sentPI = new ArrayList<>();
        sentPI.add(PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0));
        ArrayList<PendingIntent> deliveredPI = new ArrayList<>();
        deliveredPI.add(PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0));

        for(Map.Entry<String,String> entry: contacts.entrySet())
        {
            String message = "hey " + entry.getKey() +"\n Come and join my apartment.\n Copy and paste this key in your Roomies app.\n "+ aprkey;
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);
            sms.sendMultipartTextMessage(entry.getValue(),null, parts,sentPI, deliveredPI );
            Log.v(DEBUG_TAG,message);
        }


    }

}