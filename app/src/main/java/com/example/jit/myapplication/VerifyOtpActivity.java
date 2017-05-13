package com.example.jit.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyOtpActivity extends AppCompatActivity {

    EditText ot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        ot = (EditText) findViewById(R.id.giveotp);

    }

    public void verifyotp(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences("otpcheck",MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone","");
        String otp =ot.getText().toString();
        String url = "https://control.msg91.com/api/verifyRequestOTP.php?authkey=151462A3CrVZc2590d79f6&mobile="+phone+"&otp="+otp;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("type");
                    if(status.equals("success"))
                    {
                        startActivity(new Intent(getApplicationContext(),UserActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Incorrect OTP ,Resend again",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void resendotp(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences("otpcheck",MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone","");

        String url = "https://control.msg91.com/api/retryotp.php?authkey=151462A3CrVZc2590d79f6&mobile="+phone+"&retrytype=text";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
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
