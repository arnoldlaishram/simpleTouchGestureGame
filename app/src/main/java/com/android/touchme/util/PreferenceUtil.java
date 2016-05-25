package com.android.touchme.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class PreferenceUtil {

    public static final String HIGH_SCORE = "HIGH_SCORE";
    private SharedPreferences preferences;

    public PreferenceUtil() {
    }

    public PreferenceUtil(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Save methods

    public void save(String key, int value) {
        getEditor().putInt(key, value).apply();
    }

    public void remove(String key) {
        getEditor().remove(key).apply();
    }

    public void clear() {
        getEditor().clear().apply();
    }

    // Read methods
    public int readInt(String key, int defaultValue) {
        try {
            return preferences.getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

} 