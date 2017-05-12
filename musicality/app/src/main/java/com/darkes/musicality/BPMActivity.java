package com.darkes.musicality;
import java.util.TimerTask;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.darkes.musicality.metronome.MetronomeA;
import com.darkes.musicality.sgt.SimpleGuitarTunerActivity;

import java.util.Timer;

public class BPMActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureObject;
    private FloatingActionButton mFloatingActionButtonBPM;
    private TextView mTextViewBPM;
    private int mCounter;
    private Toolbar toolbar;
    Timer timer;
    public static long RESET_DURATION = 2000;

    private BpmCalculator bpmCalculator;
    static final String BPM_COUNTER = "bpm";
    private int mBpm = 0;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(BPM_COUNTER, mBpm);
        super.onSaveInstanceState(outState);
        Log.i("save", mBpm + "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpm);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        if(savedInstanceState != null) {
            Log.i("saved", mBpm + "");
            mBpm = savedInstanceState.getInt(BPM_COUNTER, 0);
            TextView bpmTextView = (TextView) findViewById(R.id.BPMTextView);
            bpmTextView.setText(Integer.valueOf(mBpm).toString());
            Log.i("savedafter", mBpm + "");
        }


        //mTextViewBPM = (TextView) findViewById(R.id.BPMTextView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFloatingActionButtonBPM = (FloatingActionButton) findViewById(R.id.floatingActionButtonBPM);
        gestureObject = new GestureDetectorCompat(this, new LearnGesture());


        bpmCalculator = new BpmCalculator();

        mFloatingActionButtonBPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpmCalculator.recordTime();


                restartResetTimer();


                updateView();
            //mTextViewBPM.setText(++mCounter + "");
            }
        });
    }

    private void updateView() {
        String displayValue;
        if (bpmCalculator.times.size() >= 2) {
            mBpm = bpmCalculator.getBpm();
            displayValue = Integer.valueOf(mBpm).toString();
        } else {
            displayValue = Integer.valueOf(mBpm).toString();
            //displayValue = getString(R.string.zeroes);
        }

        TextView bpmTextView = (TextView) findViewById(R.id.BPMTextView);
        bpmTextView.setText(displayValue);
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }

        bpmCalculator.clearTimes();
        super.onDestroy();
    }



    public void handleTouch() {
        bpmCalculator.recordTime();
        restartResetTimer();
        updateView();
    }


    private void restartResetTimer() {
        stopResetTimer();
        startResetTimer();
    }

    private void startResetTimer() {
        timer = new Timer("reset-bpm-calculator", true);
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                bpmCalculator.clearTimes();

            }
        }, RESET_DURATION);
    }

    private void stopResetTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }


    //responsible for swipe
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e2.getX() > e1.getX()) {

                //Intent intent = new Intent(BPMActivity.this, TunerActivity.class);
                Intent intent = new Intent(BPMActivity.this, SimpleGuitarTunerActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left_right, R.anim.slide_out_left_right);

            } else
            if(e2.getX() < e1.getX()) {
                //Intent intent = new Intent(BPMActivity.this, MetronomeA.class);
                Intent intent = new Intent(BPMActivity.this, MetronomeActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right_left, R.anim.slide_out_right_left);
            }

            return true;
        }
    }
}
