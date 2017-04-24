package com.darkes.musicality.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

import com.darkes.musicality.R;

//import com.chrynan.guitartuner.R;

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
 * Created by chRyNaN on 1/18/2016.
 */
public class IndicatorView extends View {
    private static final String TAG = IndicatorView.class.getSimpleName();
    private Paint paint;
    private Path path;
    private int color;
    private int length;
    private int bottomWidth;
    private float centerX;
    private float centerY;
    private float angle;
    private boolean isCenterX;
    private boolean isCenterY;

    private PointF point1;
    private PointF point2;
    private PointF point3;

    public IndicatorView(Context context){
        super(context);
        init(null);
    }

    public IndicatorView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    private void init(AttributeSet attrs){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        if(attrs != null){
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CircleTunerView, 0, 0);
            try{
                color = a.getColor(R.styleable.CircleTunerView_indicatorColor, CircleTunerView.DEFAULT_INDICATOR_COLOR);
                paint.setColor(color);
                isCenterX = a.getBoolean(R.styleable.CircleTunerView_centerX, true);
                isCenterY = a.getBoolean(R.styleable.CircleTunerView_centerY, true);
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                a.recycle();
            }
        }else{
            paint.setColor(CircleTunerView.DEFAULT_INDICATOR_COLOR);
            isCenterX = true;
            isCenterY = true;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setElevation();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setElevation(){
        setElevation(3f);
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
        length = (((int) Math.min(xWidth, yHeight)) / 2) - 10;
        if(isCenterX) {
            centerX = (int) xWidth / 2;
        }else{
            centerX = length;
        }
        if(isCenterY) {
            centerY = (int) yHeight / 2;
        }else{
            centerY = length;
        }
        bottomWidth = length / 2;
        showAngle(270);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
        path.close();
        canvas.drawPath(path, paint);
    }

    public int getColor(){
        return color;
    }

    public void setColor(@ColorInt int color){
        this.color = color;
        paint.setColor(color);
        invalidate();
    }

    public int getLength(){
        return length;
    }

    public int getBottomWidth(){
        return bottomWidth;
    }

    public float getCenterX(){
        return centerX;
    }

    public float getCenterY(){
        return centerY;
    }

    public float getAngle(){
        return angle;
    }

    public void setAngle(float angle){
        this.angle = angle;
    }

    public void showAngle(float angle){
        //angle should be between 0 and 360 in degrees
        //the angle corresponds to the "pointer" of the indicator
        //x = centerCircleX + radius * cos(angle)
        //y = centerCircleY + radius * sin(angle)
        //indicator "pointer"
        this.angle = angle;

        point1 = new PointF((float) (centerX + (length * Math.cos(Math.toRadians(angle)))),
                (float) (centerY + (length * Math.sin(Math.toRadians(angle)))));

        double bRadius = ((double) bottomWidth) / 2;
        float bAngle = angle - 90;

        bAngle = (bAngle < 0) ? 360 - Math.abs(bAngle) : bAngle;
        bAngle = (bAngle > 360) ? bAngle % 360 : bAngle;

        point2 = new PointF((float) (centerX + (bRadius * Math.cos(Math.toRadians(bAngle)))),
                (float) (centerY + (bRadius * Math.sin(Math.toRadians(bAngle)))));

        bAngle = angle + 90;

        bAngle = (bAngle < 0) ? 360 - Math.abs(bAngle) : bAngle;
        bAngle = (bAngle > 360) ? bAngle % 360 : bAngle;

        point3 = new PointF((float) (centerX + (bRadius * Math.cos(Math.toRadians(bAngle)))),
                (float) (centerY + bRadius * Math.sin(Math.toRadians(bAngle))));

        invalidate();
    }

    public void showAngle(){
        showAngle(angle);
    }

    public boolean isCenterX() {
        return isCenterX;
    }

    public void setIsCenterX(boolean centerX) {
        this.isCenterX = centerX;
        requestLayout();
    }

    public boolean isCenterY() {
        return isCenterY;
    }

    public void setIsCenterY(boolean centerY) {
        this.isCenterY = centerY;
        requestLayout();
    }

}