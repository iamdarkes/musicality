package com.darkes.musicality.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

import com.darkes.musicality.Note;
import com.darkes.musicality.R;

import java.util.ArrayList;
import java.util.List;


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

public class CircleView extends View {
    private Paint paint;
    private Paint textPaint;
    private Rect textBounds;

    private int color;
    private int textColor;

    private int textSize;
    private int diameter;
    private float circleCenterX;
    private float circleCenterY;
    private String text;
    private boolean centerX;
    private boolean centerY;

    private List<OnNoteSelectedListener> listeners;

    public CircleView(Context context){
        super(context);
        init(null);
    }

    public CircleView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        listeners = new ArrayList<>();
        text = "A";
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //set defaults
        paint.setStyle(Paint.Style.FILL);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        if(attrs != null){
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CircleTunerView, 0, 0);
            try{
                color = a.getColor(R.styleable.CircleTunerView_innerCircleColor, CircleTunerView.DEFAULT_CIRCLE_COLOR);
                paint.setColor(color);
                textColor = a.getColor(R.styleable.CircleTunerView_innerCircleTextColor, CircleTunerView.DEFAULT_TEXT_COLOR);
                textPaint.setColor(textColor);
                centerX = a.getBoolean(R.styleable.CircleTunerView_centerX, true);
                centerY = a.getBoolean(R.styleable.CircleTunerView_centerY, true);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                a.recycle();
            }
        }else{
            color = CircleTunerView.DEFAULT_CIRCLE_COLOR;
            textColor = CircleTunerView.DEFAULT_TEXT_COLOR;
            paint.setColor(color);
            textPaint.setColor(textColor);
            centerX = true;
            centerY = true;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setElevation();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setElevation(){
        setElevation(5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        switch(wMode){
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                width = (width > height) ? height : width;
                break;
        }
        switch(hMode){
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                height = (height > width) ? width : height;
                break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        float xPad = (float) (getPaddingLeft() + getPaddingRight());
        float yPad = (float) (getPaddingTop() + getPaddingBottom());
        //width minus the padding
        float xWidth = width - xPad;
        //height minus the padding
        float yHeight = height - yPad;
        int outerCircleDiameter = (int) Math.min(xWidth, yHeight);
        diameter = outerCircleDiameter / 2;
        if(centerX) {
            circleCenterX = (int) xWidth / 2;
        }else{
            circleCenterX = outerCircleDiameter / 2;
        }
        if(centerY) {
            circleCenterY = (int) yHeight / 2;
        }else{
            circleCenterY = outerCircleDiameter / 2;
        }
        textSize = diameter / 2;
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //draw the inner circle
        canvas.drawCircle(circleCenterX, circleCenterY, diameter / 2, paint);
        textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, circleCenterX, circleCenterY + (textBounds.height() / 2), textPaint);
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                if(text != null && !text.equals(Note.UNKNOWN_NOTE)){
                    alertOnNoteSelected(new Note(Note.getFrequencyFromString(text)), e.getX(), e.getY());
                }
                return true;
        }
        return super.onTouchEvent(e);
    }*/

    public interface OnNoteSelectedListener{
        void onNoteSelected(Note note, float x, float y);
    }

    public void addOnNoteSelectedListener(OnNoteSelectedListener l){
        if(listeners == null){
            listeners = new ArrayList<>();
        }
        listeners.add(l);
    }

    public boolean removeOnNoteSelectedListener(OnNoteSelectedListener l){
        if(listeners != null){
            return listeners.remove(l);
        }
        return false;
    }

    private void alertOnNoteSelected(Note note, float x, float y){
        for(OnNoteSelectedListener l : listeners){
            l.onNoteSelected(note, x, y);
        }
    }

    public int getTextSize(){
        return textSize;
    }

    public int getDiameter(){
        return diameter;
    }

    public float getCircleCenterX(){
        return circleCenterX;
    }

    public float getCircleCenterY(){
        return circleCenterY;
    }

    public String getText(){
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public int getColor(){
        return color;
    }

    public void setColor(@ColorInt int color){
        this.color = color;
        this.paint.setColor(color);
        invalidate();
    }

    public int getTextColor(){
        return textColor;
    }

    public void setTextColor(@ColorInt int color){
        this.textColor = color;
        this.textPaint.setColor(textColor);
        invalidate();
    }

    public boolean isCenterX() {
        return centerX;
    }

    public void setCenterX(boolean centerX) {
        this.centerX = centerX;
        requestLayout();
    }

    public boolean isCenterY() {
        return centerY;
    }

    public void setCenterY(boolean centerY) {
        this.centerY = centerY;
        requestLayout();
    }

}