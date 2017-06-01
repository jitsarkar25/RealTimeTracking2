package com.example.jit.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "com.example.jit.myapplication";
    private GoogleApiClient mGoogleApiClient;
    TextView tvRegister,anonymous;
    private static final int RC_SIGN_IN = 9001;
    EditText etEmail,etPassword;
    CallbackManager callbackManager;
    ProgressDialog progressDialog;
    ImageView imageView;
    Button signIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        progressDialog=new ProgressDialog(MainActivity.this);
        tvRegister=(TextView)findViewById(R.id.tvRegister);
        etEmail = (EditText)findViewById(R.id.etEmailLogin);
        etPassword = (EditText)findViewById(R.id.etPasswordLogin);
        signIn = (Button) findViewById(R.id.bSignIn);
        anonymous = (TextView) findViewById(R.id.tvAnonymous);
        anonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Logging In");
                progressDialog.show();
                signInAnonymously();
            }
        });
        signout();


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInUser();
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("islogin",false))
        {
            Log.d("redirecting",sharedPreferences.getBoolean("islogin",false)+"");
            startActivity(new Intent(getApplicationContext(),UserActivity.class));
            finish();
        }

        callbackManager=CallbackManager.Factory.create();
        final LoginButton loginButton=(LoginButton)findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email" ));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Logging In");
                progressDialog.show();
                Log.d(TAG, "Success:" + loginResult);
                SharedPreferences sharedPreferences = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("islogin", true);
                editor.commit();
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("ffgghhjj", "onAuthStateChanged:signed_in:" + user.getUid());
                    saveToken(user);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d("fail","Failed");
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("event","buttonclicked");
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        Button fbButton=(Button)findViewById(R.id.fbButton);
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();

            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("details");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("haschild",dataSnapshot.hasChild(user.getUid())+"");
                                    progressDialog.dismiss();
                                    if(dataSnapshot.hasChild(user.getUid()))
                                        startActivity(new Intent(getApplicationContext(), UserActivity.class));
                                    else
                                        startActivity(new Intent(getApplicationContext(),UserDetailsActivity.class));
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("jitGoogle","activity result "+requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d("jitGoogle","activity result");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d("jitGoogle","success");
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Logging In");
                progressDialog.show();
                // Google Sign In was successful, authenticate with Firebase
                SharedPreferences sharedPreferences = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("islogin", true);
                editor.commit();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d("jitGoogle","fail");
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
            else
            {
                callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    public void signout() {
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected())
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        Log.d("logout", "Logout");
                        // [END_EXCLUDE]
                    }
                });
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("ffgghhjj", "firebaseAuthWithGoogle:" + acct.getDisplayName());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ffgghhjj", "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("details");

                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("haschild",dataSnapshot.hasChild(user.getUid())+"");
                                    progressDialog.dismiss();

                                    if(dataSnapshot.hasChild(user.getUid()))
                                            startActivity(new Intent(getApplicationContext(), UserActivity.class));
                                    else
                                            startActivity(new Intent(getApplicationContext(),UserDetailsActivity.class));
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Log.d("ffgghhjj", "user:success"+user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("ffgghhjj", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
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

    public void signInUser(){

        String email =etEmail.getText().toString();
        String password =etPassword.getText().toString();
        if(email.equals("") || password.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Please enter the details",Toast.LENGTH_SHORT).show();
        }
else {
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Logging In");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(MainActivity.this, "Invalid Credentials",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences sharedPreferences = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("islogin", true);
                                editor.commit();
                                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                                finish();
                            }

                            // ...
                        }
                    });
        }
    }
    public void saveToken(FirebaseUser user){
        Log.d("State","here");
        SharedPreferences sharedPreferences = getSharedPreferences("firebasetoken", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        Log.d("State","here");
        Log.d("token",token);
        if(!token.equals("")){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("token").child(user.getUid()).setValue(token);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        signout();
}
    private void signInAnonymously() {
        // [START signin_anonymously]
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("details").child(user.getUid());
                            int random = (int) (Math.random() * 99999999);
                            UserInformation userInformation=new UserInformation("Anonymous","",random+"");
                            databaseReference.setValue(userInformation);
                            databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(String.valueOf(random));
                            databaseReference.setValue(user.getUid());
                            SharedPreferences sharedPreferences = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("islogin", true);
                                    editor.putBoolean("isanonymous", true);
                                    editor.commit();
                                    progressDialog.dismiss();

                            startActivity(new Intent(getApplicationContext(),UserActivity.class));
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
        // [END signin_anonymously]
    }

    public void forgotpassword(View v)
    {
        startActivity(new Intent(getApplicationContext(),ForgotPasswordActivity.class));
    }

}
