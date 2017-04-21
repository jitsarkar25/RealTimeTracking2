package com.example.jit.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jit on 16-04-2017.
 */
public class MyFriendsAdapter extends ArrayAdapter<UserInformation> {
    Context con;
    private DatabaseReference databaseReference;
    public MyFriendsAdapter(Context context, List<UserInformation> userInformations) {
        super(context,R.layout.myfriendscontent, userInformations);
        con=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View view=layoutInflater.inflate(R.layout.myfriendscontent, parent, false);

        final UserInformation userInformation=getItem(position);
        TextView name=(TextView)view.findViewById(R.id.tvMyFriendName);
        name.setText(userInformation.name);

        Button bSendTrackingRequest = (Button) view.findViewById(R.id.bSendTrackReq);
        bSendTrackingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sharedPreferences = con.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                String senderid = sharedPreferences.getString("serverid", "");
                final String myname = sharedPreferences.getString("username", "");
                Log.d("My name",myname);
                String receiverid = userInformation.getKey();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("TrackReq").child(receiverid).child(senderid);
                databaseReference.setValue("false");
                Toast.makeText(con, "Tracking Request Sent", Toast.LENGTH_SHORT).show();

                final String[] token = new String[1];
                final DatabaseReference databaseReferenceAcceptNotification = FirebaseDatabase.getInstance().getReference().child("token").child(receiverid);
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
        });


        Button removeFriend = (Button) view.findViewById(R.id.bRemoveFriend);
        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = con.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                String senderid = sharedPreferences.getString("serverid", "");
                String receiverid = userInformation.getKey();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FriendReq").child(senderid).child(receiverid);
                databaseReference.removeValue();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("FriendReq").child(receiverid).child(senderid);
                databaseReference.removeValue();
                Toast.makeText(con,"Friend Removed",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    public void sendAcceptNotification(String senderid, final String name){

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
                Log.d("Token 0", token + "\n " + name);
                params.put("key", token);
                params.put("message",name + " wants to track you");
                params.put("title","Tracking Request");
                params.put("open","OPEN_ACTIVITY_1");

                return params;
            }

        };

        MySingleton.getInstance(con).addToRequestQueue(stringRequest);
    }
}
