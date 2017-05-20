package com.example.jit.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class TaskDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Tasks tasks= (Tasks)getIntent().getSerializableExtra("task");
        if(tasks!=null)
        {
            Toast.makeText(getApplicationContext(),tasks.details,Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"null",Toast.LENGTH_SHORT).show();
        }
    }
}
