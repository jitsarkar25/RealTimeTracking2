package com.example.jit.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
 * Created by Viper PC on 4/24/2017.
 */
public class AddMembersToTrackAdapter extends ArrayAdapter<UserInformation> {
    Context con;
    private DatabaseReference databaseReference;
    public AddMembersToTrackAdapter(Context context, List<UserInformation> userInformations) {
        super(context,R.layout.addmembertotrackcontent, userInformations);
        con=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View view=layoutInflater.inflate(R.layout.addmembertotrackcontent, parent, false);


        final UserInformation userInformation=getItem(position);
        TextView name=(TextView)view.findViewById(R.id.tvAddMemberFriendName);
        name.setText(userInformation.name);
        Log.d("menu list 2",AddMemberToTrackActivity.positionList.toString());
        if(AddMemberToTrackActivity.positionList.contains(position+""))
        {
            Log.d("menu check","here");
            view.setBackgroundColor(Color.parseColor("#8AB1CA"));
        }
        else
        {
            view.setBackgroundColor(Color.parseColor("#ffffff"));
        }



        return view;
    }
}
