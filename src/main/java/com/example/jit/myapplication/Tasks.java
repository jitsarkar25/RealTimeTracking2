package com.example.jit.myapplication;

import java.io.Serializable;

/**
 * Created by Viper PC on 5/2/2017.
 */
public class Tasks implements Serializable {
   public String id,details,address,latitude,longitude;

    public Tasks(String id, String details, String latitude, String address, String longitude) {
        this.id = id;
        this.details = details;
        this.latitude = latitude;
        this.address = address;
        this.longitude = longitude;
    }
    public Tasks(){}
}
