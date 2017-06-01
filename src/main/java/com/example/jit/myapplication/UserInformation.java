package com.example.jit.myapplication;

import java.io.Serializable;

/**
 * Created by Jit on 11-04-2017.
 */
public class UserInformation implements Serializable{
    public String name;
    public String phone;
    public String id;
    private String key;
    private String dp;

    public UserInformation(String name, String phone,String id) {

        this.name = name;
        this.phone = phone;
        this.id = id;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public UserInformation(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
