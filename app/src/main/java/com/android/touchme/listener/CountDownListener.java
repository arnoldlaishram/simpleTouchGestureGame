package com.android.touchme.listener;

/**
 * Created by arnold on 24/5/16.
 */
public interface CountDownListener {

    void onTick(long timeRemaining);

    void onCountDownFinished();

}
