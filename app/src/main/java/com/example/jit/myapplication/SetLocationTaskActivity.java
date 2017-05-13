package com.example.jit.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class SetLocationTaskActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    EditText searchbox;
    Button submitloc,searcbutton;
    int search=0;
    String latret,lonret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location_task);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        searchbox=(EditText)findViewById(R.id.etsearchbox);
        searcbutton=(Button)findViewById(R.id.searchbtn);
        submitloc=(Button)findViewById(R.id.bLocSubmit);
        searcbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchloc();
            }
        });
        submitloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search == 1) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("address", searchbox.getText().toString());
                    returnIntent.putExtra("lat", latret);
                    returnIntent.putExtra("lon", lonret);

                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Search your address First", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void searchloc()
    {
        String location = searchbox.getText().toString();
        List<Address> addressList = null;
        if ( !location.equals("")) {
            search=1;
            mMap.clear();
            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            latret=String.valueOf(address.getLatitude());
            lonret=String.valueOf(address.getLongitude());
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location).draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

            Log.d("LatLng", latLng.longitude + "   " + latLng.latitude);
        }
        else
        {
            search=0;
            Toast.makeText(getApplicationContext(),"Enter a Address",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
