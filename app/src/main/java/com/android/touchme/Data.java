package com.android.touchme;

import android.app.Activity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by arnold on 13/5/16.
 */
public class Data {

    private Activity activity;

    public Data() {
    }

    public Data(Activity activity) {
        this.activity = activity;
    }

    public Map<Integer, String> getTouchEvents() {

        Map<Integer, String> data = new LinkedHashMap<>();

        data.put(Constants.DOUBLE_TAP, activity.getResources().getString(R.string.double_tap));
        data.put(Constants.SWIPE_LEFT, activity.getResources().getString(R.string.swipe_left));
        data.put(Constants.SINGLE_TAP, activity.getResources().getString(R.string.single_tap));
        data.put(Constants.SWIPE_RIGHT, activity.getResources().getString(R.string.swipe_right));
        data.put(Constants.ON_LONG_PRESS, activity.getResources().getString(R.string.on_long_press));
        data.put(Constants.SCROLL_DOWN, activity.getResources().getString(R.string.scroll_down));
        data.put(Constants.WAIT, activity.getResources().getString(R.string.wait));
        data.put(Constants.SHAKE_PHONE, activity.getResources().getString(R.string.shake_me));
        data.put(Constants.SCROLL_UP, activity.getResources().getString(R.string.scroll_up));

        return data;
    }
}
