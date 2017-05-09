package com.darkes.musicality;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


import com.darkes.musicality.metronome.Beats;
import com.darkes.musicality.metronome.Metronome;
import com.darkes.musicality.sgt.SimpleGuitarTunerActivity;


public class MetronomeActivity extends AppCompatActivity{

    private final short MIN_BPM = 1;
    private final short MAX_BPM =  300;

    private short bpm = 1;
    private short noteValue = 4;
    private short beats = 4;
    private double beatSound = 2440;
    private double sound = 6440;
    private AudioManager audio;
    private MetronomeAsyncTask metroTask;

    //private Button plusButton;
    //private Button minusButton;
    //private TextView currentBeat;

    //me
    private Toolbar toolbar;
    private boolean playing;
    private FloatingActionButton playStopFAB;
    private Button addOneButton;
    private Button addFiveButton;
    private Button minusOneButton;
    private Button minusFiveButton;
    private SeekBar seekBarTempo;
    private TextView seekBarTextView;
    private GestureDetectorCompat gestureObject;

    private Handler handler;

    // have in mind that: http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
    // in this case we should be fine as no delayed messages are queued
    private Handler getHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                String message = (String)msg.obj;
//                if(message.equals("1"))
//                    currentBeat.setTextColor(Color.GREEN);
//                else
//                    currentBeat.setTextColor(getResources().getColor(R.color.yellow));
//                currentBeat.setText(message);
            }
        };
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_metronome);

        gestureObject = new GestureDetectorCompat(this, new LearnGesture());


        metroTask = new MetronomeAsyncTask();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        playStopFAB = (FloatingActionButton) findViewById(R.id.playStopFAB);
        addOneButton = (Button) findViewById(R.id.addOneButton);
        addFiveButton = (Button) findViewById(R.id.addFiveButton);
        minusOneButton = (Button) findViewById(R.id.minusOneButton);
        minusFiveButton = (Button) findViewById(R.id.minusFiveButton);
        seekBarTempo = (SeekBar) findViewById(R.id.seekBarTempo);
        seekBarTextView = (TextView) findViewById(R.id.tempoTextView);


        Spinner beatSpinner = (Spinner) findViewById(R.id.beatspinner);
        ArrayAdapter<Beats> arrayBeats =
                new ArrayAdapter<Beats>(this,
                        android.R.layout.simple_spinner_item, Beats.values());
        beatSpinner.setAdapter(arrayBeats);
        beatSpinner.setSelection(Beats.four.ordinal());
        arrayBeats.setDropDownViewResource(R.layout.spinner_dropdown);
        beatSpinner.setOnItemSelectedListener(beatsSpinnerListener);

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        playing = false;
        playStopFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!playing) {
                    metroTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                    playing = true;
                    playStopFAB.setImageResource(R.drawable.ic_stop_white_36dp);
                }
                else {
                    metroTask.stop();
                    metroTask = new MetronomeAsyncTask();
                    Runtime.getRuntime().gc();
                    playing = false;
                    playStopFAB.setImageResource(R.drawable.ic_play_arrow_white_36dp);
                }
            }
        });

        seekBarTempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bpm = (short) (progress);
                seekBarTextView.setText("" + bpm);
                if(bpm != 0)
                    metroTask.setBpm(bpm);
                //minBpmGuard();
                //maxBpmGuard();
                //seekBarTextView.setText(Integer.toString(progress * 3));
                //bpm = (short) (progress * 3);
                //metroTask.setBpm((short) (progress * 3));
                //seekBarTextView.setText(Integer.toString(progress * 3));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        addOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = (bpm >= MAX_BPM) ? MAX_BPM : (++bpm);
                seekBarTextView.setText("" + bpm);
                metroTask.setBpm(bpm);
                seekBarTempo.setProgress(bpm);
                //maxBpmGuard();
            }
        });


        addFiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bpm = (bpm + 5 > MAX_BPM) ? MAX_BPM : (bpm += 5);
                seekBarTempo.setProgress(bpm);
                seekBarTextView.setText("" + bpm);
                metroTask.setBpm(bpm);
                //maxBpmGuard();
            }
        });

        minusOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = (bpm <= MIN_BPM) ? MIN_BPM : (--bpm);
                seekBarTextView.setText("" + bpm);
                metroTask.setBpm(bpm);
                seekBarTempo.setProgress(bpm);
                //minBpmGuard();
            }
        });

        minusFiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = (bpm - 5 < MIN_BPM) ? MIN_BPM : (bpm -= 5);
                seekBarTextView.setText("" + bpm);
                metroTask.setBpm(bpm);
                seekBarTempo.setProgress(bpm);
                //minBpmGuard();
            }
        });
    }

    private AdapterView.OnItemSelectedListener beatsSpinnerListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            Beats beat = (Beats) arg0.getItemAtPosition(arg2);
            //TextView timeSignature = (TextView) findViewById(R.id.timesignature);
            //timeSignature.setText(""+beat+"/"+noteValue);
            metroTask.setBeat(beat.getNum());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    };

    private class MetronomeAsyncTask extends AsyncTask<Void,Void,String> {
        Metronome metronome;

        MetronomeAsyncTask() {
            handler = getHandler();
            metronome = new Metronome(handler);
        }

        protected String doInBackground(Void... params) {
            metronome.setBeat(beats);
            metronome.setNoteValue(noteValue);
            metronome.setBpm(bpm);
            metronome.setBeatSound(beatSound);
            metronome.setSound(sound);

            metronome.play();

            return null;
        }

        public void stop() {
            metronome.stop();
            metronome = null;
        }

        public void setBpm(short bpm) {
            metronome.setBpm(bpm);
            metronome.calcSilence();
        }

        public void setBeat(short beat) {
            if(metronome != null)
                metronome.setBeat(beat);
        }

    }

    private void maxBpmGuard() {
        if(bpm >= MAX_BPM) {
            addOneButton.setEnabled(false);
            addOneButton.setPressed(false);
        } else if(!minusOneButton.isEnabled() && bpm > MIN_BPM) {
            minusOneButton.setEnabled(true);
        }

        if(bpm + 5 > MAX_BPM) {
            addFiveButton.setEnabled(false);
            addFiveButton.setPressed(false);
        } else if(!minusFiveButton.isEnabled() && bpm - 5 > MIN_BPM) {
            minusFiveButton.setEnabled(true);
        }

    }


    private void minBpmGuard() {
        if(bpm <= MIN_BPM) {
            minusOneButton.setEnabled(false);
            minusOneButton.setPressed(false);
        } else if(!addOneButton.isEnabled() && bpm < MAX_BPM) {
            addOneButton.setEnabled(true);
        }

        if(bpm - 5 <= MIN_BPM) {
            minusFiveButton.setEnabled(false);
            minusFiveButton.setPressed(false);
        } else if(!addFiveButton.isEnabled() && bpm - 5 < MAX_BPM) {
            addFiveButton.setEnabled(true);
        }
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
                overridePendingTransition(R.anim.slide_in_left_right, R.anim.slide_out_left_right);

            } else
                if(e2.getX() < e1.getX()) {
                    //Intent intent = new Intent(MetronomeActivity.this, TunerActivity.class);
                    Intent intent = new Intent(MetronomeActivity.this, SimpleGuitarTunerActivity.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right_left, R.anim.slide_out_right_left);
                }

                return true;
        }
    }
}
