package com.example.aquariusmessenger.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;



public class OfflineStore {

    public static final String PREF_GENERAL = "StoreName";

    public static void setUsername(@NonNull Context context, String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_GENERAL, Context.MODE_PRIVATE).edit();
        editor.putString("Username", name);
        editor.apply();
    }

    public static String getUsername(@NonNull Context context) {
        return context.getSharedPreferences(PREF_GENERAL, Context.MODE_PRIVATE)
                .getString("Username","AQUser");
    }
}
