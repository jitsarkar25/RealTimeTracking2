package com.example.jit.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    TextView tvSignIn;
    Button register;
    EditText etemail,etpassword,etName,etPhone;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseReference;
    private String TAG = "com.example.jit.myapplication";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        tvSignIn =(TextView)findViewById(R.id.tvSignIn);
        register =(Button)findViewById(R.id.bRegister);
        etemail =(EditText)findViewById(R.id.etEmailRegister);
        etpassword =(EditText)findViewById(R.id.etPasswordRegister);
        etName =(EditText)findViewById(R.id.etNameRegister);
        etPhone =(EditText)findViewById(R.id.etPhoneRegister);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }


    private void registerUser(){
        String email =etemail.getText().toString().trim();
        String password =etpassword.getText().toString().trim();



        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());


                        if (task.isSuccessful()) {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {


                                                Toast.makeText(getApplicationContext(), "Registration Successfull..Please Verify Your Email", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });

                            String name = etName.getText().toString().trim();
                            String phone = etPhone.getText().toString().trim();
                            int random = (int) (Math.random() * 99999999);
                            Log.d(TAG, "here");
                            databaseReference.child("users").child(String.valueOf(random)).setValue(user.getUid());
                            UserInformation userInformation = new UserInformation(name, phone, String.valueOf(random));
                            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            databaseReference.child("details").child(user.getUid()).setValue(userInformation);
                            sendsms(phone);
                           // startActivity(new Intent(getApplicationContext(), UserActivity.class));
                            finish();
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
