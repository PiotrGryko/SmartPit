package pl.gryko.smartpitlib.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;



import org.apache.http.impl.client.RequestWrapper;

import java.util.ArrayList;

import pl.gryko.smartpitlib.widget.SmartImageView;
import pl.gryko.smartpitlib.widget.SmartPitAppHelper;
import pl.gryko.smartpitlib.widget.SmartPitCoverFlow;

/**
 *
 * Adapter that can be setted to SmartPitCoverFlow view. Views are fullscreen images loaded from given urls.
 * Can be used as cover flow gallery.
 *
 */

public class SmartPitCoverFlowAdapter extends BaseAdapter {


    private String TAG = SmartPitCoverFlowAdapter.class.getName();

    private ArrayList<String> list;
    private Context context;
    private int width;
    private int height;

    private Drawable d;
    private int padding;


    private SmartPitCoverFlow.OnCoverFlowItemClickListener listener;


    /**
     * Constructor
     * @param context Context
     * @param listener SmartPitCoverFlow.OnCoverFlowItemClickListener listener that will be invoked on cover flow element click
     * @param urls ArrayList<String> list of urls to load in gallery
     * @param width int max width to scale loaded image, 0 if image shouldn`t be scaled
     * @param height int max height to scale loaded image, 0 if image shouldn`t be scaled
     */
    public SmartPitCoverFlowAdapter(Context context, SmartPitCoverFlow.OnCoverFlowItemClickListener listener, ArrayList<String> urls, int width, int height) {

        this.context = context;
        this.list = urls;
        this.width = width;
        this.height = height;
        this.listener = listener;

    }


    /**
     *
     * @return adapter items count
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Returns element at giver position
     * @param position int index to return positon at
     * @return object at given position
     */
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

    /**
     * sets drawable as image background
     * @param d Drawable to be setted as background
     */
    public void setImageBackground(Drawable d) {
        this.d = d;
    }

    /**
     * sets padding to gallery image
     * @param padding int padding to be set
     */
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
        SmartPitAppHelper.getInstance(context).setImage(holder.image, list.get(position), width,
                height);

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
