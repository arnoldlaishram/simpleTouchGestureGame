package com.android.touchme;

import android.app.Activity;

import com.android.touchme.model.Question;

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

    public Map<Integer, Question> getAllEvents() {

        Map<Integer, Question> data = new LinkedHashMap<>();

        data.put(Constants.DOUBLE_TAP, new Question(activity.getResources().getString(R.string.double_tap), activity.getResources().getString(R.string.double_tap)));
        data.put(Constants.SWIPE_LEFT, new Question(activity.getResources().getString(R.string.swipe_left), activity.getResources().getString(R.string.swipe_left)));
        data.put(Constants.SINGLE_TAP, new Question(activity.getResources().getString(R.string.single_tap), activity.getResources().getString(R.string.single_tap)));
        data.put(Constants.SWIPE_RIGHT, new Question(activity.getResources().getString(R.string.swipe_right), activity.getResources().getString(R.string.swipe_right)));
        data.put(Constants.ON_LONG_PRESS, new Question(activity.getResources().getString(R.string.on_long_press), activity.getResources().getString(R.string.on_long_press)));
        data.put(Constants.SCROLL_DOWN, new Question(activity.getResources().getString(R.string.scroll_down), activity.getResources().getString(R.string.scroll_down)));
        data.put(Constants.WAIT, new Question(activity.getResources().getString(R.string.wait), activity.getResources().getString(R.string.wait)));
        data.put(Constants.SHAKE_PHONE, new Question(activity.getResources().getString(R.string.shake_me), activity.getResources().getString(R.string.shake_me)));
        data.put(Constants.SCROLL_UP, new Question(activity.getResources().getString(R.string.scroll_up), activity.getResources().getString(R.string.scroll_up)));

        return data;
    }
}
