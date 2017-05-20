package com.example.jit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrackingActivity extends AppCompatActivity implements View.OnClickListener,LocationListener{

    Button stopTracking,addMembers;
    private String senderId,checknotifi,value="";
    private DatabaseReference databaseReference,databaseReference1;
    private String userid;
    boolean isGroupTrack =false;
    LocationManager locationManager;
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

       // Log.d("senderId2",senderId);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // provider = locationManager.getBestProvider(new Criteria(), false);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {

            Log.i("Location Info", "Location achieved!");

        } else {

            Log.i("Location Info", "No location :(");

        }

    }

    public void checkTrackSession(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("TrackSession").child(userid+"-"+senderId);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value=dataSnapshot.getValue().toString();
                String parts[]=value.split(",");
                if(parts[0].equals(senderId))
                {
                    Log.d("added location",parts[1]+"   ,   "+parts[2]);
                    Toast.makeText(getApplicationContext(),parts[1]+" "+parts[2],Toast.LENGTH_SHORT).show();               }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(!value.equals(dataSnapshot.getValue().toString())) {
                    value = dataSnapshot.getValue().toString();
                    String parts[] = value.split(",");
                    Log.d("changed location", value + "    " + "senderid=" + senderId);
                    if (parts[0].equals(senderId)) {
                        Log.d("changed location", parts[1] + "   ,   " + parts[2]);
                        Toast.makeText(getApplicationContext(), parts[1] + " " + parts[2], Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void check(String userid){
        Log.d("Sender", userid + " " + senderId);
        checkTrackSession();
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

    @Override
    public void onLocationChanged(Location location) {

        if(senderId!=null) {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("TrackSession").child(userid + "-" + senderId).child("track");
            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("TrackSession").child(senderId + "-" + userid).child("track");
            databaseReference.setValue(userid + "," + location.getLatitude() + "," + location.getLongitude());
            databaseReference1.setValue(userid + "," + location.getLatitude() + "," + location.getLongitude());

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}