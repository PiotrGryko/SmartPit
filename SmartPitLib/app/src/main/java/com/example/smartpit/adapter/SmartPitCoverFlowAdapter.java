package com.example.smartpit.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;


import com.example.smartpit.widget.SmartImageView;
import com.example.smartpit.widget.SmartPitAppHelper;
import com.example.smartpit.widget.SmartPitCoverFlow;

import org.apache.http.impl.client.RequestWrapper;

import java.util.ArrayList;

public class SmartPitCoverFlowAdapter extends BaseAdapter {


    private String TAG = "ImagesAdapter";
    private int HALF_MAX_VALUE = Integer.MAX_VALUE / 2;
    private int MIDDLE;
    private ArrayList<String> list;
    private Context context;
    private int width;
    private int height;

    private Drawable d;
    private int padding;


    private SmartPitCoverFlow.OnCoverFlowItemClickListener listener;


    public SmartPitCoverFlowAdapter(Context context, SmartPitCoverFlow.OnCoverFlowItemClickListener listener, ArrayList<String> list, int width, int height) {

        this.context = context;
        this.list = list;
        this.width = width;
        this.height = height;
        this.listener = listener;

    }


    public int getMiddle() {
        return MIDDLE;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position % list.size());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class ViewHolder {
        public SmartImageView image;
    }

    public void setImageBackground(Drawable d) {
        this.d = d;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    @Override
    public View getView(final int position, View image, ViewGroup parent) {


        ViewHolder holder = null;
        if (image == null) {
            holder = new ViewHolder();
            holder.image = new SmartImageView(context);
            holder.image.setPadding(padding, padding, padding, padding);
            if (d != null)
                holder.image.setBackgroundDrawable(d);
                holder.image.setTag(holder);
        } else {
            holder = (ViewHolder) image.getTag();

        }
        SmartPitAppHelper.getInstance().setImage(context, holder.image, list.get(position), 200,
                200);

        final int p = position;


        if (listener != null) {
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });

        }

        holder.image.setClickable(false);
        holder.image.setFocusableInTouchMode(false);


        return holder.image;

    }


}
