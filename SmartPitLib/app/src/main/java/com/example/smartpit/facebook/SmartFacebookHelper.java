package com.example.smartpit.facebook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.example.smartpit.R;
import com.example.smartpit.SmartPitActivity;
import com.example.smartpit.widget.Log;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

/**
 * Created by piotr on 19.05.14.
 */
public class SmartFacebookHelper {

    private static String TAG = SmartFacebookHelper.class.getName();

    public interface OnActivityResultInterface
    {
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data);
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data);

    }

    private static UiLifecycleHelper uiHelper;
    private static SmartPitActivity activity;

    public static void init(SmartPitActivity c, UiLifecycleHelper helper) {
        activity = c;
        uiHelper = helper;
    }

    private static void showInfoDialog(String text) {



        AlertDialog alert = null;
        if (Build.VERSION.SDK_INT > 10)
            alert = new AlertDialog.Builder(activity, AlertDialog.THEME_TRADITIONAL).setTitle(text).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
        else
            alert = new AlertDialog.Builder(activity).setTitle(text).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
        alert.show();
    }
    private static void makeShare(String url,String picture)
    {
        try {

            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(activity)
                    .setLink(url).setPicture(picture)
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } catch (FacebookException f) {
            showInfoDialog(activity.getString(R.string.dialog_facebook_app));
        }
    }


    public static void shareLink(final String url, final String picture) {

        Session.openActiveSession(activity, true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (state.isOpened()) {
                    Log.i(TAG, "Logged in...");
                    makeShare(url,picture);
                } else if (state.isClosed()) {
                    Log.i(TAG, "Logged out...");
                }
            }
        });
    }

}
