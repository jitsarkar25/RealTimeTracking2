package com.example.jit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrackingActivity extends AppCompatActivity implements View.OnClickListener{

    Button stopTracking,addMembers;
    private String senderId,checknotifi;
    private DatabaseReference databaseReference;
    private String userid;
    boolean isGroupTrack =false;
    private ArrayList <UserInformation> membersJoinedList;
    private ArrayList <String> membersJoinedKeyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        stopTracking = (Button) findViewById(R.id.bStopTracking);
        addMembers = (Button) findViewById(R.id.bAddMembers);
        stopTracking.setOnClickListener(this);
        addMembers.setOnClickListener(this);
        membersJoinedKeyList = new ArrayList<>();
        membersJoinedList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
       userid = sharedPreferences.getString("serverid", "");
        senderId = checknotifi = getIntent().getStringExtra("senderid");
        if(senderId == null)
        {
            //senderId =
            databaseReference = FirebaseDatabase.getInstance().getReference().child("TrackReq").child(userid);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Formateed value", dataSnapshot.getValue().toString());
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getValue().toString().equals("true")) {
                            final String key = child.getKey();
                            //fetching the user
                            Log.d("The Key",key);
                            senderId = key;
                            check(userid);
;                        }

                            /*Log.d("Value",child.toString());
                            Log.d("Value",child.getValue().toString());*/
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
            check(userid);


    }

    public void check(String userid){
        Log.d("Sender", userid + " " + senderId);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("TrackReq").child(userid).child(senderId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null)
                {
                    Log.d("Value Change","null");
                    Toast.makeText(getApplicationContext(), "Tracking has been stopped", Toast.LENGTH_SHORT).show();
                    if(checknotifi == null)
                    {
                     startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                        finish();
                }
                else {
                    String val = dataSnapshot.getValue(String.class);
                    Log.d("Value change ", val);

                }
                // finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.bStopTracking:
                SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                String userid = sharedPreferences.getString("serverid", "");
                databaseReference = FirebaseDatabase.getInstance().getReference().child("TrackReq").child(userid).child(senderId);

                databaseReference.removeValue();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("TrackReq").child(senderId).child(userid);

                databaseReference.removeValue();
                Toast.makeText(getApplicationContext(),"Tracking has been stopped",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.bAddMembers :
                addMembersTrack();

                break;
        }
    }

    private void addMembersTrack(){

       /* databaseReference = FirebaseDatabase.getInstance().getReference().child("GroupTrack").child(userid).child(userid);
        databaseReference.setValue(true);*/
        isGroupTrack = true;
        startActivity(new Intent(getApplicationContext(),AddMemberToTrackActivity.class));
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isGroupTrack)
            checkGroup();
    }

    public void checkGroup()
    {
        DatabaseReference databaseReferenceGroup = FirebaseDatabase.getInstance().getReference().child("GroupTrack").child(userid);
        databaseReferenceGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             //   Toast.makeText(getApplicationContext(),"Something changed",Toast.LENGTH_LONG).show();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getValue().toString().equals("true")) {
                        for(UserInformation ui : AddMemberToTrackActivity.addedMembersList){
                            if(ui.getKey().equals(child.getKey())) {
                                if(!membersJoinedList.contains(ui)) {
                                    membersJoinedList.add(ui);
                                    membersJoinedKeyList.add(ui.getKey());
                                    Toast.makeText(getApplicationContext(), ui.name + " joined Track", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
