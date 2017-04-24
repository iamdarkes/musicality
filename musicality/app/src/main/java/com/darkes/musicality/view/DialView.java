package com.darkes.musicality.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
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


public class DialView extends View {
    private static final String TAG = DialView.class.getSimpleName();
    private Paint paint;
    private Paint textPaint;

    private int color;
    private int textColor;

    private int diameter;
    private int width;
    private int textSize;
    private float circleCenterX;
    private float circleCenterY;
    private double angleInterval;
    private boolean centerX;
    private boolean centerY;

    private RectF bounds;
    private Rect textBounds;
    private CircleTunerView.NotePosition[] notePositions;

    private List<OnNoteSelectedListener> listeners;

    public DialView(Context context){
        super(context);
        init(null);
    }

    public DialView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }

    public DialView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DialView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        listeners = new ArrayList<>();
        notePositions = new CircleTunerView.NotePosition[12];
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        if(attrs != null){
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CircleTunerView, 0, 0);
            try {
                color = a.getColor(R.styleable.CircleTunerView_outerCircleColor, CircleTunerView.DEFAULT_DONUT_COLOR);
                paint.setColor(color);
                textColor = a.getColor(R.styleable.CircleTunerView_outerCircleTextColor, CircleTunerView.DEFAULT_TEXT_COLOR);
                textPaint.setColor(textColor);
                centerX = a.getBoolean(R.styleable.CircleTunerView_centerX, true);
                centerY = a.getBoolean(R.styleable.CircleTunerView_centerY, true);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                a.recycle();
            }
        }else{
            color = CircleTunerView.DEFAULT_DONUT_COLOR;
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
        setElevation(1f);
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
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight){
        float xPad = (float) (getPaddingLeft() + getPaddingRight());
        float yPad = (float) (getPaddingTop() + getPaddingBottom());
        //width minus the padding
        float xWidth = w - xPad;
        //height minus the padding
        float yHeight = h - yPad;
        diameter = (int) Math.min(xWidth, yHeight);
        width = diameter / 6;
        paint.setStrokeWidth(width);
        circleCenterX = (int) xWidth / 2;
        circleCenterY = (int) yHeight / 2;
        //for the bounds of the outer circle
        float startX = Math.abs(((diameter - width) - xWidth) / 2);
        float startY = Math.abs(((diameter - width) - yHeight) / 2);
        if(!centerX) {
            circleCenterX = diameter / 2;
            startX = 0f;
        }
        if(!centerY) {
            circleCenterY = diameter / 2;
            TypedArray styledAttrs = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
            startY = styledAttrs.getDimension(0, 0);
            styledAttrs.recycle();
        }
        bounds = new RectF(startX, startY, startX + (diameter - width), startY + (diameter - width));
        textSize = width / 2;
        textPaint.setTextSize(textSize);
        calculateOuterTextPositions(circleCenterX, circleCenterY, (diameter / 2) - (width / 2));
    }

    private void calculateOuterTextPositions(float centerCircleX, float centerCircleY, float radius){
        //x = centerCircleX + radius * cos(angle)
        //y = centerCircleY + radius * sin(angle)
        angleInterval = Math.toRadians(360 / 12);
        String text;
        for(int i = 0; i < 12; i++){
            float x = (float) (centerCircleX + (radius * Math.cos(angleInterval * i)));
            float y = (float) (centerCircleY + (radius * Math.sin(angleInterval * i)));
            textBounds = new Rect();
            text = Note.C_TO_B_NOTES[i];
            textPaint.getTextBounds(text, 0, text.length(), textBounds);
            notePositions[i] = new CircleTunerView.NotePosition(x, y + (textBounds.height() / 2), text);
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //draw the outer circle
        canvas.drawArc(bounds, 0, 360, false, paint);
        //draw the text on the outer circle
        canvas.save();
        for(CircleTunerView.NotePosition n : notePositions){
            //TODO add scale call to allow text to rotate with the circle
            canvas.drawText(n.getNote(), n.getX(), n.getY(), textPaint);
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        switch(e.getAction()){
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                //determine where the user selected and what note that is
                float x = e.getX();
                float y = e.getY();
                //(x - x0)^2 + (y - y0)^2 = r^2
                //gives us the radius of the touch coordinates with respect to the center circle coordinates
                double tRadius = Math.sqrt(Math.pow((x - circleCenterX), 2) + Math.pow((y - circleCenterY), 2));
                //check that the tRadius value is within the radius and the radius plus the width value
                if(tRadius >= diameter / 2 - width && tRadius <= diameter / 2){
                    //now that we know the touch point is on the dial, we can compute its angle
                    float a = (float) Math.toDegrees(Math.atan2(y - circleCenterY, x - circleCenterX));
                    a = (a < 0) ? 360 - Math.abs(a) : a;
                    a = (a > 360) ? a % 360 : a;
                    //now that we have the angle, we can figure out which note that corresponds to
                    int i;
                    float ai = (float) Math.toDegrees(angleInterval);
                    float r = ai / 2;
                    for(i = 0; i < 12; i++){
                        if(a >= ai * i - r && a < ai * i + r){
                            break;
                        }
                    }
                    i = (i >= 12) ? 11 : i;
                    Note n = new Note(Note.C4_TO_B4_VALUES[i]);
                    alertOnNoteSelected(n, x, y);
                }
                return false;
        }
        return super.onTouchEvent(e);
    }

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

    public int getDiameter(){
        return diameter;
    }

    public int getStrokeWidth(){
        return width;
    }

    public int getTextSize(){
        return textSize;
    }

    public float getCircleCenterX(){
        return circleCenterX;
    }

    public float getCircleCenterY(){
        return circleCenterY;
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

    public void setTextColor(@ColorInt int textColor){
        this.textColor = textColor;
        this.textPaint.setColor(textColor);
        invalidate();
    }

    public double getAngleInterval() {
        return angleInterval;
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