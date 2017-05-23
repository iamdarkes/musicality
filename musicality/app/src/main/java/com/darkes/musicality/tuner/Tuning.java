package com.darkes.musicality.tuner;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.darkes.musicality.R;

public class Tuning {
	public static final String TAG = "Tuning";

	private static class TuningType {
		public String humanReadableName;
		public double [] freqs;
		public String [] stringNames;
		public TuningType(String name, double [] f, String [] sn) {
			humanReadableName = name;
			freqs = f;
			stringNames = sn;
		}
	}
	//new tunings v1.0.1 drop C#, drop C, drop B, drop A, DADGAD, full step down,
    //half step up, open F,
	private static TuningType [] tuningTypes = new TuningType[]{
		new TuningType("Standard",
				new double[]{82.41, 110.00, 146.83, 196.00, 246.94, 329.63},
				new String[]{"E","A","D","G","B","E"}) ,
		new TuningType("Half Step Down",
				new double[]{77.78, 103.83, 138.59, 185.00, 233.08, 311.13},
				new String[]{"D#","G#","C#","F#","A#","D#"}) ,
        new TuningType("Full Step Down",
                new double[] {73.416, 97.999, 130.813, 174.614, 220.00, 293.665},
                new String[] {"D","G","C","F","A","D"}),
        new TuningType("Half Step Up",
                new double[] {77.782, 116.541, 155.563, 207.652, 261.626, 349.228},
                new String [] {"E#","A#","D#","G#","B#","E#"}),
		new TuningType("Drop D",
				new double[]{73.42, 110.00, 146.83, 196.00, 246.94, 329.63},
				new String[]{"D","A","D","G","B","E"}) ,
		new TuningType("Double Drop D",
				new double[]{73.42, 110.00, 146.83, 196.00, 246.94, 293.66},
                new String[]{"D","A","D","G","B","D"}) ,
        new TuningType("Drop C#",
            new double[]{69.30, 103.83, 138.59, 185.00, 233.08, 311.13},
            new String[] {"C#","G#","C#","F#","A#","D#"}),
        new TuningType("Drop C",
                new double[]{65.41, 98.00, 130.81, 174.614, 220.00, 293.665},
                new String[] {"C","G","C","F","A","D"}),
        new TuningType("Drop B",
                new double[] {61.74, 92.50, 123.471, 164.814, 207.652, 277.183},
                new String[] {"B","F#","B","E","G#","C#"}),
        new TuningType("Drop A",
                new double[] {55.000, 82.407, 110.000, 146.832, 184.997, 246.942},
                new String[] {"A","E","A","D","F#","B"}),
		new TuningType("Open A",
				new double[]{82.41, 110.00, 164.81, 220.00, 277.18, 329.63},
				new String[]{"E","A","E","A","C#","E"}) ,
		new TuningType("Open C",
				new double[]{65.41, 98.00, 130.81, 196.00, 261.63, 329.63},
				new String[]{"C","G","C","G","C","E"}) ,
		new TuningType("Open D",
				new double[]{73.42, 110.00, 146.83, 185.00, 220.00, 293.66},
				new String[]{"D","A","D","F#","A","D"}) ,
		new TuningType("Open E",
				new double[]{82.41, 123.47, 164.81, 207.65, 246.94, 329.63},
				new String[]{"E","B","E","G#","B","E"}) ,
		new TuningType("Open Em",
				new double[]{82.41, 123.47, 164.81, 196.00, 246.94, 329.63},
				new String[]{"E","B","E","G","B","E"}) ,
        new TuningType("Open F",
                new double[] {87.307, 110.00, 103.826, 174.614, 261.626, 349.228},
                new String[] {"F","A","C","F","C","F"}),
		new TuningType("Open G",
				new double[]{98.00, 123.47, 146.83, 196.00, 246.94, 293.66},
				new String[]{"G","B","D","G","B","D"}) ,
        new TuningType("DADGAD",
                new double[] {73.416, 110.000, 146.832, 195.998, 220.000, 293.665},
                new String[] {"D","A","D","G","A","D"})

	};


	
	public static void populateSpinner(Activity parent, Spinner s) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent,
              android.R.layout.simple_spinner_item);
		for(int i=0; i<tuningTypes.length; ++i) {
			String label=tuningTypes[i].humanReadableName + " (";
			for(int j=0; j<tuningTypes[i].stringNames.length; ++j) {
				label+=tuningTypes[i].stringNames[j] + 
				((j==tuningTypes[i].stringNames.length -1) ? ")": ",");
			}
			adapter.add(label);
		}
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    s.setAdapter(adapter);
	}
	
	public class GuitarString {
		public int stringId; // no of string in the order of ascending frequency
		public double minFreq;
		public double maxFreq;
		public double freq;
		public String name;
		public GuitarString(int s,double f, double mif, double maf, String n) {
			stringId=s;
			freq=f;
			minFreq=mif;
			maxFreq=maf;
			name=n;
		}
	}
	private final GuitarString zeroString = new GuitarString(0,0.0,0.0,0.0,"0");
	private GuitarString [] strings;
	private String humanReadableName;
	private int tuningId = 0;
	
	public int getTuningId() {
		return tuningId;
	}
	public void initStrings(double [] freqs, String [] names) {
		strings = new GuitarString[freqs.length];
		for(int i=0; i<freqs.length; ++i) {
			double ldist = (i==0) ? 0.75*(2*freqs[i]-(freqs[i]+freqs[i+1])/2) 
					              : (freqs[i]+freqs[i-1])/2;
			double rdist = (i==freqs.length-1) ? 1.5*(2*freqs[i] - (freqs[i]+freqs[i-1])/2)
					                           : (freqs[i]+freqs[i+1])/2;
			//Log.e(TAG, "" + freqs[i] + ": " + (ldist) + " " + rdist);
			strings[i]=new GuitarString(i+1,freqs[i],ldist,rdist, names[i]);
		}
	}
/*
	private void outputStringsFrequencies() {
		for(int i=0; i<newstrings.length; ++i) {
			Log.d(TAG, newstrings[i].name + ": " + newstrings[i].freq + " e [" +
					newstrings[i].minFreq + "," + newstrings[i].maxFreq + "]");
		}
	}
*/
	public Tuning(int tuningNumber) {
		initStrings(tuningTypes[tuningNumber].freqs,
				    tuningTypes[tuningNumber].stringNames);
		humanReadableName = tuningTypes[tuningNumber].humanReadableName;
		tuningId = tuningNumber;
		//outputStringsFrequencies();
	}
	
	public String getName() {
		return humanReadableName;
	}
	
	GuitarString getString(double frequency) {
		for(int i=0; i<strings.length; ++i) {
			if(strings[i].minFreq <=frequency && frequency<=strings[i].maxFreq)
				return strings[i];
		}
		return zeroString;
	}
}
