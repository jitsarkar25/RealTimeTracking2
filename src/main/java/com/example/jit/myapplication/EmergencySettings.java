package com.example.jit.myapplication;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EmergencySettings extends AppCompatActivity {
    EditText msg, cntct1,cntct2,cntct3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cntct1=(EditText)findViewById(R.id.etcontact1);
        cntct2=(EditText)findViewById(R.id.etcontact2);
        cntct3=(EditText)findViewById(R.id.etcontact3);
        SharedPreferences sharedPreferences = getSharedPreferences("otpcheck",MODE_PRIVATE);
        String s1=sharedPreferences.getString("contact1","");
        String s2=sharedPreferences.getString("contact2","");
        String s3=sharedPreferences.getString("contact3","");
        if(!s1.equals(""))
            cntct1.setText(s1);
        if(!s2.equals(""))
            cntct2.setText(s2);
        if(!s3.equals(""))
            cntct3.setText(s3);
    }
    public void save1(View v)
    {
        String s1=cntct1.getText().toString();
        if(s1.length()!=10)
            cntct1.setError("Must be of 10 digits");
        else {
            SharedPreferences sharedPreferences = getSharedPreferences("otpcheck", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("contact1", s1);
            editor.commit();
            Toast.makeText(getApplicationContext(), "contact 1 saved", Toast.LENGTH_SHORT).show();
        }
    }
    public void save2(View v)
    {
        String s2=cntct2.getText().toString();
        if(s2.length()!=10)
            cntct2.setError("Must be of 10 digits");
        else {
        SharedPreferences sharedPreferences = getSharedPreferences("otpcheck",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("contact2",s2);
        editor.commit();
        Toast.makeText(getApplicationContext(),"contact 2 saved",Toast.LENGTH_SHORT).show();

    }}
    public void save3(View v)
    {

        String s3=cntct3.getText().toString();
        if(s3.length()!=10)
            cntct3.setError("Must be of 10 digits");
        else {
        SharedPreferences sharedPreferences = getSharedPreferences("otpcheck",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("contact3",s3);
        editor.commit();
        Toast.makeText(getApplicationContext(),"contact 3 saved",Toast.LENGTH_SHORT).show();

    }}
}
