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

import org.w3c.dom.Text;

import java.util.Timer;

public class BPMActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureObject;
    private FloatingActionButton mFloatingActionButtonBPM;
    private TextView mTextViewBPM;
    private int mCounter;
    private Toolbar toolbar;
    Timer timer;
    private ImageButton metronomeImageButton;
    private ImageButton tunerImageButton;
    public static long RESET_DURATION = 2000;

    private BpmCalculator bpmCalculator;
    static final String BPM_COUNTER = "bpm";
    static final String TAP_COUNTER = "tap";
    private int mBpm = 0;
    private int tapCount = 0;
    private TextView curTapCountTextView;
    private TextView prevTapCountTextView;
    private TextView prevBpmTextView;

//
    //uncomment to add share button
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, shareMessage());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_message_subject));
                i = Intent.createChooser(i, getString(R.string.share_message_detail));
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
        getSupportActionBar().setTitle(R.string.toolbar_bpm);
        metronomeImageButton = (ImageButton) findViewById(R.id.bottomMetronomeButton);
        tunerImageButton = (ImageButton) findViewById(R.id.bottomTuningButton);
        mFloatingActionButtonBPM = (FloatingActionButton) findViewById(R.id.floatingActionButtonBPM);
        curTapCountTextView = (TextView) findViewById(R.id.currentCountTextView);
        prevTapCountTextView = (TextView) findViewById(R.id.previousCountTextView);
        prevBpmTextView = (TextView) findViewById(R.id.previousBpmTextView);
        gestureObject = new GestureDetectorCompat(this, new LearnGesture());


        bpmCalculator = new BpmCalculator();

        mFloatingActionButtonBPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpmCalculator.recordTime();

                Log.i("size", bpmCalculator.getSize() + "");
                tapCount = bpmCalculator.getSize();
                curTapCountTextView.setText(String.valueOf(tapCount));
                restartResetTimer();


                updateView();
            //mTextViewBPM.setText(++mCounter + "");
            }
        });

        metronomeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BPMActivity.this, MetronomeActivity.class);
                finish();
                startActivity(intent);
            }
        });

        tunerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BPMActivity.this, GuitarTunerActivity.class);
                finish();
                startActivity(intent);
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prevTapCountTextView.setText(String.valueOf(tapCount));
                        curTapCountTextView.setText("");
                        prevBpmTextView.setText(String.valueOf(mBpm));
                    }
                });

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
                Intent intent = new Intent(BPMActivity.this, GuitarTunerActivity.class);
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

    private String shareMessage() {
        return "Try out this amazing musician app!";
    }
}
