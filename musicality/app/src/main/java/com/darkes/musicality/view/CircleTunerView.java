package com.darkes.musicality.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.darkes.musicality.Note;
import com.darkes.musicality.TunerUpdate;
import com.darkes.musicality.tarsos.PitchDetectionResult;

//import com.chrynan.guitartuner.Note;
//import com.chrynan.guitartuner.TunerUpdate;
//import com.chrynan.guitartuner.tarsos.PitchDetectionResult;

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
 * Created by chRyNaN on 1/14/2016. This View is focused on drawing to the screen three things:
 * the outer donut with text, the inner circle with text, and the indicator. When the View is
 * updated (updateNote is called), the inner circle text will change to the appropriate note and
 * the indicator will animate to the appropriate position.
 */
public class CircleTunerView extends RelativeLayout implements TunerUpdate {
    private static final String TAG = CircleTunerView.class.getSimpleName();
    public static final int DEFAULT_DONUT_COLOR = Color.parseColor("#FF9800");
    public static final int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
    public static final int DEFAULT_CIRCLE_COLOR = Color.parseColor("#B0BEC5");
    public static final int DEFAULT_INDICATOR_COLOR = Color.parseColor("#FBFAFA");

    private Note note;

    private DialView dial;
    private CircleView circle;
    private IndicatorView indicator;
    private boolean allowAddView = false;
    private IndicatorAnimator anim;

    public CircleTunerView(Context context){
        super(context);
        init(context, null);
    }

    public CircleTunerView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    public CircleTunerView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleTunerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        allowAddView = true;
        note = new Note(Note.DEFAULT_FREQUENCY);
        dial = new DialView(context, attrs);
        circle = new CircleView(context, attrs);
        indicator = new IndicatorView(context, attrs);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        dial.setLayoutParams(params);
        addView(dial);
        indicator.setLayoutParams(params);
        addView(indicator);
        circle.setLayoutParams(params);
        addView(circle);
        allowAddView = false;
        anim = new IndicatorAnimator(indicator);
    }

    @Override
    public void updateNote(Note newNote, PitchDetectionResult result) {
        //I'm very sloppily casting to different values way too often
        //Because the view should instantly reflect the update but I'm animating it to the update point, there will be a delay
        note = newNote;
        circle.setText(note.getNote());
        if(newNote.getFrequency() != Note.UNKNOWN_FREQUENCY) {
            anim.start(note, Math.toDegrees(dial.getAngleInterval()));
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params){
        if(allowAddView){
            super.addView(child, index, params);
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params){
        if(allowAddView){
            super.addView(child, params);
        }
    }

    @Override
    public void addView(View child, int index){
        if(allowAddView){
            super.addView(child, index);
        }
    }

    @Override
    public void addView(View child){
        if(allowAddView){
            super.addView(child);
        }
    }

    @Override
    public void addView(View child, int width, int height){
        if(allowAddView){
            super.addView(child, width, height);
        }
    }

    public int getIndicatorColor() {
        return indicator.getColor();
    }

    public void setIndicatorColor(int indicatorColor) {
        indicator.setColor(indicatorColor);
    }

    public int getOuterCircleColor() {
        return dial.getColor();
    }

    public void setOuterCircleColor(int outerCircleColor) {
        dial.setColor(outerCircleColor);
    }

    public int getOuterCircleTextColor() {
        return dial.getTextColor();
    }

    public void setOuterCircleTextColor(int outerCircleTextColor) {
        dial.setTextColor(outerCircleTextColor);
    }

    public int getInnerCircleColor() {
        return circle.getColor();
    }

    public void setInnerCircleColor(int innerCircleColor) {
        circle.setColor(innerCircleColor);
    }

    public int getInnerCircleTextColor() {
        return circle.getTextColor();
    }

    public void setInnerCircleTextColor(int innerCircleTextColor) {
        circle.setTextColor(innerCircleTextColor);
    }

    //delegating methods
    public void addOnNoteSelectedListener(DialView.OnNoteSelectedListener l){
        if(dial != null){
            dial.addOnNoteSelectedListener(l);
        }
    }

    public boolean removeOnNoteSelectedListener(DialView.OnNoteSelectedListener l){
        if(dial != null){
            return dial.removeOnNoteSelectedListener(l);
        }
        return false;
    }


    /*
     * This class is used to store text and it's corresponding positions that need to be drawn to the canvas.
     */
    public static class NotePosition {
        private String note;
        private float x;
        private float y;

        public NotePosition(float x, float y, String note){
            this.x = x;
            this.y = y;
            this.note = note;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

    }

}