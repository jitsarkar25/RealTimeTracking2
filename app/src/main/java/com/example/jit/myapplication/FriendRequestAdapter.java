package com.example.jit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jit on 15-04-2017.
 */
public class FriendRequestAdapter extends ArrayAdapter<UserInformation> {
    Context con;
    private DatabaseReference databaseReference;
    public FriendRequestAdapter(Context context, List<UserInformation> userInformations) {
        super(context,R.layout.friendrequestcontent, userInformations);
        con=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View view=layoutInflater.inflate(R.layout.friendrequestcontent, parent, false);

        final UserInformation userInformation=getItem(position);
        TextView name=(TextView)view.findViewById(R.id.tvFriendReqName);
        name.setText(userInformation.name);

        Button bConfirm = (Button)view.findViewById(R.id.bAcceptFriendReq);
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = con.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                final String userid = sharedPreferences.getString("serverid", "");
                final String myname = sharedPreferences.getString("username", "");
                final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("users").child(userInformation.id);
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String senderid = dataSnapshot.getValue(String.class);
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("FriendReq").child(userid).child(senderid);
                        databaseReference.setValue("true");
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("FriendReq").child(senderid).child(userid);
                        databaseReference.setValue("true");
                        Toast.makeText(con, "Friend Request Accepted", Toast.LENGTH_SHORT).show();

                        //sendind notification for accepted request
                        final String[] token = new String[1];
                        final DatabaseReference databaseReferenceAcceptNotification = FirebaseDatabase.getInstance().getReference().child("token").child(senderid);
                        databaseReferenceAcceptNotification.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                token[0] = dataSnapshot.getValue(String.class);
                                sendAcceptNotification(token[0], myname);
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

            }
        });
        Button bReject = (Button)view.findViewById(R.id.bRejectFriendReq);
        bReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = con.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                final String userid=sharedPreferences.getString("serverid", "");
                final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("users").child(userInformation.id);
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String senderid = dataSnapshot.getValue(String.class);
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("FriendReq").child(userid).child(senderid);
                        databaseReference.removeValue();
                        Toast.makeText(con, "Friend Request Rejected",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        return view;
    }
public void sendAcceptNotification(String senderid, final String username){

    final String token = senderid;
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
            Log.d("Token 0", token +"\n "+ username );
            params.put("key", token);
            params.put("message",username + " has accepted your friend request");
            params.put("title","Friend Request");
            params.put("open","OPEN_MY_FRIENDS");

            return params;
        }

    };

    MySingleton.getInstance(con).addToRequestQueue(stringRequest);
}
}



