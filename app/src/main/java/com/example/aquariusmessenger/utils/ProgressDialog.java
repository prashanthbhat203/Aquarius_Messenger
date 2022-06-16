package com.example.aquariusmessenger.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.aquariusmessenger.R;

public class ProgressDialog {

    private AlertDialog.Builder alertDialog;
    private AlertDialog dialog;
    private final Activity activity;

    public ProgressDialog(Activity activity) {
        this.activity = activity;
    }

    public void startDialog() {
        alertDialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        alertDialog.setView(inflater.inflate(R.layout.progress_dialog_layout, null));

        alertDialog.setCancelable(false);
        dialog = alertDialog.create();
        dialog.show();

    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}
