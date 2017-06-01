package com.example.jit.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseAvatar extends AppCompatActivity {

    String picname="";
    FirebaseUser user;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_avatar);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void onepic(View v){
        picname="userone.png";
        assignAvatar();
    }
    public void twopic(View v){
        picname="usertwo";
        assignAvatar();
    }
    public void threepic(View v){
        picname="userthree";
        assignAvatar();
    }
    public void fourpic(View v){
        picname="userfour";
        assignAvatar();
    }
    public void fivepic(View v){
        picname="userfive";
        assignAvatar();
    }
    public void sixpic(View v){
        picname="usersix";
        assignAvatar();
    }

    public void assignAvatar(){

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("avatar").child(user.getUid()).setValue(picname);
        Intent intent = new Intent(getApplicationContext(),UserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
