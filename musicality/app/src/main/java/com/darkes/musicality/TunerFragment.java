package com.darkes.musicality;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darkes.musicality.util.PermissionUtils;
import com.darkes.musicality.view.DialView;
import com.darkes.musicality.view.TunerView;


public class TunerFragment extends Fragment {
    public static final String TAG = TunerFragment.class.getSimpleName();
    public static final int AUDIO_PERMISSION_REQUEST_CODE = 4;
    private TunerView view;
    private Tuner tuner;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.tuner_fragment, parent, false);
        view = (TunerView) v.findViewById(R.id.tuner_view);
        view.addOnNoteSelectedListener(new DialView.OnNoteSelectedListener() {
            @Override
            public void onNoteSelected(Note note, float x, float y) {
                if (getActivity() instanceof TunerActivity) {
                    ((TunerActivity) getActivity()).transitionToPitchFragment(note, x, y);
                }
            }
        });
        if(!PermissionUtils.hasPermission(getActivity(), Manifest.permission.RECORD_AUDIO)){
            PermissionUtils.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_REQUEST_CODE);
        }
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(tuner != null && tuner.isInitialized()) {
            tuner.start();
        }else if(!PermissionUtils.hasPermission(getActivity(), Manifest.permission.RECORD_AUDIO)){
            PermissionUtils.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_REQUEST_CODE);
        }else{
            init();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(tuner != null) {
            tuner.stop();
        }
    }

    @Override
    public void onDestroy(){
        if(tuner != null) {
            tuner.stop();
            tuner.release();
        }
        super.onDestroy();
    }

    public void start(){
        if(tuner == null && view != null){
            tuner = new Tuner(view);
            tuner.start();
        }else if(tuner != null){
            tuner.start();
        }
    }

    public void stop(){
        if(tuner != null){
            tuner.stop();
        }
    }

    public void init(){
        if(view != null && PermissionUtils.hasPermission(getActivity(), Manifest.permission.RECORD_AUDIO)){
            tuner = new Tuner(view);
            tuner.start();
        }
    }

}