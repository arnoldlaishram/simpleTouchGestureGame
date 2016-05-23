package com.android.touchme.view;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.touchme.Constants;
import com.android.touchme.Data;
import com.android.touchme.R;
import com.android.touchme.util.PreferenceUtil;
import com.squareup.seismic.ShakeDetector;

import java.io.IOException;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnDoubleTapListener,
        GestureDetector.OnGestureListener, ShakeDetector.Listener {

    private static int progressTime = 3 * 1000;
    private static int successCount = 0;
    private static boolean isPaused = false;
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private static long currentProgress = 0;
    private static int tickTime = progressTime/100;

    @Bind(R.id.txt_touch_event) TextView txtTouchEvent;
    @Bind(R.id.progressBar) ProgressBar timerProgress;
    @Bind(R.id.img_btn_pause) ImageButton btnPlayOrPause;
    @Bind(R.id.txt_success_count) TextView txtSuccessCount;

    private Map<Integer, String> touchEvents;
    private GestureDetectorCompat gestureDetectorCompat;
    private boolean isTouched = false;
    private CountDownTimer countDownTimer;
    private PreferenceUtil preferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        gestureDetectorCompat = new GestureDetectorCompat(this, this);
        preferenceUtil = new PreferenceUtil(this);

        setNumberOfSuccessCount();
        initializeColorToProgress();
        initializeShakeDetection();
        initialiseCounter();
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
        currentProgress = 0;
        if (countDownTimer !=null ) {
            countDownTimer.cancel();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    // overrides of Shake Detector
    @Override
    public void hearShake() {
        onTouch(Constants.SHAKE_PHONE, 0, 0);
    }

    // overrides of GestureDetector.OnDoubleTapListener
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        onTouch(Constants.SINGLE_TAP, e.getX(), e.getY());
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        onTouch(Constants.DOUBLE_TAP, e.getX(), e.getY());
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    // overrides of GestureDetector.OnGestureListener
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //onTouch(Constants.ON_SCROLL);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        onTouch(Constants.ON_LONG_PRESS, e.getX(), e.getY());
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // Grab two events located on the plane at e1=(x1, y1) and e2=(x2, y2)
        // Let e1 be the initial event
        // e2 can be located at 4 different positions, consider the following diagram
        // (Assume that lines are separated by 90 degrees.)
        //
        //
        //         \ A  /
        //          \  /
        //       D   e1   B
        //          /  \
        //         / C  \
        //
        // So if (x2,y2) falls in region:
        //  A => it's an UP swipe
        //  B => it's a RIGHT swipe
        //  C => it's a DOWN swipe
        //  D => it's a LEFT swipe
        //

        float x1 = e1.getX();
        float y1 = e1.getY();

        float x2 = e2.getX();
        float y2 = e2.getY();

        Direction direction = getDirection(x1, y1, x2, y2);

        switch (direction) {
            case up:
                onTouch(Constants.SCROLL_UP, e1.getX(), e1.getY());
                break;
            case down:
                onTouch(Constants.SCROLL_DOWN,e1.getX(), e1.getY());
                break;
            case left:
                onTouch(Constants.SWIPE_LEFT, e1.getX(), e1.getY());
                break;
            case right:
                onTouch(Constants.SWIPE_RIGHT, e1.getX(), e1.getY());
                break;
        }
        return onSwipe(direction);
    }

    public boolean onSwipe(Direction direction) {
        return false;
    }

    /**
     * Given two points in the plane p1=(x1, x2) and p2=(y1, y1), this method
     * returns the direction that an arrow pointing from p1 to p2 would have.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the direction
     */
    public Direction getDirection(float x1, float y1, float x2, float y2) {
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.get(angle);
    }

    /**
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    public double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
        return (rad * 180 / Math.PI + 180) % 360;
    }

    public enum Direction {
        up,
        down,
        left,
        right;

        /**
         * Returns a direction given an angle.
         * Directions are defined as follows:
         * <p/>
         * Up: [45, 135]
         * Right: [0,45] and [315, 360]
         * Down: [225, 315]
         * Left: [135, 225]
         *
         * @param angle an angle from 0 to 360 - e
         * @return the direction of an angle
         */
        public static Direction get(double angle) {
            if (inRange(angle, 45, 135)) {
                return Direction.up;
            } else if (inRange(angle, 0, 45) || inRange(angle, 315, 360)) {
                return Direction.right;
            } else if (inRange(angle, 225, 315)) {
                return Direction.down;
            } else {
                return Direction.left;
            }

        }

        /**
         * @param angle an angle
         * @param init  the initial bound
         * @param end   the final bound
         * @return returns true if the given angle is in the interval [init, end).
         */
        private static boolean inRange(double angle, float init, float end) {
            return (angle >= init) && (angle < end);
        }
    }

    private void initialiseTouchEvents() {
        Data data = new Data(this);
        touchEvents = data.getTouchEvents();
        setTouchEvent(Constants.DOUBLE_TAP);
    }

    private void initializeShakeDetection() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
    }

    public void onTouch(Integer touchedEvent, float x, float y) {

        if (touchedEvent != Constants.WAIT) {
            cancelProgressAnimation();
        }

        String event = txtTouchEvent.getText().toString();

        if (event.equals(touchEvents.get(touchedEvent))) {

            successCount++;
            setNumberOfSuccessCount();
            touchEvents.remove(touchedEvent);

            if (touchEvents.size() > 0) {
                setTouchEvent(touchEvents.keySet().iterator().next());

            } else {
                initialiseTouchEvents();
            }
            return;
        }

        displayFinished();
    }

    private void setNumberOfSuccessCount() {
        txtSuccessCount.setText(successCount+"");
    }

    private void displayFinished() {
        if (successCount > preferenceUtil.readInt(preferenceUtil.HIGH_SCORE, 0)) {
            preferenceUtil.save(preferenceUtil.HIGH_SCORE, successCount);
        }
        successCount = 0;
        playWrongAnswerMusic();
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
        txtTouchEvent.setText(touchEvents.get(touchEvent));
        startCountingProgress();
    }

    private void initialiseCounter() {

        countDownTimer = new CountDownTimer(progressTime, tickTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerProgress.setProgress((int)(progressTime - millisUntilFinished)/tickTime);
            }

            @Override
            public void onFinish() {
                timerProgress.setProgress(progressTime);
                if (!isTouched) {
                    timerProgress.clearAnimation();
                    onTouch(Constants.WAIT, 0, 0);
                }
            }
        };
    }

    private void initializeColorToProgress(){
        timerProgress.getProgressDrawable().setColorFilter(Color.rgb(32,148,241), PorterDuff.Mode.SRC_IN);
    }

    private void startCountingProgress() {
        countDownTimer.start();
    }

    private void cancelProgressAnimation() {
        isTouched = true;
        timerProgress.setProgress(0);
        countDownTimer.cancel();
    }

    private void resumeProgress() {
        countDownTimer.start();
        timerProgress.setProgress((int)currentProgress);
        btnPlayOrPause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
    }

    private void pauseProgress() {
        currentProgress = timerProgress.getProgress();
        countDownTimer.cancel();
        btnPlayOrPause.setImageDrawable(getResources().getDrawable(R.drawable.play));
    }

}
