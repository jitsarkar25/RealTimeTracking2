package com.example.jit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyTasksActivity extends AppCompatActivity implements View.OnClickListener {

    Button addTasks;
    private DatabaseReference databaseReference;
    private ArrayList<Tasks> taskList;
    private ListView lvTasks;
    private ListAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);
        addTasks = (Button) findViewById(R.id.bAddTasks);
        lvTasks = (ListView) findViewById(R.id.lvMyTasks);
        addTasks.setOnClickListener(this);
        taskList = new ArrayList<>();
       // fetchTasks();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bAddTasks:
                startActivity(new Intent(getApplicationContext(),AddTaskActivity.class));
                break;
        }

    }

    public void fetchTasks(){
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("serverid", "");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tasks").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final String key = child.getKey();
                    Tasks myTasks = child.getValue(Tasks.class);
                    if(!taskList.contains(myTasks))
                    taskList.add(myTasks);
                    listAdapter = new TaskAdapter(MyTasksActivity.this,taskList);
                    lvTasks.setAdapter(listAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchTasks();
    }
}
