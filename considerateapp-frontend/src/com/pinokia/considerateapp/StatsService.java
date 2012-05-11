package com.pinokia.considerateapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.provider.Settings.SettingNotFoundException;
import android.os.Handler;
import android.util.Log;

public class StatsService extends Service {

	//This is a bit of a hacky way to see if the service is running, but from sources, it looks like it's the best way to do it.
	protected static boolean running = false;
	//This makes the running variable read-only.
	public static boolean isRunning() {return running;}

	private static int numPowerChecks, numLocks;
	public static int getNumLocks() {
		ensureStatsLoaded();
		return numLocks;
	}
	public static int getNumPowerChecks() {
		ensureStatsLoaded();
		return numPowerChecks;
	}

	public static void initContext(Context aContext) {
		context = aContext;
	}

	public static SharedPreferences savedData;
	public static SharedPreferences.Editor editor;

	private static boolean statsLoaded = false;
	private static Context context;
	private boolean initialized = false;
	private boolean active = false;
	private boolean awake = false;

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(getClass().getSimpleName(), "onBind()");
		return null;//we don't bind
	}

	//Code to toggle the service
	public static boolean toggleService(Context context) {
		if (isRunning())
		{
			stop(context);
			return false;
		} else
		{
			start(context);
			return true;
		}
	}

	public static void start(Context context) {
	    Intent serviceIntent = new Intent(context, StatsService.class);
		if(!isRunning())
			context.startService(serviceIntent);
	}
	public static void stop(Context context) {
	    Intent serviceIntent = new Intent(context, StatsService.class);
		if(isRunning())
			context.stopService(serviceIntent);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		running = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		running = false;
		stopReceivers();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);	

		if (initialized)
		{
			return 1;
		}
		Log.v("onStartCommand", "SleepMonitorService started!");

		ensureStatsLoaded();

		startReceivers();
		initialized=true;
		return 1;
	}	

	protected static void ensureStatsLoaded() {
		if(statsLoaded || context == null)
			return;

    	savedData = context.getSharedPreferences("considerateapp", 0);
    	editor = savedData.edit();
    	
    	numLocks = savedData.getInt("numLocks", 0);
    	numPowerChecks = savedData.getInt("numPowerChecks", 0);
	}

	void startReceivers() {
		if (active) return;
		IntentFilter onFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		IntentFilter offFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		IntentFilter unlockFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
		// IntentFilter battfilter = new IntentFilter(Intent.ACTION_SCREEN_CHANGED);

		registerReceiver(screenOn, onFilter);
		registerReceiver(screenOff, offFilter);
		registerReceiver(screenUnlocked, unlockFilter);
		// registerReceiver(battchange, battfilter);

		active=true;
	}

	void stopReceivers() {
		unregisterReceiver(screenOn);
		unregisterReceiver(screenOff);
		unregisterReceiver(screenUnlocked);
	}

	protected void saveData() {
	    editor.putInt("numLocks", numLocks);
	    editor.putInt("numPowerChecks", numPowerChecks);
	    editor.commit();
	}

	BroadcastReceiver screenOn = new BroadcastReceiver() {
		public static final String TAG="screenOn";
		public static final String OnIntent="android.intent.action.SCREEN_ON";

		@Override
		public void onReceive(Context context, Intent intent) {
			if(!intent.getAction().equals(OnIntent)) return;

			numPowerChecks++;
			saveData();
			return;
		}
	};

	BroadcastReceiver screenOff = new BroadcastReceiver() {
		public static final String TAG="screenOff";
		public static final String OffIntent="android.intent.action.SCREEN_OFF";

		@Override
		public void onReceive(Context context, Intent intent) {
			if(!intent.getAction().equals(OffIntent)) return;

			//Right now we're doing nothing.
			saveData();
			return;
		}
	};

	BroadcastReceiver screenUnlocked = new BroadcastReceiver() {
		public static final String TAG="screenUnlocked";
		public static final String UnlockIntent="android.intent.action.ACTION_USER_PRESENT";

		@Override
		public void onReceive(Context context, Intent intent) {
			if(!intent.getAction().equals(UnlockIntent)) return;

			numLocks++;
			saveData();
			return;
		}
	};
}