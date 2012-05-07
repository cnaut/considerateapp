package com.pinokia.considerateapp;

import com.pinokia.considerateapp.ConsiderateAppActivity.timerClearTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.content.BroadcastReceiver;

public class Lockscreen extends Activity{
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		// getWindow.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

    	WebView wv = new WebView(this);
        int numPowerChecks = 0;
        int numLocks = 0;
        
    	String phoneUnlockText = "<body bgcolor = 'black'><font color= 'white' size = 5><center>You have checked <br />your phone " + numPowerChecks + " times <br /> and unlocked your phone " + numLocks + " times today</center></font></body> <br /> <br />";
    	String data = phoneUnlockText;
        wv.loadData(data, "text/html", "UTF-8");
        setContentView(wv);  
	}
}