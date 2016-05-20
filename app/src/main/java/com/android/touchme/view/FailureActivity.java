package com.android.touchme.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.touchme.R;
import com.android.touchme.util.PreferenceUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by arnold on 14/5/16.
 */
public class FailureActivity extends AppCompatActivity{

    @Bind(R.id.txt_high_score) TextView txtHighScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.failure_activity);

        ButterKnife.bind(this);

        int highScore = new PreferenceUtil(this).readInt(PreferenceUtil.HIGH_SCORE, 0);

        txtHighScore.setText(highScore+"");
    }

    @OnClick(R.id.txt_tap_to_continue)
    void tapToContinue() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
