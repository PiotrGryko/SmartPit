package com.example.smartpit.widget;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartpit.R;

/**
 * Created by piotr on 02.04.14.
 */


public class SmartPitAppDialog {


    private Context context;
    private LinearLayout titleBase;
    private TextView title;

    private LinearLayout messageBase;
    private TextView message;

    private LinearLayout buttonsBase;
    private Button buttonOne;
    private Button buttonTwo;


    class Builder {

        public void setTitle(String t) {
            title.setText(t);
        }

        public void setTitleBackgroundColor(String color) {
            titleBase.setBackgroundColor(Color.parseColor(color));
        }
        ///  public void setTitleBackgroundDrawable()

    }


    private static SmartPitAppDialog instance;

    public static SmartPitAppDialog getInstance(Context context) {
        if (instance == null)
            instance = new SmartPitAppDialog(context);

        return instance;
    }


    public SmartPitAppDialog(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.smart_pit_dialog, null);
        titleBase = (LinearLayout) v.findViewById(R.id.title_background);
        title = (TextView) v.findViewById(R.id.title);

        messageBase = (LinearLayout) v.findViewById(R.id.message_background);
        message = (TextView) v.findViewById(R.id.message);

        buttonOne = (Button) v.findViewById(R.id.button_one);
        buttonTwo = (Button) v.findViewById(R.id.button_two);

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AlertDialog getAlertDialog(int theme, Context context, String text) {
        AlertDialog alert = null;

        if (Build.VERSION.SDK_INT > 11)
            alert = new AlertDialog.Builder(context, theme).setTitle(text).create();
        else
            alert = new AlertDialog.Builder(context, theme).setTitle(text).create();


        return alert;

    }


}
