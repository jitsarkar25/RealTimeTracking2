package com.example.jit.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDetailsActivity extends AppCompatActivity {

    EditText etName,etPhone;
    private DatabaseReference databaseReference;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        etName =(EditText)findViewById(R.id.etNameRegister);
        etPhone =(EditText)findViewById(R.id.etPhoneRegister);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("here", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("here", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    public void submitdetails(View v)
    {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        if(name.equals("") || phone.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Enter the details",Toast.LENGTH_SHORT).show();
        }
        else if(phone.length()!=10){
                etPhone.setError("Must be of 10 digits");
    }
        else {
            int random = (int) (Math.random() * 99999999);

            databaseReference = FirebaseDatabase.getInstance().getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference.child("users").child(String.valueOf(random)).setValue(user.getUid());
            UserInformation userInformation = new UserInformation(name, phone, String.valueOf(random));
            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference.child("details").child(user.getUid()).setValue(userInformation);
            sendsms(phone);
            // startActivity(new Intent(getApplicationContext(), UserActivity.class));

        }
    }
    public void sendsms(String phone) {

        SharedPreferences sharedPreferences = getSharedPreferences("otpcheck", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", phone);
        editor.commit();

        String url = "https://control.msg91.com/api/sendotp.php?authkey=151462A3CrVZc2590d79f6&mobile=" + phone + "&sender=trckit";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Please Verify OTP Sent to your mobile", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), VerifyOtpActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
