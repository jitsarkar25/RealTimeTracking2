package com.example.jit.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.spec.ECField;
import java.util.ArrayList;

/**
 * Created by Viper PC on 5/6/2017.
 */
public class TaskService extends Service implements LocationListener {

    DatabaseReference databaseReference;
    private ArrayList<Tasks> taskList;
    LocationManager locationManager;
    String provider;
    private ArrayList<Tasks> taskAlerted;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Working", Toast.LENGTH_SHORT).show();
        taskList = new ArrayList<>();
        taskAlerted = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

       // provider = locationManager.getBestProvider(new Criteria(), false);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {

            Log.i("Location Info", "Location achieved!");

        } else {

            Log.i("Location Info", "No location :(");

        }

        fetchTasks();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void fetchTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("serverid", "");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tasks").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final String key = child.getKey();
                    Tasks myTasks = child.getValue(Tasks.class);
                   // Log.d("TaskService", myTasks.details);
                    if (!taskList.contains(myTasks))
                        taskList.add(myTasks);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

        for(Tasks tasks:taskList)
        {
            Location location1 = new Location("");
            location1.setLatitude(Double.parseDouble(tasks.latitude));
            location1.setLongitude(Double.parseDouble(tasks.longitude));
            if(location.distanceTo(location1)<=500 && !taskAlerted.contains(tasks) )
            {
               // Toast.makeText(getApplicationContext(),"You have a task "+tasks.details,Toast.LENGTH_SHORT).show();
                taskAlerted.add(tasks);

                Intent intent= new Intent(this,TaskDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("task",tasks);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setContentTitle("Task Alert");
                notificationBuilder.setContentText("You Have a task Here "+tasks.details);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setContentIntent(pendingIntent);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notificationBuilder.setSound(alarmSound);
                notificationBuilder.setLights(Color.BLUE, 500, 500);
                long[] pattern = {500,500,500,500,500,500,500,500,500};
                notificationBuilder.setVibrate(pattern);
                notificationBuilder.setStyle(new NotificationCompat.InboxStyle());
                NotificationManager notificationManager = (NotificationManager) getSystemService((Context.NOTIFICATION_SERVICE));
                notificationManager.notify(0,notificationBuilder.build());

            }
        }
       // Toast.makeText(getApplicationContext(),location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}