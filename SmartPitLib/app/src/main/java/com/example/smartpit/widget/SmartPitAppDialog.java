package com.example.smartpit.widget;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;

/**
 * Created by piotr on 02.04.14.
 */
public class SmartPitAppDialog {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static AlertDialog getAlertDialog(int theme, Context context, String text)
    {
        AlertDialog alert = null;

        if(Build.VERSION.SDK_INT>11)
        alert = new AlertDialog.Builder(context, theme).setTitle(text).create();
        else
            alert = new AlertDialog.Builder(context, theme).setTitle(text).create();


        return  alert;

    }



}
