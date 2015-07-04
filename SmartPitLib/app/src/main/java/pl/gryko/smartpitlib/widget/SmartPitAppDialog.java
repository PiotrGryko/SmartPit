package pl.gryko.smartpitlib.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import pl.gryko.smartpitlib.R;

/**
 * Created by piotr on 02.04.14.
 */


public class SmartPitAppDialog {


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static AlertDialog getAlertDialog(int theme, Context context) {
        AlertDialog alert = null;

        if (Build.VERSION.SDK_INT > 11)
            alert = new AlertDialog.Builder(context, theme).create();
        else
            alert = new AlertDialog.Builder(context).create();


        return alert;

    }

    public static AlertDialog getInfoDialog(int theme,final Context context, String message) {
        final AlertDialog alert = getAlertDialog(theme, context);
        alert.setMessage(message);
        alert.setButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        return alert;
    }

    public static void showExitDialog(int theme,final Activity context, String message, String confirm, String cancel) {
        final AlertDialog alert = getAlertDialog(theme, context);

        alert.setMessage(message);
        alert.setButton(confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.finish();
                alert.dismiss();
            }
        });

        alert.setButton2(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                alert.dismiss();
            }
        });
        alert.show();
    }

    public static AlertDialog getLoadingDialog(int theme, Context context, String message) {
        final AlertDialog alert = getAlertDialog(theme, context);
        alert.setMessage(message);

        View v = LayoutInflater.from(context).inflate(R.layout.smart_pit_loading_dialog,null);
        alert.setView(v);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        return alert;
    }

    public static DatePickerDialog getDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener listener) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(context, listener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        return dialog;
    }


}
