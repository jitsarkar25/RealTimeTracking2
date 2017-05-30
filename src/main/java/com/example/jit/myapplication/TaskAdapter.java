package com.example.jit.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by Viper PC on 5/2/2017.
 */
public class TaskAdapter  extends ArrayAdapter<Tasks> {
    Context con;
    private DatabaseReference databaseReference;
    public TaskAdapter(Context context, List<Tasks> tasks) {
        super(context,R.layout.taskcontent, tasks);
        con=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View view=layoutInflater.inflate(R.layout.taskcontent, parent, false);
        final Tasks tasks=getItem(position);

        TextView tvTaskDet = (TextView) view.findViewById(R.id.tvSavedTaskDetails);
        TextView tvTaskLoc = (TextView) view.findViewById(R.id.tvSavedTaskLOcation);
        Button bEdit = (Button) view.findViewById(R.id.bEditTask);
        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con,AddTaskActivity.class);
                intent.putExtra("tasks",tasks);
                con.startActivity(intent);
            }
        });

        tvTaskDet.setText(tasks.details);
        tvTaskLoc.setText(tasks.address);


        return  view;
    }
}
