package com.darkes.musicality.bpm;
import java.util.TimerTask;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.darkes.musicality.R;
import com.darkes.musicality.metronome.MetronomeActivity;
import com.darkes.musicality.tuner.GuitarTunerActivity;

import java.util.Timer;

public class BpmActivity extends AppCompatActivity {

    private GestureDetectorCompat mGestureObject;
    private FloatingActionButton mBpmFloatingActionButton;
    private Toolbar mToolbar;
    Timer mTimer;
    private ImageButton mMetronomeImageButton;
    private ImageButton mTunerImageButton;
    public static long RESET_DURATION = 2000;

    private BpmCalculator mBpmCalculator;
    static final String BPM_COUNTER = "bpm_blue";
    static final String TAP_COUNTER = "tap";
    private int mBpm = 0;
    private int mTapCount = 0;
    private TextView mCurTapCountTextView;
    private TextView mPrevTapCountTextView;
    private TextView mPrevBpmTextView;

    //share button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //implicit intent for sharing app
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message_content));
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_message_subject));
                i = Intent.createChooser(i, getString(R.string.share_message_detail));
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //save counter state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(BPM_COUNTER, mBpm);
        super.onSaveInstanceState(outState);
        //Log.i("save", mBpm + "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpm);

        if(savedInstanceState != null) {
            //Log.i("saved", mBpm + "");
            mBpm = savedInstanceState.getInt(BPM_COUNTER, 0);
            TextView bpmTextView = (TextView) findViewById(R.id.BPMTextView);
            bpmTextView.setText(Integer.valueOf(mBpm).toString());
            //Log.i("savedafter", mBpm + "");
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.toolbar_bpm);
        mMetronomeImageButton = (ImageButton) findViewById(R.id.bottomMetronomeButton);
        mTunerImageButton = (ImageButton) findViewById(R.id.bottomTuningButton);
        mBpmFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButtonBPM);
        mCurTapCountTextView = (TextView) findViewById(R.id.currentCountTextView);
        mPrevTapCountTextView = (TextView) findViewById(R.id.previousCountTextView);
        mPrevBpmTextView = (TextView) findViewById(R.id.previousBpmTextView);
        mGestureObject = new GestureDetectorCompat(this, new LearnGesture());

        mBpmCalculator = new BpmCalculator();

        mBpmFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBpmCalculator.recordTime();

                //Log.i("size", mBpmCalculator.getSize() + "");
                mTapCount = mBpmCalculator.getSize();
                mCurTapCountTextView.setText(String.valueOf(mTapCount));
                restartResetTimer();

                updateView();
            }
        });

        mMetronomeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BpmActivity.this, MetronomeActivity.class);
                finish();
                startActivity(intent);
            }
        });

        mTunerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BpmActivity.this, GuitarTunerActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void updateView() {
        String displayValue;
        if (mBpmCalculator.times.size() >= 2) {
            mBpm = mBpmCalculator.getBpm();
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
        if (mTimer != null) {
            mTimer.cancel();
        }
        mBpmCalculator.clearTimes();
        super.onDestroy();
    }

//    public void handleTouch() {
//        mBpmCalculator.recordTime();
//        restartResetTimer();
//        updateView();
//    }

    private void restartResetTimer() {

        stopResetTimer();
        startResetTimer();

    }

    private void startResetTimer() {
        mTimer = new Timer("reset-bpm_blue-calculator", true);
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mBpmCalculator.clearTimes();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPrevTapCountTextView.setText(String.valueOf(mTapCount));
                        mCurTapCountTextView.setText("");
                        mPrevBpmTextView.setText(String.valueOf(mBpm));
                    }
                });

            }
        }, RESET_DURATION);
    }

    private void stopResetTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    //responsible for swipe
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mGestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e2.getX() > e1.getX()) {

                //Intent intent = new Intent(BpmActivity.this, TunerActivity.class);
                Intent intent = new Intent(BpmActivity.this, GuitarTunerActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left_right, R.anim.slide_out_left_right);

            } else
            if(e2.getX() < e1.getX()) {
                //Intent intent = new Intent(BpmActivity.this, MetronomeA.class);
                Intent intent = new Intent(BpmActivity.this, MetronomeActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right_left, R.anim.slide_out_right_left);
            }

            return true;
        }
    }
}
