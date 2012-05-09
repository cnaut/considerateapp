package com.pinokia.considerateapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class LockScreenActivity extends Activity implements OnClickListener, OnTouchListener {
	String tag = "LOCK SCREEN";
	HorizontalScrollView lock;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lockscreen);
		
		TextView phoneScore = (TextView) findViewById(R.id.phoneScore);
        phoneScore.setOnClickListener(this);
        
        lock = (HorizontalScrollView) findViewById(R.id.lock);
        lock.postDelayed(new Runnable() {
            public void run() {
                lock.scrollTo(435, 0);
            }
        }, 250);
        lock.setOnTouchListener(this);
	}
	
	// Implement the OnClickListener callback
    public void onClick(View v) {
      // do something when the button is clicked
    	if (v.getId() == R.id.phoneScore) {
        	Log.i(tag, "phone score clicked");
    		Intent intent = new Intent(this, ConsiderateAppActivity.class);
    		startActivity(intent);
    	}
    }
    
    public boolean onTouch(View v, MotionEvent event) {
    	if (v.getId() == R.id.lock) {
    		if (event.getAction() == MotionEvent.ACTION_DOWN ||
    			event.getAction() == MotionEvent.ACTION_MOVE) {
    			if (lock.getScrollX() < 200 || lock.getScrollX() > 700) {
    				Intent startMain = new Intent(Intent.ACTION_MAIN);
    		        startMain.addCategory(Intent.CATEGORY_HOME);
    		        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		        startActivity(startMain);
    			}
    			return false;
    		}
    		if (event.getAction() == MotionEvent.ACTION_UP) {
    			lock.scrollTo(435, 0);
    			return true;
    		}
    	}
    	return false;
	}
    
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
}
