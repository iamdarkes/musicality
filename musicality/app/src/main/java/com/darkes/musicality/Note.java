package com.darkes.musicality;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.UUID;

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
public class Note implements Serializable{
    public static final double FREQUENCY_A_0 = 27.5000;
    public static final double FREQUENCY_PIANO_KEY_1 = FREQUENCY_A_0;
    public static final double FREQUENCY_C_8 = 4186.01;
    public static final double FREQUENCY_PIANO_KEY_88 = FREQUENCY_C_8;
    public static final double FREQUENCY_C_4 = 261.626;
    public static final double FREQUENCY_MIDDLE_C = FREQUENCY_C_4;
    public static final double FREQUENCY_C_SHARP_4 = 277.183;
    public static final double FREQUENCY_D_FLAT_4 = 277.183;
    public static final double FREQUENCY_D_4 = 293.665;
    public static final double FREQUENCY_D_SHARP_4 = 311.127;
    public static final double FREQUENCY_E_FLAT_4 = 311.127;
    public static final double FREQUENCY_E_4 = 329.628;
    public static final double FREQUENCY_F_4 = 349.228;
    public static final double FREQUENCY_F_SHARP_4 = 369.994;
    public static final double FREQUENCY_G_FLAT_4 = 369.994;
    public static final double FREQUENCY_G_4 = 391.995;
    public static final double FREQUENCY_G_SHARP_4 = 415.305;
    public static final double FREQUENCY_A_FLAT_4 = 415.305;
    public static final double FREQUENCY_A_4 = 440.000;
    public static final double FREQUENCY_A_SHARP_4 = 466.164;
    public static final double FREQUENCY_B_FLAT_4 = 466.164;
    public static final double FREQUENCY_B_4 = 493.883;
    public static final double DEFAULT_FREQUENCY = FREQUENCY_A_4;
    public static final double UNKNOWN_FREQUENCY = -1;

    public static final double[] C4_TO_B4_VALUES = new double[]{FREQUENCY_C_4, FREQUENCY_C_SHARP_4, FREQUENCY_D_4,
            FREQUENCY_D_SHARP_4, FREQUENCY_E_4, FREQUENCY_F_4, FREQUENCY_F_SHARP_4, FREQUENCY_G_4, FREQUENCY_G_SHARP_4,
            FREQUENCY_A_4, FREQUENCY_A_SHARP_4, FREQUENCY_B_4};

    public static final double[] PIANO_NOTE_FREQUENCIES = new double[]{27.5000, 29.1352, 30.8677, 32.7032, 34.6478, 36.7081,
            38.8909, 41.2034, 43.6535, 46.2493, 48.9994, 51.9131, 55.0000, 58.2705, 61.7354, 65.4064, 69.2957, 73.4162, 77.7817,
            82.4069, 87.3071, 92.4986, 97.9989, 103.826, 110.000, 116.541, 123.471, 130.813, 138.591, 146.832, 155.563, 164.814,
            174.614, 184.997, 195.998, 207.652, 220.000, 233.082, 246.942, 261.626, 277.183, 293.665, 311.127, 329.628, 349.228,
            369.994, 391.995, 415.305, 440.000, 466.164, 493.883, 523.251, 554.365, 587.330, 622.254, 659.255, 698.456, 739.989,
            783.991, 830.609, 880.000, 932.328, 987.767, 1046.50, 1108.73, 1174.66, 1244.51, 1318.51, 1396.91, 1479.98, 1567.98,
            1661.22, 1760.00, 1864.66, 1975.53, 2093.00, 2217.46, 2349.32, 2489.02, 2637.02, 2793.83, 2959.96, 3135.96, 3322.44,
            3520.00, 3729.31, 3951.07, 4186.01};

    public static final double[] GUITAR_STANDARD_FREQUENCIES = new double[]{329.628, 246.942, 195.998, 146.832, 110.000, 82.4069};

    public static final double[] GUITAR_C_TO_B_FREQUENCIES = new double[]{65.4064, 69.2957, 146.832, 155.563, 82.4069, 87.3071,
            92.4986, 195.998, 207.652, 110.000, 116.541, 246.942};

    public static final double SEMITONE_INTERVAL = Math.pow(2, 1/12);
    public static final int UNKNOWN_POSITION = -1;

    public static final String FLAT = "\u266D";
    public static final String SHARP = "\u266F";
    public static final String C = "C";
    public static final String C_SHARP = C + SHARP;
    public static final String D = "D";
    public static final String D_FLAT = D + FLAT;
    public static final String D_SHARP = D + SHARP;
    public static final String E = "E";
    public static final String E_FLAT = E + FLAT;
    public static final String F = "F";
    public static final String F_SHARP = F + SHARP;
    public static final String G = "G";
    public static final String G_FLAT = G + FLAT;
    public static final String G_SHARP = G + SHARP;
    public static final String A = "A";
    public static final String A_FLAT = A + FLAT;
    public static final String A_SHARP = A + SHARP;
    public static final String B = "B";
    public static final String B_FLAT = B + FLAT;
    public static final String UNKNOWN_NOTE = "";

    public static final String[] C_TO_B_NOTES = new String[]{C, C_SHARP, D, D_SHARP, E, F, F_SHARP,
            G, G_SHARP, A, A_SHARP, B};

    public static final String[] NOTES = new String[]{A, A_SHARP, B, C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP};

    public static final String[] GUITAR_STANDARD_NOTES = new String[]{E, B, G, D, A, E};

    //An id for keeping uniqueness between objects of the same type
    private String id;
    //The name of the note
    private String note;
    //The closest note frequency to the frequency provided in the constructor
    private double frequency;
    //The frequency provided to the constructor when making an instance of this object
    private double actualFrequency;
    //As in 4 for A4
    private int position;
    //Can be used for storing a value relating this note to another
    //or can be used for the difference between the actual frequency and the frequency
    private double difference;
    private double noteBelowFrequency;
    private double noteAboveFrequency;
    private int index;

    public Note(double frequency){
        this.id = UUID.randomUUID().toString();
        this.actualFrequency = frequency;
        init(frequency);
    }

    //If you want a Note object with the same values of another Note object but does not reference the other Note object.
    public Note(Note note){
        this.id = note.getId();
        this.note = note.getNote();
        this.frequency = note.getFrequency();
        this.actualFrequency = note.getActualFrequency();
        this.position = note.getPosition();
        this.difference = note.getDifference();
        this.noteBelowFrequency = note.getNoteBelowFrequency();
        this.noteAboveFrequency = note.getNoteAboveFrequency();
        this.index = note.getIndex();
    }

    /*
     * To avoid reallocating space and instantiating a new object each time, this method provides a way
     * to use an existing Note object and update its attributes. Hopefully, this will save some time even if
     * just a little. This method will not change the id property.
     */
    public void changeTo(double frequency){
        this.actualFrequency = frequency;
        init(frequency);
    }

    private void init(double frequency){
        if(frequency < 27.5000 || frequency > 4186.01){
            this.frequency = UNKNOWN_FREQUENCY;
            this.position = UNKNOWN_POSITION;
            this.note = UNKNOWN_NOTE;
            this.noteAboveFrequency = 29.1352;
            this.noteBelowFrequency = UNKNOWN_FREQUENCY;
            this.index = -1;
        }else{
            this.index = searchForNote(this.actualFrequency, PIANO_NOTE_FREQUENCIES,
                    0, PIANO_NOTE_FREQUENCIES.length);
            this.frequency = PIANO_NOTE_FREQUENCIES[index];
            if(index - 1 >= 0) {
                this.noteBelowFrequency = PIANO_NOTE_FREQUENCIES[index - 1];
            }
            if(index + 1 < PIANO_NOTE_FREQUENCIES.length) {
                this.noteAboveFrequency = PIANO_NOTE_FREQUENCIES[index + 1];
            }
            this.note = getNoteFromIndex(index);
            this.position = getPositionFromIndex(index);
        }
        this.difference = this.actualFrequency - this.frequency;
    }

    /*
     * A Binary Search recursive implementation that gets the closest piano note frequency from the frequency provided.
     * The return value is the index in the PIANO_NOTE_FREQUENCIES array where the appropriate value is stored.
     * To avoid having to make a copy of the array at specified indices, use min and max index parameters;
     * This will also keep the complexity down to meet the O(log(n)) Binary Search standard.
     * The parameters minIndex is inclusive and maxIndex is exclusive
     * (ex: 0 and array.length can be supplied at first respectively).
     */
    private int searchForNote(double frequency, double[] array, int minIndex, int maxIndex){
        //minIndex is inclusive and maxIndex is exclusive, so if they are only one apart that means
        //that we found our value and we can return it
        if(minIndex + 1 >= maxIndex){
            return minIndex;
        }
        //maxIndex - minIndex gives you the size of the useable array
        //since maxIndex is exclusive we subtract one to not include that value
        //dividing by two gives you half the length of the useable array
        //adding half the length to the minIndex gives us the middle index
        int pivot = (minIndex + (maxIndex - 1)) / 2;
        double midValue = array[pivot];
        if(frequency == midValue){
            return pivot;
        }else if(frequency < midValue){
            //frequency is less than value so search the left side of the array
            return searchForNote(frequency, array, minIndex, pivot);
        }else{
            //frequency is greater than or equal to value so search the right side of the array
            return searchForNote(frequency, array, pivot + 1, maxIndex);
        }
    }

    /*
     * Get the position value from the specified index corresponding to the PIANO_NOTE_FREQUENCIES array.
     */
    private int getPositionFromIndex(int index){
        if(index < 3){
            return 0;
        }else if(index < 15){
            return 1;
        }else if(index < 27){
            return 2;
        }else if(index < 39){
            return 3;
        }else if(index < 51){
            return 4;
        }else if(index < 63){
            return 5;
        }else if(index < 75){
            return 6;
        }else if(index < 87){
            return 7;
        }else{
            return 8;
        }
    }

    /*
     * Get the String representation of the note from the specified index corresponding to
     * the PIANO_NOTE_FREQUENCIES array.
     */
    private String getNoteFromIndex(int index){
        //if the index is less than twelve, simply return the value at the indexed position in the NOTES array
        if(index < 12) {
            return NOTES[index];
        }else{
            //otherwise mod the index by twelve to get the remainder which corresponds to the appropriate value in the
            //NOTES array
            return NOTES[index % 12];
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getActualFrequency() {
        return actualFrequency;
    }

    public int getPosition() {
        return position;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }

    public double getNoteBelowFrequency() {
        return noteBelowFrequency;
    }

    public double getNoteAboveFrequency() {
        return noteAboveFrequency;
    }

    public int getIndex() {
        return index;
    }

    //returns 0 - 11 depending on the index position of the note according to the C_TO_B_NOTES array
    public int getCToBNotesIndex(){
        for(int i = 0; i < C_TO_B_NOTES.length; i++){
            if(C_TO_B_NOTES[i].equals(note)){
                return i;
            }
        }
        return -1;
    }

    public static double getFrequencyFromString(String s){
        switch(s){
            case A:
                return FREQUENCY_A_4;
            case A_SHARP:
                return FREQUENCY_A_SHARP_4;
            case B:
                return FREQUENCY_B_4;
            case C:
                return FREQUENCY_C_4;
            case C_SHARP:
                return FREQUENCY_C_SHARP_4;
            case D:
                return FREQUENCY_D_4;
            case D_SHARP:
                return FREQUENCY_D_SHARP_4;
            case E:
                return FREQUENCY_E_4;
            case F:
                return FREQUENCY_F_4;
            case F_SHARP:
                return FREQUENCY_F_SHARP_4;
            case G:
                return FREQUENCY_G_4;
            case G_SHARP:
                return FREQUENCY_G_SHARP_4;
            default:
                return DEFAULT_FREQUENCY;
        }
    }

    public void fromJSON(JSONObject obj){
        try{
            if(obj.has("id")){
                this.id = obj.getString("id");
            }
            if(obj.has("note")){
                this.note = obj.getString("note");
            }
            if(obj.has("frequency")){
                this.frequency = obj.getDouble("frequency");
            }
            if(obj.has("actualFrequency")){
                this.actualFrequency = obj.getDouble("actualFrequency");
            }
            if(obj.has("position")){
                this.position = obj.getInt("position");
            }
            if(obj.has("difference")){
                this.difference = obj.getDouble("difference");
            }
            if(obj.has("noteBelowFrequency")){
                this.noteBelowFrequency = obj.getDouble("noteBelowFrequency");
            }
            if(obj.has("noteAboveFrequency")){
                this.noteAboveFrequency = obj.getDouble("noteAboveFrequency");
            }
            if(obj.has("index")){
                this.index = obj.getInt("index");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public JSONObject toJSON(){
        try{
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("note", note);
            obj.put("frequency", frequency);
            obj.put("actualFrequency", actualFrequency);
            obj.put("position", position);
            obj.put("difference", difference);
            obj.put("noteBelowFrequency", noteBelowFrequency);
            obj.put("noteAboveFrequency", noteAboveFrequency);
            obj.put("index", index);
            return obj;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String toJSONString(){
        JSONObject obj = this.toJSON();
        if(obj != null){
            return obj.toString();
        }
        return this.toString();
    }

    @Override
    public String toString() {
        return "Note{" +
                "id='" + id + '\'' +
                ", note='" + note + '\'' +
                ", frequency=" + frequency +
                ", actualFrequency=" + actualFrequency +
                ", position=" + position +
                ", difference=" + difference +
                ", noteBelowFrequency=" + noteBelowFrequency +
                ", noteAboveFrequency=" + noteAboveFrequency +
                ", index=" + index +
                '}';
    }

}