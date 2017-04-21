package com.example.jit.myapplication;

/**
 * Created by Jit on 11-04-2017.
 */
public class UserInformation {
    public String name;
    public String phone;
    public String id;
    private String key;

    public UserInformation(String name, String phone,String id) {

        this.name = name;
        this.phone = phone;
        this.id = id;
    }

    public UserInformation(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
