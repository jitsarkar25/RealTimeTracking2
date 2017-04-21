package com.example.jit.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrackingRequestActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "com.example.jit.myapplication";
    private DatabaseReference databaseReference;

    ListView lvTrackingReqs;
    private FirebaseUser user;
    private ArrayList<UserInformation> friendname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_request);
        lvTrackingReqs = (ListView)findViewById(R.id.lvMyTrackingReqs);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    populateList();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }
    public void populateList(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("TrackReq").child(user.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.d("Value",dataSnapshot.getValue().toString());
                if(dataSnapshot.getValue() == null)
                {
                    Log.d("Value","No Tracking Request");
                }
                else {
                    friendname = new ArrayList<UserInformation>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getValue().toString().equals("false")) {
                            final String key = child.getKey();
                            //fetching the user
                            DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("details").child(key);
                            databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                                    Log.d("Value Name", userInformation.name);
                                    friendname.add(userInformation);
                                    Log.d("Adapter", friendname.toString());
                                    /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FriendRequestActivity.this,android.R.layout.simple_expandable_list_item_1,friendname);
                                    lvFriendReq.setAdapter(arrayAdapter);*/
                                    ListAdapter listAdapter = new TrackingReqAdapter(TrackingRequestActivity.this,friendname);
                                    lvTrackingReqs.setAdapter(listAdapter);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                            /*Log.d("Value",child.toString());
                            Log.d("Value",child.getValue().toString());*/
                    }
                    Log.d("Adapter last", friendname.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}
