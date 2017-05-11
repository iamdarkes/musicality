package com.darkes.musicality.sgt;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darkes.musicality.BPMActivity;
import com.darkes.musicality.MetronomeActivity;
import com.darkes.musicality.R;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class SimpleGuitarTunerActivity extends AppCompatActivity {
	// switch off gc logs.
	// System.setProperty("log.tag.falvikvm", "SUPPRESS"); 
	public static final String TAG = "Activity";
	
	private final boolean LAUNCHANALYZER = true;

	
	private ImageView guitar = null;
	//private ImageView tune = null;
	private Spinner tuningSelector = null;
	private SoundAnalyzer soundAnalyzer = null ;
	private UiController uiController = null;
	private TextView mainMessage = null;
    private GestureDetectorCompat gestureObject;
	private Boolean permissionsGranted = false;
    private Toolbar toolbar;



    private void requestRecordAudioPermission() {
        //check API version, do nothing if API version < 23!
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // permissionsGranted = true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Activity", "Granted!");
                    if(LAUNCHANALYZER) {
                        try {
                            soundAnalyzer = new SoundAnalyzer();
                        } catch(Exception e) {
                            Toast.makeText(this, "The are problems with your microphone :(", Toast.LENGTH_LONG ).show();
                            Log.e(TAG, "Exception when instantiating SoundAnalyzer: " + e.getMessage());
                        }
                    soundAnalyzer.addObserver(uiController);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Activity", "Denied!");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("permissionsGranted", permissionsGranted);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate()");
        setContentView(R.layout.activity_simple_tuner);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gestureObject = new GestureDetectorCompat(this, new LearnGesture());

        uiController = new UiController(SimpleGuitarTunerActivity.this);

        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        Log.i("permg",PackageManager.PERMISSION_GRANTED + "");

        Log.i("before", check + "");
          requestRecordAudioPermission();

        check = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        Log.i("after", check + "");


        if(check == PackageManager.PERMISSION_GRANTED) {
            if(LAUNCHANALYZER) {
                try {
                    soundAnalyzer = new SoundAnalyzer();
                } catch(Exception e) {
                    Toast.makeText(this, "The are problems with your microphone :(", Toast.LENGTH_LONG ).show();
                    Log.e(TAG, "Exception when instantiating SoundAnalyzer: " + e.getMessage());
                }
                soundAnalyzer.addObserver(uiController);
            }
        }


//        if(LAUNCHANALYZER) {
//	        try {
//	        	soundAnalyzer = new SoundAnalyzer();
//	        } catch(Exception e) {
//                Toast.makeText(this, "The are problems with your microphone :(", Toast.LENGTH_LONG ).show();
//	        	Log.e(TAG, "Exception when instantiating SoundAnalyzer: " + e.getMessage());
//	        }
////            soundAnalyzer.addObserver(uiController);
//        }


        guitar = (ImageView)findViewById(R.id.guitar);
        //tune = (ImageView)findViewById(R.id.tune);
        mainMessage = (TextView)findViewById(R.id.mainMessage);
        tuningSelector = (Spinner)findViewById(R.id.tuningSelector);
        Tuning.populateSpinner(this, tuningSelector);
        tuningSelector.setOnItemSelectedListener(uiController);
    }
	
	private Map<Integer, Bitmap> preloadedImages;
	private BitmapFactory.Options bitmapOptions;
	
	private Bitmap getAndCacheBitmap(int id) {
		if(preloadedImages == null)
			preloadedImages = new HashMap<Integer,Bitmap>();
		Bitmap ret = preloadedImages.get(id);
		if(ret == null) {
			if(bitmapOptions == null) {
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize = 4; // The higher it goes, the smaller the image.
			}
			ret = BitmapFactory.decodeResource(getResources(), id, bitmapOptions);
			preloadedImages.put(id, ret);
		}
		return ret;
	}
	
	public void dumpArray(final double [] inputArray, final int elements) {
		Log.d(TAG, "Starting File writer thread...");
		final double [] array = new double[elements];
		for(int i=0; i<elements; ++i)
			array[i] = inputArray[i];
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { // catches IOException below
					// Location: /data/data/your_project_package_structure/files/samplefile.txt         
					String name = "Chart_" + (int)(Math.random()*1000) + ".data";
					FileOutputStream fOut = openFileOutput(name,
							Context.MODE_WORLD_READABLE);
					OutputStreamWriter osw = new OutputStreamWriter(fOut); 

					// Write the string to the file
					for(int i=0; i<elements; ++i) 
							osw.write("" + array[i] + "\n");
					/* ensure that everything is
					 * really written out and close */
					osw.flush();
					osw.close();
					Log.d(TAG, "Successfully dumped array in file " + name);
				} catch(Exception e) {
					Log.e(TAG,e.getMessage());
				}
			}
		}).start();
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(ConfigFlags.menuKeyCausesAudioDataDump) {
		    if (keyCode == KeyEvent.KEYCODE_MENU) {
		        Log.d(TAG,"Menu button pressed");
		        Log.d(TAG,"Requesting audio data dump");
		        soundAnalyzer.dumpAudioDataRequest();
		        return true;
		    }
		}
	    return false;
	}
	
	private int [] stringNumberToImageId = new int[]{
            R.drawable.stringsreduced,
            R.drawable.stringsreduced,
            R.drawable.stringsreduced,
            R.drawable.stringsreduced,
            R.drawable.stringsreduced,
            R.drawable.stringsreduced,
			R.drawable.stringsreduced
			//R.drawable.strings0,
			//R.drawable.strings1,
			//R.drawable.strings2,
			//R.drawable.strings3,
			//R.drawable.strings4,
			//R.drawable.strings5,
			//R.drawable.strings6
	};
	

	
	
    int oldString = 0;
    // 1-6 strings (ascending frequency), 0 - no string.
    public void changeString(int stringId) {
    	if(oldString!=stringId) {
    		guitar.setImageBitmap(getAndCacheBitmap(stringNumberToImageId[stringId]));
        	oldString=stringId;
    	}
    }
    
	int [] targetColor =         new int[]{160,80,40};
	int [] awayFromTargetColor = new int[]{160,160,160};

    
//    public void coloredGuitarMatch(double match) {
//        tune.setBackgroundColor(
//        		Color.rgb((int)(match*targetColor[0]+ (1-match)*awayFromTargetColor[0]),
//        				  (int)(match*targetColor[1]+ (1-match)*awayFromTargetColor[1]),
//        				  (int)(match*targetColor[2]+ (1-match)*awayFromTargetColor[2])));
//
//    }
    
    public void displayMessage(String msg, boolean positiveFeedback) {
    	int textColor = positiveFeedback ? Color.rgb(34,139,34) : Color.rgb(255,36,0);
    	mainMessage.setText(msg);
    	mainMessage.setTextColor(textColor);
    }
    
    


	@Override
	protected void onPause() {
		super.onPause();
        Log.d(TAG,"onPause()");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
        Log.d(TAG,"onRestart()");

	}

	@Override
	protected void onResume() {
		super.onResume();
        Log.d(TAG,"onResume()");
        if(soundAnalyzer!=null) {
            Log.i("soundAna", "not null");
            soundAnalyzer.ensureStarted();
        }
	}

	@Override
	protected void onStart() {
		super.onStart();
        Log.d(TAG,"onStart()");
        if(soundAnalyzer!=null)
        	soundAnalyzer.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
        Log.d(TAG,"onStop()");
        if(soundAnalyzer!=null)
        	soundAnalyzer.stop();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()");

    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.gestureObject.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	class LearnGesture extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			if(e2.getX() > e1.getX()) {

				Intent intent = new Intent(SimpleGuitarTunerActivity.this, MetronomeActivity.class);
				finish();
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_left_right, R.anim.slide_out_left_right);

			} else
			if(e2.getX() < e1.getX()) {
				//Intent intent = new Intent(BPMActivity.this, MetronomeA.class);
				Intent intent = new Intent(SimpleGuitarTunerActivity.this, BPMActivity.class);
				finish();
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right_left, R.anim.slide_out_right_left);
			}
			return true;
		}
	}
}