package com.darkes.musicality.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;

import com.darkes.musicality.Note;
import com.darkes.musicality.R;
import com.darkes.musicality.TunerUpdate;
import com.darkes.musicality.tarsos.PitchDetectionResult;

//import com.chrynan.guitartuner.Note;
//import com.chrynan.guitartuner.R;
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
 * Created by chRyNaN on 1/24/2016. This class is a CircleImageView that displays up, down, or check drawables
 * depending on the current state of the current Note object. For instance, if the actualFrequency property
 * of the Note object is less than the frequency property of that same Note object then this View will display
 * the up drawable.
 */
public class DirectionPointerView extends CircleImageView implements TunerUpdate {
    //Should keep this in sync with TunerView.DEFAULT_WIGGLE_ROOM
    public static final int DEFAULT_WIGGLE_ROOM = 5;
    private Drawable check;
    private Drawable up;
    private Drawable down;

    public DirectionPointerView(Context context){
        super(context);
        init(context, null);
    }

    public DirectionPointerView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    public DirectionPointerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context, AttributeSet attrs){
        //set default fill color
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            setDefaultFillColorMarshmallow(context);
        }else {
            setFillColor(context.getResources().getColor(R.color.white));
        }
        if(attrs != null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DirectionPointerView);
            try{
                up = a.getDrawable(R.styleable.DirectionPointerView_upDrawable);
                down = a.getDrawable(R.styleable.DirectionPointerView_downDrawable);
                check = a.getDrawable(R.styleable.DirectionPointerView_checkDrawable);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                a.recycle();
            }
        }
        if(up == null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                setDefaultUpDrawableLollipop(context);
            }else{
                up = context.getResources().getDrawable(R.drawable.up);
            }
        }
        if(down == null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                setDefaultDownDrawableLollipop(context);
            }else{
                down = context.getResources().getDrawable(R.drawable.down);
            }
        }
        if(check == null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                setDefaultCheckDrawableLollipop(context);
            }else{
                check = context.getResources().getDrawable(R.drawable.check);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setElevation(){
        setElevation(3f);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setDefaultFillColorMarshmallow(Context context){
        setFillColor(context.getColor(R.color.white));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setDefaultUpDrawableLollipop(Context context){
        up = context.getDrawable(R.drawable.up);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setDefaultDownDrawableLollipop(Context context){
        down = context.getDrawable(R.drawable.down);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setDefaultCheckDrawableLollipop(Context context){
        check = context.getDrawable(R.drawable.check);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        if(wMode == MeasureSpec.UNSPECIFIED || hMode == MeasureSpec.UNSPECIFIED
                || wMode == MeasureSpec.AT_MOST || hMode == MeasureSpec.AT_MOST){
            if(width >= height){
                width = height / 3;
                height = height / 3;
            }else{
                width = width / 3;
                height = width / 3;
            }
            setMeasuredDimension(width, height);
        }else{
            //be wary of weird issue that when calling super.onMeasure it creates an infinite loop.
            //probably something wrong with the Android Framework possibly in Marshmallow (since that's what I'm
            //currently testing in). However, don't know a work around right now and I need to call super for
            //this scenario.
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void updateNote(Note newNote, PitchDetectionResult result) {
        //give some room for if the note is in tune because it's nearly impossible
        //for it to be exact for more than a moment (since the algorithm works on such a small
        //scale, a slight fluctuation in even the volume could offset it a hair, which would be
        //considered out of tune). However, one thing to keep in mind is the fact that lower frequencies
        //are more distinguishable between shorter differences than higher frequencies. I can optimize this later
        //right now I'll use a default wiggle room variable.
        boolean equal = false;
        if(newNote.getActualFrequency() < newNote.getFrequency()){
            if(newNote.getActualFrequency() + DEFAULT_WIGGLE_ROOM >= newNote.getFrequency()){
                setImageDrawable(check);
                equal = true;
            }
        }else{
            if(newNote.getActualFrequency() - DEFAULT_WIGGLE_ROOM <= newNote.getFrequency()){
                setImageDrawable(check);
                equal = true;
            }
        }
        if(!equal){
            if(newNote.getActualFrequency() < newNote.getFrequency()){
                setImageDrawable(up);
            }else{
                setImageDrawable(down);
            }
        }
    }

    public Drawable getUpDrawable(){
        return up;
    }

    public void setUpDrawable(Drawable up){
        this.up = up;
        invalidate();
    }

    @SuppressWarnings("deprecation")
    public void setUpResource(@DrawableRes int resourceId){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setUpResourceLollipop(resourceId);
        }else{
            up = getContext().getResources().getDrawable(resourceId);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUpResourceLollipop(@DrawableRes int resourceId){
        up = getContext().getDrawable(resourceId);
    }

    public Drawable getDownDrawable(){
        return down;
    }

    public void setDownDrawable(Drawable down){
        this.down = down;
        invalidate();
    }

    @SuppressWarnings("deprecation")
    public void setDownResource(@DrawableRes int resourceId){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setDownResourceLollipop(resourceId);
        }else{
            down = getContext().getResources().getDrawable(resourceId);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setDownResourceLollipop(@DrawableRes int resourceId){
        down = getContext().getDrawable(resourceId);
    }

    public Drawable getCheckDrawable(){
        return check;
    }

    public void setCheckDrawable(Drawable check){
        this.check = check;
        invalidate();
    }

    @SuppressWarnings("deprecation")
    public void setCheckResource(@DrawableRes int resourceId){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setCheckResourceLollipop(resourceId);
        }else{
            getContext().getResources().getDrawable(resourceId);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setCheckResourceLollipop(@DrawableRes int resourceId){
        check = getContext().getDrawable(resourceId);
    }

}