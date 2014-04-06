package com.example.smartpit.model;

/**
 * Created by piotr on 06.04.14.
 */
public class SmartPitGoogleAddress {

    private String name;
    private String lat;
    private String lon;

    public SmartPitGoogleAddress(String name, String lat, String lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;

    }

    public String getName() {
        return name;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }


}
