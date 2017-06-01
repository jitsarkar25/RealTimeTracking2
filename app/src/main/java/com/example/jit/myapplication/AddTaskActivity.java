package com.example.jit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTaskActivity extends AppCompatActivity {

    EditText taskLocation,taskDetails;
    String gotlat,gotlon;
    Button saveTask,deleteTask;
    private DatabaseReference databaseReference;
    private int random;
    Tasks tasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
         tasks =(Tasks) getIntent().getSerializableExtra("tasks");
        taskLocation = (EditText) findViewById(R.id.etTaskLocation);
        taskDetails = (EditText) findViewById(R.id.etTaskDetails);
        saveTask = (Button) findViewById(R.id.bSaveTask);
        deleteTask = (Button) findViewById(R.id.bDeleteTask);
        if(tasks == null) {
            Log.d("TasksNull", "null");
            random = (int)(Math.random()*999999);
        }
        else
        {
            deleteTask.setVisibility(View.VISIBLE);
            taskLocation.setText(tasks.address);
            taskDetails.setText(tasks.details);
            random = Integer.parseInt(tasks.id);
            gotlat=tasks.latitude;
            gotlon=tasks.longitude;
        }

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String details,loc;
                details=taskDetails.getText().toString();
                loc=taskLocation.getText().toString();
                if(details.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter Task Details", Toast.LENGTH_SHORT).show();
                }
                else if(loc.equals("")){
                    Toast.makeText(getApplicationContext(),"Enter Task Location",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                    String userid = sharedPreferences.getString("serverid", "");

                    Tasks tasks = new Tasks(random+"",details,gotlat,loc,gotlon);
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Tasks").child(userid).child(random+"");
                    databaseReference.setValue(tasks);
                    Toast.makeText(getApplicationContext(),"Task Saved",Toast.LENGTH_SHORT).show();
                    stopService(new Intent(getApplicationContext(), TaskService.class));
                    startService(new Intent(getApplicationContext(), TaskService.class));
                    finish();
                   /* Toast.makeText(getApplicationContext(),details,Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),loc,Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),gotlat+" "+gotlon,Toast.LENGTH_SHORT).show();*/

                }

            }
        });
        taskLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetLocationTaskActivity.class);
                if(tasks!=null)
                intent.putExtra("tasks",tasks);
                startActivityForResult(intent, 5);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==5)
        {
            if (resultCode== RESULT_OK) {
                taskLocation.setText(data.getStringExtra("address"));
                gotlat= data.getStringExtra("lat");
                gotlon= data.getStringExtra("lon");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void deletetask(View v)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("serverid", "");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tasks").child(userid).child(random+"");
        databaseReference.removeValue();
        Toast.makeText(getApplicationContext(),"Task Deleted",Toast.LENGTH_SHORT).show();
        stopService(new Intent(getApplicationContext(), TaskService.class));
        startService(new Intent(getApplicationContext(), TaskService.class));
        finish();
    }
}
