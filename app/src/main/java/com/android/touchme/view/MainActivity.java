package com.android.touchme.view;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.touchme.Constants;
import com.android.touchme.Data;
import com.android.touchme.R;
import com.android.touchme.custom.CircularProgress;
import com.android.touchme.model.Question;
import com.android.touchme.util.PreferenceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends EventsActivity {

    private static int maxProgressTime;
    private static int successCount = 0;
    private static boolean isPaused = false;
    private static Map<Integer, Question> touchEvents;
    private static int countDownInterval;
    private static CountDownTimer countDownTimer;
    private static Random random = new Random();
    private static final MediaPlayer mediaPlayer = new MediaPlayer();

    @Bind(R.id.txt_touch_event) TextView txtTouchEvent;
    @Bind(R.id.progressBar) ProgressBar timerProgress;
    @Bind(R.id.img_btn_pause) ImageButton btnPlayOrPause;
    @Bind(R.id.txt_success_count) TextView txtSuccessCount;
    @Bind(R.id.progressbar_circular) CircularProgress circleProgress;

    private boolean isTouched = false;
    private PreferenceUtil preferenceUtil;
    private long timeRemaining = maxProgressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        preferenceUtil = new PreferenceUtil(this);

        initializeMaximumProgressTime(4000);
        initializeCountDownInterval();
        enableGestureDetector();
        displayNumberOfSuccessCount();
        initializeColorToProgress();
        initialiseTouchEvents();
    }

    @OnClick(R.id.img_btn_pause)
    void performAction() {
        if (isPaused) {
            resumeProgress();
            isPaused = false;
            return;
        }

        pauseProgress();
        isPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        successCount = 0;
        if (countDownTimer !=null ) {
            countDownTimer.cancel();
        }
    }

    private void initialiseTouchEvents() {
        Data data = new Data(this);
        touchEvents = data.getAllEvents();
        setTouchEvent(getRandomQuestionNumber());
    }

    public void onTouch(Integer touchedEvent) {

        if (touchedEvent == Constants.ON_LONG_PRESS) {
            showCircularProgress();
            final int doubleTapOutTime = ViewConfiguration.getDoubleTapTimeout();
            final int countDownInterval = doubleTapOutTime / 100;
            new CountDownTimer(doubleTapOutTime, countDownInterval) {

                @Override
                public void onTick(long millisUntilFinished) {
                    circleProgress.setProgress((int)(doubleTapOutTime - millisUntilFinished) / countDownInterval);
                }

                @Override
                public void onFinish() {
                    circleProgress.setProgress(doubleTapOutTime);
                    hideCircularProgress();
                }
            }.start();
        } else {
            hideCircularProgress();
        }

        if (touchedEvent != Constants.WAIT) {
            cancelProgressAnimation();
        }

        if (txtTouchEvent.getText().toString().equals(touchEvents.get(touchedEvent).getQuestion())) {
            incrementSuccessCount();
            displayNumberOfSuccessCount();
            setTouchEvent(getRandomQuestionNumber());
            return;
        }

        displayFinishedScreen();
    }

    private void incrementSuccessCount() {
        successCount++;
    }

    private void resetSuccessCount() {
        successCount = 0;
    }

    private void displayNumberOfSuccessCount() {
        txtSuccessCount.setText(successCount+"");
    }

    private void displayFinishedScreen() {
        if (successCount > preferenceUtil.readInt(preferenceUtil.HIGH_SCORE, 0)) {
            preferenceUtil.save(preferenceUtil.HIGH_SCORE, successCount);
        }
        resetSuccessCount();
        playWrongAnswerMusic();
        showFailureScreen();
    }

    private void showFailureScreen() {
        Intent intent = new Intent(this, FailureActivity.class);
        startActivity(intent);
        finish();
    }

    private void playWrongAnswerMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        try {
            mediaPlayer.reset();
            AssetFileDescriptor afd = getAssets().openFd("Wrong-answer.mp3");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTouchEvent(Integer touchEvent) {
        isTouched = false;
        txtTouchEvent.setText(touchEvents.get(touchEvent).getQuestion());
        Log.d("question", touchEvents.get(touchEvent).getQuestion() );
        startCountingProgress();
    }

    private void initialiseCounter() {
        countDownTimer = new CountDownTimer(timeRemaining, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                timerProgress.setProgress((int)(maxProgressTime - timeRemaining)/ countDownInterval);
            }

            @Override
            public void onFinish() {
                timerProgress.setProgress(maxProgressTime);
                if (!isTouched) {
                    onTouch(Constants.WAIT);
                }
            }
        };
    }

    private void initializeColorToProgress(){
        timerProgress.getProgressDrawable().setColorFilter(Color.rgb(32,148,241), PorterDuff.Mode.SRC_IN);
    }

    private void startCountingProgress() {
        timeRemaining = maxProgressTime;
        initialiseCounter();
        countDownTimer.start();
    }

    private void cancelProgressAnimation() {
        isTouched = true;
        timerProgress.setProgress(0);
        countDownTimer.cancel();
    }

    private void resumeProgress() {
        initialiseCounter();
        countDownTimer.start();
        enableGestureDetector();
        btnPlayOrPause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
    }

    private void pauseProgress() {
        disableGestureDetector();
        countDownTimer.cancel();
        btnPlayOrPause.setImageDrawable(getResources().getDrawable(R.drawable.play));
    }

    private Integer getRandomQuestionNumber() {
        List<Integer> keys = new ArrayList<Integer>(touchEvents.keySet());
        Log.d("key", keys.get(random.nextInt(keys.size()-1))+"");
        return keys.get(random.nextInt(keys.size()));
    }

    private void initializeCountDownInterval() {
        countDownInterval = maxProgressTime /100;
    }

    private void initializeMaximumProgressTime(int maxTime) {
        maxProgressTime = maxTime;
    }

    private void showCircularProgress() {
        circleProgress.setVisibility(View.VISIBLE);
    }

    private void hideCircularProgress() {
        circleProgress.setVisibility(View.GONE);
    }

}
