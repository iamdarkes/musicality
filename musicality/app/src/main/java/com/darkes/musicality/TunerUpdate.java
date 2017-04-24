package com.darkes.musicality;

import com.darkes.musicality.tarsos.PitchDetectionResult;


public interface TunerUpdate {
    void updateNote(Note newNote, PitchDetectionResult result);
}