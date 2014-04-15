package com.example.smartpit.model;

import com.example.smartpit.widget.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piotr on 06.04.14.
 */
public class SmartPitGoogleAddress {

    private String name;
    private String lat;
    private String lon;

    private String country;
    private String voivodeship;
    private String city;
    private String street;
    private String streetNumber;
    private String sublocality;


    public SmartPitGoogleAddress(String name, String lat, String lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;

    }

    public String getSublocality()
    {
        return sublocality;
    }
    public void setSublocality(String sublocality)
    {
        this.sublocality=sublocality;
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


    public void setCountry(String country) {
        this.country = country;
    }

    public String getCoutry() {
        return country;
    }

    public void setVoivodeship(String voivodeship) {
        this.voivodeship = voivodeship;
    }

    public String getVoivodeship() {
        return voivodeship;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetNumber() {
        return streetNumber;
    }


    public static SmartPitGoogleAddress valueOf(JSONObject o) {

        String address = "";
        String lat = "";
        String lon = "";

        String country = "";
        String voivodeship = "";
        String city = "";
        String sublocality = "";
        String street = "";
        String streetNumber = "";


        try {
        if(o.has("address_components"))
        {
            JSONArray components = o.getJSONArray("address_components");
            for(int i=0;i<components.length();i++)
            {
                JSONObject component = components.getJSONObject(i);

                String componentName = component.has("long_name")?component.getString("long_name"):"";


                if(component.has("types"))
                {
                    String componentType = component.getJSONArray("types").getString(0);

                    if(componentType.equals("street_number"))
                        streetNumber=componentName;
                    else if(componentType.equals("route"))
                        street=componentName;
                    else if(componentType.equals("sublocality"))
                        sublocality=componentName;
                    else if(componentType.equals("locality"))
                        city=componentName;
                    else if(componentType.equals("administrative_area_level_1"))
                        voivodeship=componentName;
                    else if(componentType.equals("country"))
                        country=componentName;

                }
            }

        }



            address = o.has("formatted_address") ? o
                    .getString("formatted_address") : "";


            if (o.has("geometry")) {
                JSONObject g = o.getJSONObject("geometry");
                JSONObject l = g.getJSONObject("location");
                lat = l.getString("lat");
                lon = l.getString("lng");


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SmartPitGoogleAddress adr = new SmartPitGoogleAddress(address, lat, lon);
        adr.setCity(city);
        adr.setCountry(country);
        adr.setStreet(street);
        adr.setStreetNumber(streetNumber);
        adr.setSublocality(sublocality);
        adr.setVoivodeship(voivodeship);


        return adr;

    }

}
