package com.darkes.musicality;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

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
 * Created by chRyNaN on 1/22/2016.
 */
public class PitchFragment extends Fragment {
    public static final String TAG = PitchFragment.class.getSimpleName();
    private PitchPlayer player;
    private View viewRoot;
    private TextView text;
    private Note note;
    //for circular reveal
    private float x;
    private float y;

    public static PitchFragment newInstance(Note note){
        PitchFragment fragment = new PitchFragment();
        Bundle args = new Bundle();
        args.putSerializable("note", note);
        fragment.setArguments(args);
        return fragment;
    }

    public static PitchFragment newInstance(Note note, float x, float y){
        PitchFragment fragment = new PitchFragment();
        Bundle args = new Bundle();
        args.putSerializable("note", note);
        args.putFloat("x", x);
        args.putFloat("y", y);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        note = (Note) args.getSerializable("note");
        x = args.getFloat("x", -1);
        y = args.getFloat("y", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        player = new PitchPlayer();
        player.play(note);
        viewRoot = inflater.inflate(R.layout.pitch_fragment, parent, false);
        text = (TextView) viewRoot.findViewById(R.id.note);
        if (note != null) {
            text.setText(note.getNote());
        }
        viewRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                       int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                circularReveal(x, y);
            }
        });
        viewRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(getActivity() instanceof TunerActivity){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        ((TunerActivity) getActivity()).transitionBackToTunerFragment(unreveal(event.getX(), event.getY()));
                    }else {
                        ((TunerActivity) getActivity()).transitionBackToTunerFragment(null);
                    }
                    player.stop();
                }
                return true;
            }
        });
        return viewRoot;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(player != null){
            player.stop();
        }
    }

    public void updateNote(Note note) {
        this.note = note;
        if (text != null && note != null) {
            text.setText(note.getNote());
        }
    }

    public Note getNote() {
        return note;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void circularReveal(final float x, final float y) {
        if (x != -1 && y != -1) {
            float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, (int) x, (int) y, 0, finalRadius);
            //viewRoot.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            anim.setInterpolator(new DecelerateInterpolator(2f));
            anim.setDuration(1000);
            anim.start();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Animator unreveal(final float x, final float y){
        Animator anim = ViewAnimationUtils.createCircularReveal(getView(), (int) x, (int) y,
                (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight()), 0);
        anim.setInterpolator(new AccelerateInterpolator(0.5f));
        anim.setDuration(500);
        return anim;
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