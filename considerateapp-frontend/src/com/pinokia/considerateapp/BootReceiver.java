package com.pinokia.considerateapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
//launch a startup service which will read prefs
//user can opt not to start at boot but we must check that from a service
	public static final String TAG = "BootReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
	  // just make sure we are getting the right intent (better safe than sorry)
		if( "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			ComponentName comp = new ComponentName(context.getPackageName(), FirstBootService.class.getName());
			ComponentName service = context.startService(new Intent().setComponent(comp));
			Log.v("boot_complete","The service loaded at boot!");
			if (service == null){
	    // something really wrong here
				Log.e(TAG, "Could not start service " + comp.toString());
			}
		} else {
			Log.e(TAG, "Received unexpected intent " + intent.toString());   
		}
	}
}