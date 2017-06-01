package com.example.jit.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "com.example.jit.myapplication";
    private DatabaseReference databaseReference;
    FirebaseUser user;
    TextView textView, uniqueID,searchfriend,mytask,friendreq,myfriends,trackingreq,emergencysetings;
    double longitude, latitude;
    LocationManager locationManager;
    // Button logout;
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    ProgressDialog progress;
    private GoogleApiClient mGoogleApiClient;
    ImageView ivLogout,userimage;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String _Location = "", _country_name = "", _Locality = "", Full_Location = "", _Sub_Locality = "", _postal_code = "",username,dp="userone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        textView = (TextView) findViewById(R.id.tvNameUser);
        searchfriend = (TextView) findViewById(R.id.search_user);
        mytask = (TextView) findViewById(R.id.tvMyTask);
        friendreq = (TextView) findViewById(R.id.tvFriendReq);
        myfriends = (TextView) findViewById(R.id.tvMyFriend);
        trackingreq = (TextView) findViewById(R.id.tvTrackingReq);
        emergencysetings = (TextView) findViewById(R.id.tvEmergencySet);
        uniqueID = (TextView) findViewById(R.id.tvUniqueId);
        ivLogout = (ImageView) findViewById(R.id.ivLogout);
        userimage = (ImageView) findViewById(R.id.user_profile_photo);
        ivLogout.setOnClickListener(this);
//        logout = (Button)findViewById(R.id.bLogout);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location!=null)
        {
            Log.i("Location","LocationYES");

        }
        else
        {
            Log.i("Location","LocationNO");

        }

        progress = new ProgressDialog(UserActivity.this);
        progress.setTitle("Please Wait");
        progress.setMessage("Fetching details");
        progress.show();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    databaseReference= FirebaseDatabase.getInstance().getReference().child("details").child(user.getUid());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            UserInformation userInformation= dataSnapshot.getValue(UserInformation.class);

                         progress.dismiss();
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("avatar").child(user.getUid());
                            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dp=dataSnapshot.getValue(String.class);
                                    editor.putString("dp",dp);
                                    Log.d("avatarId",dp+" dp");
                                    if(dp==null)
                                    {
                                        userimage.setImageResource(R.drawable.userone);
                                    }
                                    else {
                                        int id = getResources().getIdentifier("com.example.jit.myapplication:drawable/" + dp, null, null);
                                        Log.d("avatarId", id + "");
                                        userimage.setImageResource(id);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            editor.putString("username",userInformation.name);
                            editor.putString("id",userInformation.id);
                            editor.putString("phone",userInformation.phone);
                            editor.putString("serverid",user.getUid());



                            editor.commit();
                            textView.setText(userInformation.name);
                            uniqueID.setText("Unique Id " + userInformation.id);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

//        logout.setOnClickListener(this);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent i = new Intent(getApplicationContext(),TaskService.class);
                    startService(i);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.ivLogout:
                SharedPreferences sharedPreferences2 = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.putBoolean("islogin",false);
                Log.d("IsAnon",sharedPreferences2.getBoolean("isanonymous",false)+"");
                Log.d("IsAnon id",user.getUid());
                if(sharedPreferences2.getBoolean("isanonymous",false))
                {
                    databaseReference=FirebaseDatabase.getInstance().getReference().child("details").child(user.getUid());
                    databaseReference.removeValue();
                    SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                    databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("id",""));
                    databaseReference.removeValue();
                }
                editor2.putBoolean("isanonymous",false);
                editor2.commit();
                SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                sharedPreferences = getSharedPreferences("otpcheck",MODE_PRIVATE);
                SharedPreferences.Editor editor4=sharedPreferences.edit();
                editor4.clear();
                editor4.commit();
                LoginManager.getInstance().logOut();
                FirebaseDatabase.getInstance().getReference().child("token").child(user.getUid()).removeValue();
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences sharedPreferences2 = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
        if(sharedPreferences2.getBoolean("isanonymous",false)) {
            getMenuInflater().inflate(R.menu.anonymoususermenu, menu);
            searchfriend.setVisibility(View.GONE);
            mytask.setVisibility(View.GONE);
            myfriends.setVisibility(View.GONE);
            friendreq.setVisibility(View.GONE);
            emergencysetings.setVisibility(View.GONE);
        }
        else
        {
            getMenuInflater().inflate(R.menu.useractivitymenu, menu);


        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.search_user:
                startActivity(new Intent(getApplicationContext(),SearchFriendsActivity.class));
                break;
            case R.id.friendreq:
                startActivity(new Intent(getApplicationContext(),FriendRequestActivity.class));
                break;
            case R.id.myfriends:
                startActivity(new Intent(getApplicationContext(),MyFriendsActivity.class));
                break;
            case R.id.chatfriends:
                startActivity(new Intent(getApplicationContext(),ChatLIst.class));
                break;
            case R.id.trackreqs:
                startActivity(new Intent(getApplicationContext(),TrackingRequestActivity.class));
                break;
            case R.id.menutasks:
                startActivity(new Intent(getApplicationContext(),MyTasksActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void searchuser(View v)
    {
        startActivity(new Intent(getApplicationContext(),SearchFriendsActivity.class));
    }
    public void mytasks(View v)
    {
        startActivity(new Intent(getApplicationContext(),MyTasksActivity.class));
    }
    public void myfriends(View v)
    {
        startActivity(new Intent(getApplicationContext(),MyFriendsActivity.class));
    }
    public void trackrequest(View v)
    {
        startActivity(new Intent(getApplicationContext(),TrackingRequestActivity.class));
    }
    public void friendreq(View v)
    {
        startActivity(new Intent(getApplicationContext(),FriendRequestActivity.class));
    }

    public void emergencysetting(View v)
    {
        startActivity(new Intent(getApplicationContext(),EmergencySettings.class));
    }

    public void emergencysend(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences("otpcheck", MODE_PRIVATE);
        String c1 = sharedPreferences.getString("contact1", "");
        String c2 = sharedPreferences.getString("contact2", "");
        String c3 = sharedPreferences.getString("contact3", "");
        SharedPreferences sharedPreferences2 = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        username=sharedPreferences2.getString("username","");
        String m1 = "HELP "+username;
        double lat=((int)(latitude*100))/100.0;
        double lon=((int)(longitude*100))/100.0;
        //Log.d(lat+" "+lon,"barnali")2;
        Toast.makeText(this,lat+" "+lon,Toast.LENGTH_LONG).show();
        Log.d("msgcheck","locality "+_Locality);
        Log.d("msgcheck","location "+_Location);
        Log.d("msgcheck","country "+_country_name);
        Log.d("msgcheck","postal "+_postal_code);
        Log.d("msgcheck","sublocality "+_Sub_Locality);
      //  Log.d("msgcheck","locality "+_Locality);
        m1=m1+" AT LOCATION: "+Full_Location+" "+ lat+" "+lon;
        String url = "https://control.msg91.com/api/sendhttp.php?authkey=151462A3CrVZc2590d79f6&mobiles=" + c1 + "," + c2 + "," + c3 + "&message=" + m1 + "&sender=trckit&route=4";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
   }

    @Override
    public void onLocationChanged(Location location) {
        longitude=location.getLongitude();
        latitude=location.getLatitude();
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (null != listAddresses && listAddresses.size() > 0) {
                if(listAddresses.get(0).getAddressLine(0)!=null)
                    _Location = listAddresses.get(0).getAddressLine(0);
                if(listAddresses.get(0).getCountryName()!=null)
                    _country_name=listAddresses.get(0).getCountryName();
                if(listAddresses.get(0).getLocality()!=null)
                    _Locality=listAddresses.get(0).getLocality();
                if(listAddresses.get(0).getSubLocality()!=null)
                    _Sub_Locality=listAddresses.get(0).getSubLocality();
                if(listAddresses.get(0).getPostalCode()!=null)
                    _postal_code=listAddresses.get(0).getPostalCode();

                Full_Location=_Location+", "+_Sub_Locality+", "+_postal_code+", "+_country_name;
                Log.i("Full_address",Full_Location);
            }
        }catch (IOException e)

        {e.printStackTrace();
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
