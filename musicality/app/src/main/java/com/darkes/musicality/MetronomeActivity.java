package com.darkes.musicality;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.widget.TextView;


public class MetronomeActivity extends AppCompatActivity{

    private SeekBar seekBarTempo;
    private TextView seekBarTextView;
    private GestureDetectorCompat gestureObject;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_metronome);

        gestureObject = new GestureDetectorCompat(this, new LearnGesture());

        seekBarTempo = (SeekBar) findViewById(R.id.seekBarTempo);
        seekBarTextView = (TextView) findViewById(R.id.tempoTextView);

        seekBarTempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarTextView.setText(Integer.toString(progress * 3));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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

                Intent intent = new Intent(MetronomeActivity.this, BPMActivity.class);
                finish();
                startActivity(intent);

            } else
                if(e2.getX() < e1.getX()) {
                    Intent intent = new Intent(MetronomeActivity.this, TunerActivity.class);
                    finish();
                    startActivity(intent);
                }

                return true;
        }
    }
}
