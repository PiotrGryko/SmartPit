package pl.gryko.smartpitlib.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import pl.gryko.smartpitlib.model.SmartPitGoogleAddress;

/**
 *
 * Adapter that can be set to AutoCompleteTextView or be used inside EditText TextWacher. Class wraps google geocode api and provide
 * google address autocomplete filtering.
 *
 * minimal sample inside SmartPitFragment:
 *
 * public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
 * {
 *     SmartPitGoogleAdressesAdapter adapter=  new SmartPitGoogleAddressesAdapter(this.getActivity(),android.R.layout.simple_list_item1);
 *
 *     View v = inflater.inflater(R.layout.sample)
 *
 *     AutoCompleteTextView  actv = (AutoCompleteTextView)v.findViewById(R.id.autocomplete)
 *
 *     actv.setAdapter(adapter)
 *
 *     EditText et = (EditText)v.findViewById(R.id.et);
 *
 *     et.setOnTextChangedListener(
 *     new TextWacher()
 *     {
 *         afterTextChanged(char arg0)
 *         {
 *             if(!arg.trim().equals(""))
 *             adapter.getFilter().performFiltering(arg0);
 *         }
 *     }
 *     );
 * }
 *
 */


public class SmartPitGoogleAddressesAdapter extends ArrayAdapter<String> implements
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

    /**
     * Constructor
     * @param context Context
     * @param res row layout
     */
    public SmartPitGoogleAddressesAdapter(Context context, int res) {
        super(context, res);
        // TODO Auto-generated constructor stub

    }

    /**
     * return count of adapter elements
     * @return int cout of adapter elements
     */
    public int getCount() {
        return list.size();
    }

    /**
     * returns item name at given position
     * @param index
     * @return String returns google formatted_address
     */
    public String getItem(int index) {
        return list.get(index).getName();
    }

    /**
     * return SmartPitGoogleAddress model object at given position
     * @param index int index of element to return
     * @return
     */
    public SmartPitGoogleAddress getPoint(int index) {
        return list.get(index);
    }


    /**
     * returns filter
     * @return Filter
     */
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



    private class FilterAddresses extends Thread {

        private String TAG = FilterAddresses.class.getName();

        private URL url;
        private HttpURLConnection httpConn;
        private BufferedReader br;
        ;
        private String arg0;
        private Handler handler;


        public FilterAddresses(String arg0, Handler handler) {
            this.arg0 = arg0;
            this.handler = handler;
        }


        @Override
        public void run() {


            try {
                res = new ArrayList<SmartPitGoogleAddress>();

                url = new URL(
                        "http://maps.googleapis.com/maps/api/geocode/json?address="
                                + arg0.toString().trim() + "&bounds=52.998256,22.900572|53.212612,23.309813&sensor=false"
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

                        //res.add(SmartPitGoogleAddress.valueOfGoogleJson(o));
                        populateResult(SmartPitGoogleAddress.valueOfGoogleJson(o),res);

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


    private void populateResult(SmartPitGoogleAddress element, ArrayList<SmartPitGoogleAddress> list) {
        list.add(element);

    }
}