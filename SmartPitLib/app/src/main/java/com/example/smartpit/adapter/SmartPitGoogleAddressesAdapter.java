package com.example.smartpit.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.smartpit.model.SmartPitGoogleAddress;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by piotr on 06.04.14.
 *
 * Addapter with google addresses api support.
 * getItem(int index) method can be used inside AdapterView.OnItemClickListener class
 * to get name and location of selected address.
 *
 */
public class SmartPitGoogleAddressesAdapter  extends ArrayAdapter<String> implements
        Filterable {

    private String TAG = "AdapterDeliveryAddress";
    private ArrayList<SmartPitGoogleAddress> list = new ArrayList<SmartPitGoogleAddress>();
    ArrayList<SmartPitGoogleAddress> res;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            list.removeAll(list);
            list.addAll(res);

            SmartPitGoogleAddressesAdapter.this.notifyDataSetChanged();
        }
    };

    public SmartPitGoogleAddressesAdapter(Context context) {
        super(context, android.R.layout.simple_expandable_list_item_1);
        // TODO Auto-generated constructor stub

    }

    public int getCount() {
        return list.size();
    }

    public String getItem(int index) {
        return list.get(index).getName();
    }

    public SmartPitGoogleAddress getPoint(int index)
    {
        return list.get(index);
    }



    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
        return new Filter() {

            @Override
            protected FilterResults performFiltering(final CharSequence arg0) {
                // TODO Auto-generated method stub

                String entry = arg0.toString().replace(" ", "%20");

                new FilterAddresses(entry, handler).start();

                FilterResults result = new FilterResults();
                result.values = list;
                result.count = list.size();

                return result;
            }

            @Override
            protected void publishResults(CharSequence arg0, FilterResults arg1) {

            }
        };
    }


    class FilterAddresses extends Thread {

        private String TAG = FilterAddresses.class.getName();

        private URL url;
        private HttpURLConnection httpConn;
        private BufferedReader br;
       ;
        private String arg0;
        private Handler handler;

        public FilterAddresses( String arg0, Handler handler) {
            this.arg0 = arg0;
            this.handler = handler;
        }



        @Override
        public void run() {


            try {
                res = new ArrayList<SmartPitGoogleAddress>();

                url = new URL(
                        "http://maps.googleapis.com/maps/api/geocode/json?address="
                                + arg0.toString().trim() + "&sensor=false"
                );
                httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                br = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                String line;
                StringBuilder out = new StringBuilder();
                while ((line = br.readLine()) != null) {

                    out.append(line);

                }


                JSONObject json = new JSONObject(out.toString());
                if (json.getString("status").equals("OK")) {

                    JSONArray array = json.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject o = array.getJSONObject(i);

                       res.add(SmartPitGoogleAddress.valueOf(o));

                    }
                    //
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }

            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}