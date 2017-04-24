package com.darkes.musicality;

import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;


/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Circular Reveal Animation Transition help from here: https://gist.github.com/ferdy182/d9b3525aa65b5b4c468a
 */
public class TunerActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TunerFragment tunerFragment;
    private PitchFragment pitchFragment;
    private boolean showCancel;
    private GestureDetectorCompat gestureObject;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        showCancel = false;
        setContentView(R.layout.activity_tuner);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tunerFragment = new TunerFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, tunerFragment, TunerFragment.TAG).commit();

        gestureObject = new GestureDetectorCompat(this, new LearnGesture());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        if(showCancel) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }else{
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if(showCancel){
                    transitionBackToTunerFragment(pitchFragment.unreveal(pitchFragment.getX(), pitchFragment.getY()));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(showCancel){
            transitionBackToTunerFragment(pitchFragment.unreveal(pitchFragment.getX(), pitchFragment.getY()));
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case TunerFragment.AUDIO_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(tunerFragment != null) {
                        tunerFragment.init();
                    }
                }else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(TunerActivity.this, "GuitarTuner needs access to the microphone to function.", Toast.LENGTH_LONG).show();
                    TunerActivity.this.finish();
                }
                break;
        }
    }

    public void transitionToPitchFragment(Note note, float x, float y){
        if(!showCancel) {
            pitchFragment = PitchFragment.newInstance(note, x, y);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, pitchFragment, PitchFragment.TAG).commit();
            tunerFragment.stop();
            showCancel = true;
            invalidateOptionsMenu();
        }
    }

    public void transitionBackToTunerFragment(Animator anim){
        if(anim != null){
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // remove the fragment only when the animation finishes
                    getSupportFragmentManager().beginTransaction().remove(pitchFragment).commit();
                    //to prevent flashing the fragment before removing it, execute pending transactions inmediately
                    getSupportFragmentManager().executePendingTransactions();
                    tunerFragment.start();
                    showCancel = false;
                    invalidateOptionsMenu();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            anim.start();
        }else{
            getSupportFragmentManager().beginTransaction().remove(pitchFragment).commit();
            tunerFragment.start();
            showCancel = false;
            invalidateOptionsMenu();
        }
    }

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e2.getX() > e1.getX()) {

                Intent intent = new Intent(TunerActivity.this, MetronomeActivity.class);
                finish();
                startActivity(intent);

            } else
            if(e2.getX() < e1.getX()) {
                Intent intent = new Intent(TunerActivity.this, BPMActivity.class);
                finish();
                startActivity(intent);
            }

            return true;
        }
    }

}