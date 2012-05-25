package com.pinokia.considerateapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class FirstBootService extends Service {
	// this will be started when myLock is shut off due to idle timeout, or at
	// bootup it starts everything up once we receive user present broadcast
	// (meaning the lockscreen was completed) just bridges the gap between an
	// idle timeout or first startup and the user authentication of their
	// pattern

	// TODO possible that pattern setting won't be able to get set back to on if
	// a battery pull is done before idle timer finishes?

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(getClass().getSimpleName(), "onBind()");
		return null;// we don't bind
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(getClass().getSimpleName(), "onDestroy()");

		unregisterReceiver(unlockdone);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		IntentFilter userunlock = new IntentFilter(Intent.ACTION_USER_PRESENT);

		registerReceiver(unlockdone, userunlock);

		return 1;
	}

	BroadcastReceiver unlockdone = new BroadcastReceiver() {

		public static final String present = "android.intent.action.USER_PRESENT";

		@Override
		public void onReceive(Context context, Intent intent) {
			SharedPreferences settings = getSharedPreferences(
				ConsiderateAppActivity.prefsName, 0);
			if (!intent.getAction().equals(present))
				return;
			Log.v("user unlocking", "Keyguard was completed by user");
			// send myLock start intent
			if (settings.getBoolean("lockscreen", false)) {
				SleepMonitorService.start(getApplicationContext(), false);
			}
			if (settings.getBoolean("considerate_mode", false)) {
				FlipService.start(getApplicationContext(), false);
			}
			StatsService.start(getApplicationContext());
			stopSelf();
			return;

		}
	};
}
