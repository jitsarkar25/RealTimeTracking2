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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Jit on 30-05-2017.
 */

public class ChatListAdapter extends ArrayAdapter<UserInformation> {
    Context con;
    private DatabaseReference databaseReference;

    public ChatListAdapter(Context context, List<UserInformation> userInformations) {
        super(context, R.layout.chatlistcontent, userInformations);
        con = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.chatlistcontent, parent, false);

        final UserInformation userInformation = getItem(position);
        TextView name = (TextView) view.findViewById(R.id.tvChatFriendName);
        name.setText(userInformation.name);


        return view;
    }
}
