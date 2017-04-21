package com.example.jit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "com.example.jit.myapplication";
    private DatabaseReference databaseReference;
    FirebaseUser user;
    TextView textView,uniqueID;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        textView = (TextView)findViewById(R.id.tvNameUser);
        uniqueID = (TextView)findViewById(R.id.tvUniqueId);
        logout = (Button)findViewById(R.id.bLogout);

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

                         /*   Saving userinfo to sharedpreference*/
                            SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username",userInformation.name);
                            editor.putString("id",userInformation.id);
                            editor.putString("phone",userInformation.phone);
                            editor.putString("serverid",user.getUid());
                            editor.commit();
                            textView.setText("Welcome " + userInformation.name);
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

        logout.setOnClickListener(this);

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
            case R.id.bLogout:
                SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                FirebaseDatabase.getInstance().getReference().child("token").child(user.getUid()).removeValue();
                FirebaseAuth.getInstance().signOut();

                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.useractivitymenu,menu);
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
            case R.id.trackreqs:
                startActivity(new Intent(getApplicationContext(),TrackingRequestActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
