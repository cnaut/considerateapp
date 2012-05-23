package com.pinokia.considerateapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;

public class StatsService extends Service {

	private boolean initialized = false;
	private boolean active = false;
	private static boolean userPresent = true;
	// This is a bit of a hacky way to see if the service is running, but from
	// sources, it looks like it's the best way to do it.
	protected static boolean running = false;

	private static final int numDays = 5; // num days to collect info for
	ArrayList<Stats> sendDataQueue;
	String prevStats = "";

	// Timers
	private static Timer dailyTimer = new Timer();
	private static final long dailyDelay = ConsiderateAppActivity.testing ? 5 * 60 * 1000
			: 24 * 60 * 60 * 1000;
	private static Timer sendDataTimer = new Timer();
	private static final long sendDataDelay = ConsiderateAppActivity.testing ? 1 * 60 * 1000
			: 60 * 60 * 1000;

	// Top Apps
	private static TreeMap<String, Double> appsMap = new TreeMap<String, Double>();
	private ActivityManager am;
	private PackageManager pack;
	private Timer topAppsTimer = new Timer();
	private static final int topAppsDelay = 5 * 1000; // 5 seconds
	private static final int topAppsElapsed = topAppsDelay / 1000;

	// Total Time
	private static StopWatch stopwatch = new StopWatch();
	private static ArrayList<Long> totalTime;

	// NumUnlocks
	private static ArrayList<Integer> numScreenViews;
	private static ArrayList<Integer> numUnlocks;
	private Intent updateUIIntent;
	public static final String BROADCAST_ACTION = "com.pinokia.considerateapp.updateUI";

	/*
	 * ========================================================================
	 * Data access functions
	 * ========================================================================
	 */

	public static ArrayList<Integer> getNumScreenViews() {
		return numScreenViews;
	}

	public static ArrayList<Integer> getNumUnlocks() {
		return numUnlocks;
	}

	public static StopWatch getStopWatch() {
		return stopwatch;
	}

	public static ArrayList<Long> getTotalTime() {
		long time = ConsiderateAppActivity.testing ? stopwatch.getTotalTime() / 1000 : stopwatch.getTotalTime() / 1000 / 60;
		if (totalTime != null)
			totalTime.set(totalTime.size() - 1, time);
		return totalTime;
	}

	public static TreeMap<String, Double> getAppsMap() {
		return appsMap;
	}

	/*
	 * ========================================================================
	 * Timer tasks
	 * ========================================================================
	 */

	class topAppsTask extends TimerTask {
		public void run() {

			if (userPresent) {

				int numberOfTasks = 1;
				String packageName = am.getRunningTasks(numberOfTasks).get(0).topActivity
						.getPackageName();
				String appName = "";

				try {
					appName = (String) pack.getApplicationLabel(pack
							.getApplicationInfo(packageName,
									PackageManager.GET_META_DATA));
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				if (appsMap.containsKey(appName)) { // already present
					double currValue = appsMap.get(appName);
					appsMap.put(appName, currValue + topAppsElapsed);
				} else {
					appsMap.put(appName, (double) 5);
				}
			}
		}
	}

	class dailyUpdateTask extends TimerTask {
		public void run() {
			// Update unlocks
			numScreenViews.remove(0);
			numScreenViews.add(0);
			numUnlocks.remove(0);
			numUnlocks.add(0);
			sendBroadcast(updateUIIntent);

			// Update total time
			long time = ConsiderateAppActivity.testing ? stopwatch.getTotalTime() / 1000 : stopwatch.getTotalTime() / 1000 / 60;
			totalTime.set(totalTime.size() - 1, time);
			totalTime.remove(0);
			totalTime.add((long) 0);
			stopwatch.setTotalTime(0);

			// Update top apps
			appsMap.clear();
		}
	}

	private void sendData() {
		Stats stats = new Stats(System.currentTimeMillis(),
				numUnlocks.get(numDays - 1), numScreenViews.get(numDays - 1),
				stopwatch.getTotalTime(), appsMap);
		sendDataQueue.add(stats);

		TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String uid = tManager.getDeviceId();
		String json = "{ \"id\":" + uid + ", \"data\":"
				+ Stats.toJsonString(sendDataQueue, prevStats) + " }";
		System.out.println(json);

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://184.169.136.30/batchstats");
		try {
			StringEntity content = new StringEntity(json);
			content.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			httpPost.setEntity(content);

			// Execute HTTP Post Request
			HttpResponse response = httpClient.execute(httpPost);
			if (response.getEntity().getContent().toString() == "success") {
				// only clear data if successfully sent to server
				sendDataQueue.clear();
				prevStats = "";
			}

		} catch (Exception e) {
			// Auto-generated catch block
		}
	}

	class sendDataTask extends TimerTask {
		public void run() {
			sendData();
		}
	}

	/*
	 * ========================================================================
	 * StatsService-related functions
	 * ========================================================================
	 */

	// This makes the running variable read-only.
	public static boolean isRunning() {
		return running;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(getClass().getSimpleName(), "onBind()");
		return null; // we don't bind
	}

	// Code to toggle the service
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
		Intent serviceIntent = new Intent(context, StatsService.class);
		if (!isRunning())
			context.startService(serviceIntent);
	}

	public static void stop(Context context) {
		Intent serviceIntent = new Intent(context, StatsService.class);
		if (isRunning())
			context.stopService(serviceIntent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		running = true;

		sendDataQueue = new ArrayList<Stats>(10);

		// Set up arrays for NumUnlocksFragment and TotalTimeFragment
		numScreenViews = new ArrayList<Integer>(numDays);
		numUnlocks = new ArrayList<Integer>(numDays);
		totalTime = new ArrayList<Long>(numDays);

		updateUIIntent = new Intent(BROADCAST_ACTION);

		for (int i = 0; i < numDays; i++) {
			numScreenViews.add(0);
			numUnlocks.add(0);
			totalTime.add((long) 0);
		}

		// Set up stats for Top Apps Fragment
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		pack = getPackageManager();
		topAppsTimer.scheduleAtFixedRate(new topAppsTask(), 0, topAppsDelay);

		// Load previously stored data that hasn't been sent yet
		SharedPreferences storage = getSharedPreferences(ConsiderateAppActivity.prefsName, 0);
		prevStats = storage.getString("prevStats", "");

		Calendar firstExecutionDate = new GregorianCalendar();

		if (ConsiderateAppActivity.testing) {
			firstExecutionDate.set(Calendar.SECOND, 0);
			firstExecutionDate.add(Calendar.MINUTE, 1);
			dailyTimer.scheduleAtFixedRate(new dailyUpdateTask(),
					firstExecutionDate.getTime(), dailyDelay);

			firstExecutionDate.add(Calendar.SECOND, -5);
			sendDataTimer.scheduleAtFixedRate(new sendDataTask(),
					firstExecutionDate.getTime(), sendDataDelay);
		} else {

			firstExecutionDate.set(Calendar.SECOND, 0);
			firstExecutionDate.set(Calendar.MINUTE, 0);
			firstExecutionDate.add(Calendar.HOUR_OF_DAY, 1);
			firstExecutionDate.add(Calendar.SECOND, -5);
			sendDataTimer.scheduleAtFixedRate(new sendDataTask(),
					firstExecutionDate.getTime(), sendDataDelay);

			firstExecutionDate.set(Calendar.SECOND, 0);
			firstExecutionDate.set(Calendar.HOUR_OF_DAY, 0);
			firstExecutionDate.add(Calendar.DAY_OF_MONTH, 1);
			dailyTimer.scheduleAtFixedRate(new dailyUpdateTask(),
					firstExecutionDate.getTime(), dailyDelay);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		running = false;
		stopReceivers();
		dailyTimer.cancel();
		sendDataTimer.cancel();

		// Try to send data or save to phone to be sent at a later time
		sendData();
		if (sendDataQueue.size() != 0) {
			prevStats = Stats.toJsonString(sendDataQueue, prevStats);
		}
		SharedPreferences storage = getSharedPreferences(ConsiderateAppActivity.prefsName, 0);
		SharedPreferences.Editor editor = storage.edit();
		editor.putString("prevStats", prevStats);
		editor.commit();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (initialized) {
			return 1;
		}
		Log.v("onStartCommand", "SleepMonitorService started!");

		startReceivers();
		initialized = true;
		return 1;
	}

	void startReceivers() {
		if (active)
			return;
		IntentFilter onFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		IntentFilter offFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		IntentFilter unlockFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);

		registerReceiver(screenOn, onFilter);
		registerReceiver(screenOff, offFilter);
		registerReceiver(screenUnlocked, unlockFilter);

		active = true;

	}

	void stopReceivers() {
		unregisterReceiver(screenOn);
		unregisterReceiver(screenOff);
		unregisterReceiver(screenUnlocked);
	}

	/*
	 * ========================================================================
	 * Broadcast Receivers
	 * ========================================================================
	 */

	BroadcastReceiver screenOn = new BroadcastReceiver() {
		public static final String OnIntent = "android.intent.action.SCREEN_ON";

		@Override
		public void onReceive(Context context, Intent intent) {

			if (!intent.getAction().equals(OnIntent))
				return;

			int index = numScreenViews.size() - 1;
			Integer currNumScreenViews = numScreenViews.get(index);
			numScreenViews.set(index, currNumScreenViews + 1);

			// Save num unlocks for phone score
			SharedPreferences savedData = getSharedPreferences(
					"considerateapp", 0);
			SharedPreferences.Editor dataEdit = savedData.edit();
			dataEdit.putInt("numScreenViews", numScreenViews.get(index));
			dataEdit.commit();

			System.out.println("NumScreenViews: " + numScreenViews.get(index));
			return;
		}
	};

	BroadcastReceiver screenOff = new BroadcastReceiver() {
		public static final String OffIntent = "android.intent.action.SCREEN_OFF";

		@Override
		public void onReceive(Context context, Intent intent) {

			if (!intent.getAction().equals(OffIntent))
				return;

			userPresent = false;
			stopwatch.stop();
			return;
		}
	};

	BroadcastReceiver screenUnlocked = new BroadcastReceiver() {
		public static final String UnlockIntent = "android.intent.action.USER_PRESENT";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(UnlockIntent))
				return;
			stopwatch.start();
			userPresent = true;

			int index = numUnlocks.size() - 1;
			Integer currNumUnlocks = numUnlocks.get(index);
			numUnlocks.set(index, currNumUnlocks + 1);

			System.out.println("NumUnlocks: " + numUnlocks.get(index));
			sendBroadcast(updateUIIntent);
			return;
		}
	};

}
