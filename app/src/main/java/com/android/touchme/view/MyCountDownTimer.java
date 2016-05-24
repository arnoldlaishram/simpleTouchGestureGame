package com.android.touchme.view;

import android.os.CountDownTimer;

import com.android.touchme.Constants;
import com.android.touchme.listener.CountDownListener;

/**
 * Created by arnold on 24/5/16.
 */
public class MyCountDownTimer extends CountDownTimer {

    private static long millisInFuture ;
    private static long countDownInterval;
    private static long timeRemaining;
    private CountDownListener countDownListener;
    private static MyCountDownTimer myCountDownTimer = new MyCountDownTimer(millisInFuture, countDownInterval);


    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        this.timeRemaining = millisUntilFinished;
        countDownListener.onTick(millisUntilFinished);
    }

    @Override
    public void onFinish() {
        countDownListener.onCountDownFinished();
    }

    public static MyCountDownTimer getInstance() {
        return myCountDownTimer;
    }

    public static void setMillisInFuture(long millisInFuture) {
        MyCountDownTimer.millisInFuture = millisInFuture;
    }

    public static void setCountDownInterval(long countDownInterval) {
        MyCountDownTimer.countDownInterval = countDownInterval;
    }

    public void setCountDownListener(CountDownListener countDownListener) {
        this.countDownListener = countDownListener;
    }

    public static long getTimeRemaining() {
        return timeRemaining;
    }
}
