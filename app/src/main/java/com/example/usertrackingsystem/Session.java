package com.example.usertrackingsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Collections;

public class Session {
    private final SharedPreferences preferences;

    public Session(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public void setUsername(String username){
        preferences.edit().putString("username", username).apply();
    }
    public String getUsername(){
        return preferences.getString("username", "");
    }
}
