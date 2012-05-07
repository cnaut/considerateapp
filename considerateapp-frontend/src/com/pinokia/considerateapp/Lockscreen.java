package com.pinokia.considerateapp;

import android.app.Activity;
import android.os.Bundle;
import android.content.BroadcastReceiver;

public class Lockscreen extends Activity{
	//MAKE OUR LOCKSCREEN HERE!
		
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		// getWindow.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}
}