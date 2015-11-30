package pl.gryko.smartpitlib.route;

import android.net.Uri;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.gryko.smartpitlib.SmartPitActivity;
import pl.gryko.smartpitlib.widget.Log;

/**
 * Created by piotr on 20.03.15.
 *
 * Class that wraps google direction api. Allaws ask google for route between two points and later draw in on map.
 */
public class SmartMapRouteManager {

    private String TAG = pl.gryko.smartpitlib.route.SmartMapRouteManager.class.getName();


    /**
     * Google direction api returns json with structure routes-legs-steps. Each step response contains encoded polyline data, html instructions, dirsance and ruration.
     * These implementation allows easly interact with each step line drawed on map. In example show different instructions for different step.
     *
     */
    public class Step {
        public String distance;
        public String duration;
        public String instructions;
        public PolylineOptions polylineOptions;
        public Polyline polyline;

        /**
         * draws line on google map, sets local variable and return value;
         * @param map GoogleMap to draw line
         * @return Polyline drawed on map
         */
        public Polyline drawLine(GoogleMap map) {
            polyline = map.addPolyline(polylineOptions);
            return polyline;
        }

        /**
         * parse json data into Step object
         * @param data JSONObject data
         * @return Step object.
         */
        public Step init(JSONObject data) {
            JSONObject dist = null;
            try {
                dist = data.has("distance") ? data.getJSONObject("distance") : new JSONObject();

                distance = dist.has("text") ? dist.getString("text") : "";
                JSONObject dur = data.has("duration") ? data.getJSONObject("duration") : new JSONObject();
                duration = dur.has("text") ? dur.getString("text") : "";
                instructions = data.has("html_instructions") ? data.getString("html_instructions") : "";

                JSONObject poliline = data.has("polyline") ? data.getJSONObject("polyline") : new JSONObject();
                String line = poliline.has("points") ? poliline.getString("points") : "";

                polylineOptions = new PolylineOptions();
                polylineOptions.width(4);
                polylineOptions.addAll(decodePoly(line));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return this;
        }
    }

    /**
     * higher part of google response. Each leg contains own steps, duration, start and end adres, distance.
     */
    public class Leg {
        public String distance;
        public String duration;
        public String start_address;
        public String end_address;
        public ArrayList<Step> steps;

        public Leg init(JSONObject data) {
            JSONObject dist = null;
            try {
                dist = data.has("distance") ? data.getJSONObject("distance") : new JSONObject();

                distance = dist.has("text") ? dist.getString("text") : "";
                JSONObject dur = data.has("duration") ? data.getJSONObject("duration") : new JSONObject();
                duration = dur.has("text") ? dur.getString("text") : "";
                start_address = data.has("start_address") ? data.getString("start_address") : "";
                end_address = data.has("end_address") ? data.getString("end_address") : "";

                JSONArray parts = data.has("steps") ? data.getJSONArray("steps") : new JSONArray();

                steps = new ArrayList<>();
                for (int i = 0; i < parts.length(); i++) {
                    steps.add(new Step().init(parts.getJSONObject(i)));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return this;
        }
    }


    /**
     * Interface to be invoked after successful data feching
     */
    public interface RouteListener {
        /**
         *
         * @param routes ArrayList of Legs to be draw on map
         */
        public void success(ArrayList<Leg> routes);

        public void failure();
    }


    private void getRoad(Response.Listener listener, Response.ErrorListener errorListener, String request) {
        Log.d(TAG, request);

        StringRequest jr = new StringRequest(Request.Method.GET, request,
                listener, errorListener);
        jr.setRetryPolicy(new DefaultRetryPolicy(
                10 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rq = SmartPitActivity.getRequestQueue();

        rq.add(jr);
        rq.start();
    }


    private String buildRequest(LatLng start, LatLng end, String api_key) {
        String request = "https://maps.googleapis.com/maps/api/directions/json?";
        ArrayList<String> waypoints = new ArrayList<String>();
        String origin = "";
        String destination = "";
        String key = "&key=" + api_key + "&language=pl";

        origin = "origin=" + Double.toString(start.latitude) + "," + Double.toString(start.longitude);


        destination = "&destination=" + Double.toString(end.latitude) + "," + Double.toString(end.longitude);


        request += origin;

        request += destination;
        request += key;
        String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
        String urlEncoded = Uri.encode(request, ALLOWED_URI_CHARS);
        return urlEncoded;
    }

    private ArrayList<LatLng> decodePoly(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            Log.d(TAG, "latitude " + lat / 1E5 + " longitude " + lng / 1E5);
            LatLng p = new LatLng(((double) lat / 1E5),
                    ((double) lng / 1E5));
            poly.add(p);

        }


        return poly;
    }


    /**
     * main method. Hets start and end point coords, google api_key and route listener.
     * @param start LatLng startpoint
     * @param end LatLng endpoint
     * @param api_key String google api key
     * @param listener listener
     */
    public void showRoute(LatLng start, LatLng end, String api_key, final RouteListener listener) {

        getRoad(new Response.Listener() {
            @Override
            public void onResponse(Object o) {
                Log.d(TAG, o.toString());

                ArrayList<String> lines = new ArrayList<String>();
                ArrayList<Leg> output = new ArrayList<Leg>();
                try {


                    JSONObject roads = new JSONObject(o.toString());
                    JSONArray routes = roads.getJSONArray("routes");

                    for (int k = 0; k < routes.length(); k++) {
                        PolylineOptions po = new PolylineOptions().geodesic(true);


                        JSONObject route = routes.getJSONObject(k);
                        JSONArray legs = route.getJSONArray("legs");
                        for (int j = 0; j < legs.length(); j++) {

                            output.add(new Leg().init(legs.getJSONObject(j)));

                        }
                    }
                    listener.success(output);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, volleyError.toString());
            }
        }, buildRequest(start, end, api_key));

    }

}
