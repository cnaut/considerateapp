package com.pinokia.considerateapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class Lockscreen extends Activity {
	public int timeleft = 0;
	public SharedPreferences savedData;
    public boolean starting = true;//flag off after we successfully gain focus. flag on when we send task to back
    public boolean waking = false;//any time quiet or active wake are up
    public boolean finishing = false;//flag on when an event causes unlock, back off when onStart comes in again (relocked)
    
    public boolean paused = false;
    
    public boolean shouldFinish = false;
   
    public boolean screenwake = false;//set true when a wakeup key or external event turns screen on
    
    public WebView wv;
    
    public int numLocks;
    public int numPowerChecks;
    
    
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

    	wv = new WebView(this);
        savedData = getSharedPreferences("considerateapp", 0);
   

	}
	
	protected void onStart() {
		super.onStart();
	 	numLocks = savedData.getInt("numLocks", 0);
    	numPowerChecks = savedData.getInt("numPowerChecks", 0);
        
    	String phoneUnlockText = "<body bgcolor = 'black'><font color= 'white' size = 5><center>You have checked <br />your phone " + numPowerChecks + " times <br /> and unlocked your phone " + numLocks + " times today</center></font></body> <br /> <br />";
    	String data = phoneUnlockText;
        wv.loadData(data, "text/html", "UTF-8");
        setContentView(wv);  
	}
	
    @Override
    public void onBackPressed() {
    	if (screenwake) {
    		finishing = true;
    		moveTaskToBack(true);
        }
        return;
    }
    
    BroadcastReceiver screenoff = new BroadcastReceiver() {
        public static final String Screenoff = "android.intent.action.SCREEN_OFF";

        @Override
        public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(Screenoff)) return;       
                
	        if (screenwake && hasWindowFocus()) {
	    
	        	screenwake = false;
	        	setBright((float) 0.0);
	        	}
	        else if (waking) {
	        	shouldFinish=true;
	        	}
	        waking = false; //reset lifecycle
	        
	        return;//avoid unresponsive receiver error outcome    
	    }
    };
	
    public void wakeup() {
    	setBright((float) 0.1);//tell screen to go on with 10% brightness
    	PowerManager myPM = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
  	  	myPM.userActivity(SystemClock.uptimeMillis(), false);
  	  	
    	screenwake = true;
    	timeleft = 0;//this way the task doesn't keep going     	  	
    }
    
    public void setBright(float value) {
    	Window mywindow = getWindow();
    	
    	WindowManager.LayoutParams lp = mywindow.getAttributes();

		lp.screenBrightness = value;

		mywindow.setAttributes(lp);
    }
   
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        boolean up = event.getAction() == KeyEvent.ACTION_UP;
        int code = event.getKeyCode();
        Log.v("dispatching a key event","Is this the up? -" + up);
        
        int reaction = 1;//wakeup, the preferred behavior in advanced mode
                
        if (code == KeyEvent.KEYCODE_BACK) reaction = 3;//check for wake, if yes, exit
        else if (code == KeyEvent.KEYCODE_POWER) reaction = 2;//unlock
        else if (code == KeyEvent.KEYCODE_FOCUS) reaction = 0;//locked (advanced power save)
       
        switch (reaction) {
        	case 3:
        		onBackPressed();
        		return true;
        	case 2:
        		if (up && !finishing) {
        			Log.v("unlock key","power key UP, unlocking");
        			finishing = true;
	    	  	  	
	    		   setBright((float) 0.1);
	    		       		       		  
	    		   moveTaskToBack(true);
    		  
        		}
        		return true;
       
        	case 1:
        		if (up && !screenwake) {
        			waking = true;
                  	Log.v("key event","wake key");
                  	wakeup();
        		}
        		return true;
       
        	case 0:    	   
        		if (!screenwake && up) {
        			timeleft=10;
         	
        			if (!waking) {
        				Log.v("key event","locked key timer starting");

        				waking = true;
        				//serviceHandler.postDelayed(myTask, 500L);
        			}
        		}
            
        		return true;
        }
        return false;
    }
}