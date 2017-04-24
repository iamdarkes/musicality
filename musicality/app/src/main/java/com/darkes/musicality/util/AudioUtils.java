package com.darkes.musicality.util;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;

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
 * Created by chRyNaN on 1/13/2016.
 */
public class AudioUtils {
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final int[] POSSIBLE_SAMPLE_RATES = new int[] {8000, 11025, 16000, 22050,
            32000, 37800, 44056, 44100, 47250, 48000};

    public static final int getSampleRate(boolean lowest){
        int sampleRate = -1;
        for(int rate : POSSIBLE_SAMPLE_RATES){
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                // buffer size is valid, Sample rate supported
                sampleRate = rate;
                if(lowest){
                    return sampleRate;
                }
            }
        }
        return sampleRate;
    }

    public static final int getSampleRate(){
        return getSampleRate(false);
    }

    public static final int getDefaultEncodingFormat(){
        return AudioFormat.ENCODING_PCM_16BIT;
    }

    public static final int getMinimumBufferSize(int sampleRate){
        return AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
    }

}