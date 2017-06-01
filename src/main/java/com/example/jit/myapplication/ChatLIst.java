package com.example.jit.myapplication;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.util.ArrayList;

public class ChatLIst extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "com.example.jit.myapplication";
    private DatabaseReference databaseReference;

    ListView lvMyFriends;
    private FirebaseUser user;
    private ArrayList<UserInformation> friendname;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lvMyFriends = (ListView)findViewById(R.id.lvChatFriends);
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
        lvMyFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(getApplicationContext(),ChatActivity.class);
                i.putExtra("pos",position);
                i.putExtra("arrayfriend",friendname);
                startActivity(i);
            }
        });
    }

    public void populateList(){
        progress = new ProgressDialog(ChatLIst.this);
        progress.setTitle("Please Wait");
        progress.setMessage("Fetching Details");
        progress.show();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("FriendReq").child(user.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.d("Value",dataSnapshot.getValue().toString());
                if(dataSnapshot.getValue() == null)
                {
                    Log.d("Value","No Friends");
                }
                else {
                    friendname = new ArrayList<UserInformation>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getValue().toString().equals("true")) {
                            final String key = child.getKey();
                            //fetching the user
                            DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("details").child(key);
                            databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    progress.dismiss();
                                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                                    Log.d("Value Name", userInformation.name);
                                    userInformation.setKey(key);
                                    friendname.add(userInformation);
                                    Log.d("Adapter", friendname.toString());
                                    /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FriendRequestActivity.this,android.R.layout.simple_expandable_list_item_1,friendname);
                                    lvFriendReq.setAdapter(arrayAdapter);*/
                                    ListAdapter listAdapter = new ChatListAdapter(ChatLIst.this,friendname);
                                    lvMyFriends.setAdapter(listAdapter);
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
