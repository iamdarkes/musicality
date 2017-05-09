package com.darkes.musicality;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.darkes.musicality.metronome.MetronomeA;
import com.darkes.musicality.sgt.SimpleGuitarTunerActivity;

public class BPMActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureObject;
    private FloatingActionButton mFloatingActionButtonBPM;
    private TextView mTextViewBPM;
    private int mCounter;
    private Toolbar toolbar;
    private BpmCalculator bpmCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpm);

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
                updateView();
            //mTextViewBPM.setText(++mCounter + "");
            }
        });
    }


    private void updateView() {
        String displayValue;

        if (bpmCalculator.times.size() >= 2) {
            int bpm = bpmCalculator.getBpm();
            displayValue = Integer.valueOf(bpm).toString();
        } else {
            displayValue = getString(R.string.zeroes);
        }

        TextView bpmTextView = (TextView) findViewById(R.id.BPMTextView);
        bpmTextView.setText(displayValue);
    }

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
