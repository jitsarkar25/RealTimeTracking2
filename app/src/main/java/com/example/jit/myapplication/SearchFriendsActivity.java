package com.example.jit.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchFriendsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "com.example.jit.myapplication";
    private DatabaseReference databaseReference;
    FirebaseUser user;
    EditText etSearch;
    Button bSearch,bSendFriendReq;
    TextView tvMsgFriend;
    LinearLayout linearLayout;
    String token,dbId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        etSearch=(EditText)findViewById(R.id.etSearchFriend);
        bSearch=(Button)findViewById(R.id.bSearchFriend);
        tvMsgFriend=(TextView)findViewById(R.id.tvMessageFriend);
        bSendFriendReq=(Button)findViewById(R.id.bSendFriendReq);
        linearLayout=(LinearLayout)findViewById(R.id.llFriendFound);
        bSearch.setOnClickListener(this);
        bSendFriendReq.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.bSearchFriend:

                    String enteredId = etSearch.getText().toString().trim();
                    Log.d("Value", enteredId);
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(enteredId);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.getValue() != null) {
                                 dbId = dataSnapshot.getValue().toString();

                                //fetching user name from database
                                DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("details").child(dbId);
                                databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserInformation userInformation= dataSnapshot.getValue(UserInformation.class);
                                        Log.d("Value",userInformation.name);
                                        linearLayout.setVisibility(View.VISIBLE);
                                        tvMsgFriend.setText(userInformation.name);

                                        //fetching firebase token from database

                                        DatabaseReference databaseReferenceToken = FirebaseDatabase.getInstance().getReference().child("token").child(dbId);
                                        databaseReferenceToken.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.getValue() != null) {
                                                    token = dataSnapshot.getValue().toString();
                                                    Log.d("Token",token);
                                                }
                                                else{
                                                    token = "";
                                                    Log.d("Token","no token");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }else
                            {
                                Toast.makeText(getApplicationContext(),"User Not Found",Toast.LENGTH_LONG).show();
                                linearLayout.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                break;
            case R.id.bSendFriendReq:
                sendFriendRequest();
                break;
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

    private void sendFriendRequest(){
        DatabaseReference databaseReferenceFrndReq = FirebaseDatabase.getInstance().getReference();


        databaseReferenceFrndReq.child("FriendReq").child(dbId).child(user.getUid()).setValue(false);
        String url = "http://demobintest-com.stackstaging.com/send_notification.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
              //  progressDialog.dismiss();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> params =new HashMap<>();
                SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                String name = sharedPreferences.getString("username","");
                params.put("key",token);
                params.put("message",name + " has send you a friend request");
                params.put("title","Friend Request");
                params.put("open","OPEN_ACTIVITY_1");

                return params;
            }

        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
