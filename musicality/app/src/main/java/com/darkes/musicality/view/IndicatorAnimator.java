package com.darkes.musicality.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

import com.darkes.musicality.Note;

/*
 * Copyright 2016 chRyNaN
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
 * Created by chRyNaN on 1/21/2016. This class encapsulates an ObjectAnimator which will be used to animate
 * the IndicatorView within a CircleTunerView. The ObjectAnimator updates the IndicatorView's angle value which
 * changes its position. There's a lot of computations occurring on the UI thread which may be problematic. Consider
 * handling that logic on a different thread and posting to the UI thread with a Handler when needed.
 */
public class IndicatorAnimator {
    private static final String TAG = IndicatorAnimator.class.getSimpleName();
    private IndicatorView view;
    private int duration;
    private float angle;
    private float lastShownAngle;
    private ObjectAnimator anim;
    private Note prevNote;

    public IndicatorAnimator(IndicatorView view){
        this.view = view;
        this.duration = 1000;
        this.angle = 0;
        this.lastShownAngle = 0;
    }

    private void init(){
        if(anim != null && anim.isRunning()){
            anim.cancel();
        }
        this.anim = ObjectAnimator.ofFloat(view, "angle", view.getAngle(), angle);
        this.anim.setDuration(duration);
        this.anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(view != null && lastShownAngle != view.getAngle()){
                    view.showAngle();
                    lastShownAngle = view.getAngle();
                }else if(view != null){
                    lastShownAngle = view.getAngle();
                }
            }
        });
    }

    public IndicatorView getView(){
        return view;
    }

    public void setView(IndicatorView view){
        this.view = view;
    }

    public int getDuration(){
        return duration;
    }

    public void setDuration(int durationInMilliseconds){
        this.duration = durationInMilliseconds;
    }

    public float getAngle(){
        return angle;
    }

    public void setAngle(float angle){
        this.angle = angle;
    }

    public void start(){
        init();
        if(anim != null){
            anim.start();
        }
    }

    public void start(Note note, double angleInterval){
        if(prevNote == null || isDifferent(prevNote, note)){
            calculateNewAngle(note, angleInterval);
            init();
            if (anim != null) {
                anim.start();
            }
            prevNote = new Note(note);
        }
    }

    private boolean isDifferent(Note oldNote, Note newNote){
        if(oldNote != null && newNote != null){
            if((oldNote.getActualFrequency() >= newNote.getActualFrequency()) &&
                    ((oldNote.getActualFrequency() - 5.0) <= newNote.getActualFrequency()) &&
                    oldNote.getNote().equals(newNote.getNote())){
                return false;
            }else if((oldNote.getActualFrequency() < newNote.getActualFrequency()) &&
                    ((oldNote.getActualFrequency() + 5.0) >= newNote.getActualFrequency()) &&
                    oldNote.getNote().equals(newNote.getNote())){
                return false;
            }else{
                return true;
            }
        }
        return false;
    }

    private float calculateNewAngle(Note note, double angleInterval){
        int angleDifference;
        double percentage;
        float newAngle;
        if(note.getActualFrequency() < note.getFrequency()){
            percentage = ((note.getActualFrequency() - note.getNoteBelowFrequency()) * 100)
                    / (note.getFrequency() - note.getNoteBelowFrequency());
            angleDifference = (int) ((percentage / 100) * angleInterval);
            newAngle = (float) ((angleInterval * note.getCToBNotesIndex()) - angleDifference);
        }else{
            percentage = ((note.getActualFrequency() - note.getFrequency()) * 100)
                    / (note.getNoteAboveFrequency() - note.getFrequency());
            angleDifference = (int) ((percentage / 100) * angleInterval);
            newAngle = (float) ((angleInterval * note.getCToBNotesIndex()) + angleDifference);
        }
        if(newAngle < 0){
            newAngle = 360 + newAngle;
        }else if(newAngle > 360){
            newAngle = newAngle % 360;
        }
        angle = newAngle;
        return angle;
    }

}