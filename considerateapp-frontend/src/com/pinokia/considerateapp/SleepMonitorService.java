package com.pinokia.considerateapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;

public class SleepMonitorService extends Service {

	// This is a bit of a hacky way to see if the service is running, but from
	// sources, it looks like it's the best way to do it.
	protected static boolean running = false;

	// This makes the running variable read-only.
	public static boolean isRunning() {
		return running;
	}

	private boolean initialized = false;
	private boolean active = false;
	private boolean awake = false;
	Handler serviceHandler;
	Runnable homescreenTask = new Runnable() {
		public void run() {
			Log.v("homescreenTask", "pre launching ze missiles!");
			ManageKeyguard.disableKeyguard(getApplicationContext());
			Intent lockscreen = new Intent(getApplicationContext(),
					Lockscreen.class);
			lockscreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_NO_USER_ACTION);
			getApplicationContext().startActivity(lockscreen);

			Log.v("homescreenTask", "launching ze missiles!");
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(getClass().getSimpleName(), "onBind()");
		return null;// we don't bind
	}

	@Override
	public void onCreate() {
		super.onCreate();
		running = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		initialized = false;
		running = true;
		stopReceivers();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (initialized) {
			return 1;
		}
		Log.v("onStartCommand", "SleepMonitorService started!");

		serviceHandler = new Handler();
		startReceivers();
		initialized = true;
		return 1;
	}

	// Code to toggle the service.
	public static boolean toggleService(Context context) {
		if (isRunning()) {
			stop(context);
			return false;
		} else {
			start(context);
			return true;
		}
	}

	public static void start(Context context) {
		Intent serviceIntent = new Intent(context, SleepMonitorService.class);
		if (!isRunning())
			context.startService(serviceIntent);
	}

	public static void stop(Context context) {
		Intent serviceIntent = new Intent(context, SleepMonitorService.class);
		if (isRunning())
			context.stopService(serviceIntent);
	}

	// Start and stop receivers
	void startReceivers() {
		if (active)
			return;
		IntentFilter onFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		IntentFilter offFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		// IntentFilter battfilter = new
		// IntentFilter(Intent.ACTION_SCREEN_CHANGED);

		registerReceiver(screenOn, onFilter);
		registerReceiver(screenOff, offFilter);
		// registerReceiver(battchange, battfilter);

		active = true;
	}

	void stopReceivers() {
		if (!active)
			return;

		unregisterReceiver(screenOn);
		unregisterReceiver(screenOff);

		active = false;
	}

	BroadcastReceiver screenOn = new BroadcastReceiver() {
		public static final String TAG = "screenOn";
		public static final String OnIntent = "android.intent.action.SCREEN_ON";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(OnIntent))
				return;

			Log.v(TAG, "Screen turned on!");
			awake = true;
			return;
		}
	};

	BroadcastReceiver screenOff = new BroadcastReceiver() {
		public static final String TAG = "screenOff";
		public static final String OffIntent = "android.intent.action.SCREEN_OFF";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(OffIntent))
				return;

			Log.v(TAG, "Screen turned off!");
			awake = false;

			serviceHandler.postDelayed(homescreenTask, 500L);

			return;
		}

	};
}