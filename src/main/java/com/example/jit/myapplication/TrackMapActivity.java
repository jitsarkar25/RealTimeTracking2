package com.example.jit.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_MAGENTA;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

public class TrackMapActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener,LocationListener {

    private GoogleMap mMap;
    int c=0,d=0,f=0;
    double a1,b1,f1,f2,c1,d1=0.0;
    Marker marker1,marker2,marker3;
    Button stopTracking,addMembers;
    private String senderId,checknotifi,value="";
    private DatabaseReference databaseReference,databaseReference1;
    private String userid;
    boolean isGroupTrack =false;
    LocationManager locationManager;
    private ArrayList<UserInformation> membersJoinedList;
    private ArrayList <String> membersJoinedKeyList;
    private boolean showme=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        stopTracking = (Button) findViewById(R.id.bStopTracking);
       // addMembers = (Button) findViewById(R.id.bAddMembers);
        stopTracking.setOnClickListener(this);
     //   addMembers.setOnClickListener(this);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1,this);
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

    @Override
    public void onBackPressed() {
        // do nothing.
    }

    public void showMe(View v){
        showme=true;
    }
    public void showFriend(View v)
    {
        showme=false;
    }
    public void checkTrackSession(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("TrackSession").child(userid+"-"+senderId).child("track");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value=dataSnapshot.getValue().toString();
                String key=dataSnapshot.getKey();
                Log.d("Key obtained",key);
                String parts[]=value.split(",");
                if(key.equals(senderId)) {
                    /*Log.d("added location",parts[1]+"   ,   "+parts[2]);
                    Toast.makeText(getApplicationContext(),parts[1]+" "+parts[2],Toast.LENGTH_SHORT).show();  */
                    LatLng sydney = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                    if (d == 0) {
                        //mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
                        marker2 = mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
                    }
                    if(!showme)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.5f));
                    if (d != 0) {
                        marker2.remove();
                        mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(c1, d1), sydney)
                                .width(5).color(Color.BLUE).geodesic(true));
                    }
                    marker2 = mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
                    if(!showme)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.5f));

                    c1 = Double.parseDouble(parts[0]);
                    d1 = Double.parseDouble(parts[1]);
                    d = 1;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(!value.equals(dataSnapshot.getValue().toString())) {
                    value = dataSnapshot.getValue().toString();
                    String key=dataSnapshot.getKey();
                    Log.d("Key obtained",key);
                    String parts[] = value.split(",");
                    Log.d("changed location", value + "    " + "senderid=" + senderId);
                    if (key.equals(senderId)) {
                        LatLng sydney = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                        if(c==0) {
                            //mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
                            marker1=mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
                            mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(c1,d1), sydney)
                                    .width(5).color(Color.BLUE).geodesic(true));
                        }
                        if(!showme)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,17.5f));

                            marker1.remove();
                        if(c!=0)
                            mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(a1,b1), sydney)
                                    .width(5).color(Color.BLUE).geodesic(true));

                        marker1=mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
                        if(!showme)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,17.5f));
                        a1=Double.parseDouble(parts[0]);
                        b1=Double.parseDouble(parts[1]);
                        c=1;
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


/*
    private void addMembersTrack(){

       /* databaseReference = FirebaseDatabase.getInstance().getReference().child("GroupTrack").child(userid).child(userid);
        databaseReference.setValue(true);
        isGroupTrack = true;
        startActivity(new Intent(getApplicationContext(),AddMemberToTrackActivity.class));

    }
*/
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
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
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng position =  new LatLng(location.getLatitude(),location.getLongitude());

        if(f==0) {
            marker3=mMap.addMarker(new MarkerOptions().position(position).title("First Your Location").icon(BitmapDescriptorFactory.defaultMarker(HUE_MAGENTA)));
        }
        if(showme)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,17.5f));
        if(f!=0) {
            marker3.remove();
            mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(f1,f2), position)
                    .width(5).color(Color.GREEN).geodesic(true));
        }
        marker3=mMap.addMarker(new MarkerOptions().position(position).title("First Your Location").icon(BitmapDescriptorFactory.defaultMarker(HUE_MAGENTA)));
        f1=location.getLatitude();
        f2=location.getLongitude();
        f=1;
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        if(senderId!=null) {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("TrackSession").child(userid + "-" + senderId).child("track").child(userid);
            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("TrackSession").child(senderId + "-" + userid).child("track").child(userid);
            databaseReference.setValue(location.getLatitude() + "," + location.getLongitude());
            databaseReference1.setValue( location.getLatitude() + "," + location.getLongitude());

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
