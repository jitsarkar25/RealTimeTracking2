package com.example.jit.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddMemberToTrackActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "com.example.jit.myapplication";
    private DatabaseReference databaseReference;
    ListView lvFriends;
    private FirebaseUser user;
    private ArrayList<UserInformation> friendname;
    static ArrayList<String> positionList;
    private int count =0;
    private String userid = "";
    ArrayAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_to_track);
        lvFriends = (ListView) findViewById(R.id.lvAddMemberMyfriends);
        mAuth = FirebaseAuth.getInstance();
        positionList = new ArrayList<>();
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

        lvFriends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvFriends.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if(checked) {
                    positionList.add(position+"");
                    count++;
                }
                else {
                    positionList.remove(position+"");
                    count--;
                }
                mode.setTitle(count + " members selected");
                Log.d("menu position", position + "");
                Log.d("menu checked" , checked+"");
                Log.d("menu list" , positionList.toString());
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.addmembertotrack, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_addmember:
                        for(String pos : positionList){
                           // Toast.makeText(getApplicationContext(),friendname.get(Integer.parseInt(pos)).name,Toast.LENGTH_SHORT).show();
                            if(userid == "")
                            {
                                SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                                userid = sharedPreferences.getString("serverid", "");
                            }
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("GroupTrack").child(userid).child(friendname.get(Integer.parseInt(pos)).getKey());
                            databaseReference.setValue(false);
                        }
                        finish();
                        break;
                }
                return true;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {


            }
        });

    }
    public void populateList(){
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
                                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                                    Log.d("Value Name", userInformation.name);
                                    userInformation.setKey(key);
                                    friendname.add(userInformation);
                                    Log.d("Adapter", friendname.toString());
                                    /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FriendRequestActivity.this,android.R.layout.simple_expandable_list_item_1,friendname);
                                    lvFriendReq.setAdapter(arrayAdapter);*/
                                    listAdapter = new AddMembersToTrackAdapter(AddMemberToTrackActivity.this,friendname);
                                    lvFriends.setAdapter(listAdapter);
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
