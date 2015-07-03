package pl.gryko.smartpitlib.model;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piotr on 06.04.14.
 */
public class SmartPitGoogleAddress {

    private String name;
    private double lat;
    private double lon;

    private String country;
    private String voivodeship;
    private String city;
    private String street;
    private String streetNumber;
    private String sublocality;

    private String id;


    public SmartPitGoogleAddress(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;

    }

    public String getSublocality() {
        return sublocality;
    }

    public void setSublocality(String sublocality) {
        this.sublocality = sublocality;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
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

    public void setId(String id)
    {
        this.id=id;
    }
    public String getId()
    {
        return id;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String toJsonString() {

        return new Gson().toJson(this);
    }


    public static SmartPitGoogleAddress valueOf(String data) {
        return (SmartPitGoogleAddress) new Gson().fromJson(data, SmartPitGoogleAddress.class);
    }


    public static SmartPitGoogleAddress valueOfGoogleJson(JSONObject o) {

        String address = "";
        double lat = 0;
        double lon = 0;

        String country = "";
        String voivodeship = "";
        String city = "";
        String sublocality = "";
        String street = "";

        String id = "";
        String streetNumber = "";


        try {
            if (o.has("address_components")) {
                JSONArray components = o.getJSONArray("address_components");
                for (int i = 0; i < components.length(); i++) {
                    JSONObject component = components.getJSONObject(i);

                    String componentName = component.has("long_name") ? component.getString("long_name") : "";


                    if (component.has("types")) {
                        String componentType = component.getJSONArray("types").getString(0);

                        if (componentType.equals("street_number"))
                            streetNumber = componentName;
                        else if (componentType.equals("route"))
                            street = componentName;
                        else if (componentType.equals("sublocality"))
                            sublocality = componentName;
                        else if (componentType.equals("locality"))
                            city = componentName;
                        else if (componentType.equals("administrative_area_level_1"))
                            voivodeship = componentName;
                        else if (componentType.equals("country"))
                            country = componentName;

                    }
                }

            }

            id = o.has("place_id")?o.getString("place_id"):"";
            address = o.has("formatted_address") ? o
                    .getString("formatted_address") : "";


            if (o.has("geometry")) {
                JSONObject g = o.getJSONObject("geometry");
                JSONObject l = g.getJSONObject("location");
                lat = Double.parseDouble(l.getString("lat"));
                lon = Double.parseDouble(l.getString("lng"));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SmartPitGoogleAddress adr = new SmartPitGoogleAddress(address, lat, lon);
        adr.setId(id);
        adr.setCity(city);
        adr.setCountry(country);
        adr.setStreet(street);
        adr.setStreetNumber(streetNumber);
        adr.setSublocality(sublocality);
        adr.setVoivodeship(voivodeship);


        return adr;

    }

}
