package com.example.jit.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Jit on 11-04-2017.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {


    private String TAG = "com.example.jit.myapplication";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        SharedPreferences sharedPreferences = getSharedPreferences("firebasetoken", Context.MODE_PRIVATE);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",refreshedToken);
        editor.commit();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }
}
