package com.pinokia.considerateapp;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.pinokia.considerateapp.TotalTimeFragment.timerSecondTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class StatsService extends Service {
	
	

	//Timers
	private static Timer dailyTimer = new Timer();
	// long delay = 86400 * 1000; //number of millisec in 24 hours
	private static long dailyDelay = 5 * 60 * 1000; // number of millisec in 1 minute
	
	//Top Apps
	static HashMap<String, Double> appsMap = new HashMap<String, Double>();
	static int numTopApps = 5;
	ValueComparator bvc;
	//ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	//PackageManager pack = getPackageManager();
	
	//Total Time
	static double max_TotalTime = 10;
	static double tMinus5_tt = 0;
	static double tMinus4_tt = 0;
	static double tMinus3_tt = 0;
	static double tMinus2_tt = 0;
	static double tMinus1_tt = 0;
	
	//NumUnlocks
	static double max = 5;
	// Power Check
	static double tMinus5_pc = 0;
	static double tMinus4_pc = 0;
	static double tMinus3_pc = 0;
	static double tMinus2_pc = 0;
	static double tMinus1_pc = 0;
	// Num Unlock
	static double tMinus5_nu = 0;
	static double tMinus4_nu = 0;
	static double tMinus3_nu = 0;
	static double tMinus2_nu = 0;
	static double tMinus1_nu = 0;

	// This is a bit of a hacky way to see if the service is running, but from
	// sources, it looks like it's the best way to do it.
	protected static boolean running = false;

	// This makes the running variable read-only.
	public static boolean isRunning() {
		return running;
	}

	private static int numPowerChecks = 0;
	private static int numLocks = 0;
	private static boolean userPresent = true;
	private static StopWatch stopwatch = new StopWatch();

	/*
	protected static void saveData() {
		editor.putInt("numLocks", numLocks);
		editor.putInt("numPowerChecks", numPowerChecks);
		editor.commit();
	}*/

	public static double get_tMinus5_tt() {
		return tMinus5_tt;
	}
	
	public static double get_tMinus4_tt() {
		return tMinus4_tt;
	}
	
	public static double get_tMinus3_tt() {
		return tMinus3_tt;
	}
	
	public static double get_tMinus2_tt() {
		return tMinus2_tt;
	}
	
	public static double get_tMinus1_tt() {
		return tMinus1_tt;
	}
	
	public static double get_tMinus5_pc() {
		return tMinus5_pc;
	}
	
	public static double get_tMinus4_pc() {
		return tMinus4_pc;
	}
	
	public static double get_tMinus3_pc() {
		return tMinus3_pc;
	}
	
	public static double get_tMinus2_pc() {
		return tMinus2_pc;
	}
	
	public static double get_tMinus1_pc() {
		return tMinus1_pc;
	}
	
	public static double get_tMinus5_nu() {
		return tMinus5_nu;
	}
	
	public static double get_tMinus4_nu() {
		return tMinus4_nu;
	}
	
	public static double get_tMinus3_nu() {
		return tMinus3_nu;
	}
	
	public static double get_tMinus2_nu() {
		return tMinus2_nu;
	}
	
	public static double get_tMinus1_nu() {
		return tMinus1_nu;
	}
	
	public static double get_max_TotalTime() {
		return max_TotalTime;
	}
	
	public static int getNumTopApps() {
		return numTopApps;
	}
	
	public static HashMap<String, Double> getAppsMap() {
		return appsMap;
	}
	
	public static double getMax() {
		return max;
	}
	
	public static int getNumLocks() {
		//ensureStatsLoaded();
		return numLocks;
	}

	public static int getNumPowerChecks() {
		//ensureStatsLoaded();
		return numPowerChecks;
	}

	public static boolean getUserPresent() {
		//ensureStatsLoaded();
		return userPresent;
	}

	public static StopWatch getStopWatch() {
		//ensureStatsLoaded();
		return stopwatch;
	}

	public static void setNumLocks(int newNumLocks) {
		//ensureStatsLoaded();
		numLocks = newNumLocks;
		//saveData();
	}

	public static void setNumPowerChecks(int newNumPowerChecks) {
		//ensureStatsLoaded();
		numPowerChecks = newNumPowerChecks;
		//saveData();
	}
	
	public static void set_tMinus1_nu(double newDouble) {
		tMinus1_nu = newDouble;
	}
	
	public static void set_tMinus1_pc(double newDouble) {
		tMinus1_pc = newDouble;
	}
	
	public static void set_tMinus1_tt(double newDouble) {
		tMinus1_tt = newDouble;
	}
	
	public static void setMax(double newDouble) {
		max = newDouble;
	}
	
	public static void setMaxTotalTime(double newMax) {
		max_TotalTime = newMax;
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
	
	//TIMERR
	
	
	class timerDailyTask extends TimerTask {
		public void run() {
			dailyUpdateUnlocks();
			dailyUpdateTotalTime();
			dailyUpdateTopApps();
		}
	}
	
	public static void dailyUpdateUnlocks() { 
		// Power Checks
		tMinus5_pc = tMinus4_pc;
		tMinus4_pc = tMinus3_pc;
		tMinus3_pc = tMinus2_pc;
		tMinus2_pc = StatsService.getNumPowerChecks();;
		tMinus1_pc = 0;

		// Num Unlocks
		tMinus5_nu = tMinus4_nu;
		tMinus4_nu = tMinus3_nu;
		tMinus3_nu = tMinus2_nu;
		tMinus2_nu = StatsService.getNumLocks();;
		tMinus1_nu = 0;

		StatsService.setNumPowerChecks(0);
		StatsService.setNumLocks(0);
		
		UnlocksFragment.update();

		//System.out.println("Unlocks day passed");

	}
	
	public void dailyUpdateTotalTime() { 
		tMinus5_tt = tMinus4_tt;
		tMinus4_tt = tMinus3_tt;
		tMinus3_tt = tMinus2_tt;
		tMinus2_tt = getStopWatch().getTotalTime();
		tMinus1_tt = 0;
		getStopWatch().setTotalTime(0);
		
		//double timeSpentSeconds = timeSpentMillis / 1000.00;
		if (getStopWatch().getTotalTime() > max_TotalTime)
			max_TotalTime = getStopWatch().getTotalTime();
	 
		//System.out.println("AFTER: "+ stopwatch.getTotalTime());
		
		System.out.println("Total Time day passed");
	}
	
	
	public void dailyUpdateTopApps() {
		HashMap<String, Double> tempMap = appsMap;
		bvc = new ValueComparator(tempMap);
		TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
		
		sorted_map.putAll(tempMap);

		String plotPointsApps = "";
		String plotPointsTime = "";
		double max = -1;

		int size = sorted_map.size();
		if (size > numTopApps) {
			size = numTopApps;
		}

		while (size > 0) {
			for (String key : sorted_map.keySet()) {
				// System.out.println("key/value: " + key +
				// "/"+sorted_map.get(key));
				double value = sorted_map.get(key);
				if (value > max)
					max = value;

				if (size == 1) {
					plotPointsApps = plotPointsApps + key;
					plotPointsTime = plotPointsTime + value;
				} else {
					plotPointsApps = plotPointsApps + key + "|";
					plotPointsTime = plotPointsTime + value + ",";
				}
				size -= 1;
			}
		}
		String graphString = "<img src='http://2.chart.apis.google.com/chart?"
				+ "chf=bg,s,67676700|c,s,67676700" // transparent background
				+ "&chs=" + TopAppsFragment.chartWidth + "x" + TopAppsFragment.chartHeight // chart size
				+ "&cht=p" // chart type
				+ "&chco=58D9FC,EE58FC" // slice colors
				+ "&chds=0," + max // range
				+ "&chd=t:" + plotPointsTime // data
				+ "&chdl=" + plotPointsApps + "' />"; // labels
		TopAppsFragment.setGraphString(graphString);

		appsMap.clear();

		System.out.println("Top Apps day passed");
	}
	
	/*
	class ValueComparator implements Comparator<Object> {

		Map<String, Double> base;

		public ValueComparator(Map<String, Double> base) {
			this.base = base;
		}

		public int compare(Object a, Object b) {

			if ((Double) base.get(a) < (Double) base.get(b)) {
				return 1;
			} else if ((Double) base.get(a) == (Double) base.get(b)) {
				return 0;
			} else {
				return -1;
			}
		}
	}*/

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(getClass().getSimpleName(), "onBind()");
		return null;// we don't bind
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
		
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);
		int day = now.get(Calendar.DAY_OF_MONTH);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int min = now.get(Calendar.MINUTE);
		int sec = now.get(Calendar.SECOND);
		
		System.out.println("Hour: " + hour 
				+ "Minute: " + min
				+ "Second: " + sec);
		
		Calendar firstExecutionDate = new GregorianCalendar();
		
		//For mock testing; starts at top of every minute.  Comment out the next three lines of code.
		firstExecutionDate.set(Calendar.HOUR_OF_DAY, hour);
		firstExecutionDate.set(Calendar.MINUTE, min+1); //rollover to next minute
		firstExecutionDate.set(Calendar.SECOND, 0); //start at top of minute
		
		//For Release into the real world; Uncomment out this block!
		/*
		firstExecutionDate.set(Calendar.YEAR, year);
		firstExecutionDate.set(Calendar.MONTH, month);
		firstExecutionDate.set(Calendar.DAY_OF_MONTH, day+1);
		firstExecutionDate.set(Calendar.HOUR_OF_DAY, 0); //start at top of day.
		firstExecutionDate.set(Calendar.MINUTE, 0); 
		firstExecutionDate.set(Calendar.SECOND, 0);
		*/
		dailyTimer.schedule(new timerDailyTask(), firstExecutionDate.getTime(), dailyDelay);
		//
		dailyUpdateUnlocks();
		//dailyUpdateTotalTime();
		dailyUpdateTopApps();
		
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		running = false;
		stopReceivers();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (initialized) {
			return 1;
		}
		Log.v("onStartCommand", "SleepMonitorService started!");

		//ensureStatsLoaded();

		startReceivers();
		initialized = true;
		return 1;
	}

	protected static void ensureStatsLoaded() {
		if (statsLoaded || context == null)
			return;

		savedData = context.getSharedPreferences("considerateapp", 0);
		editor = savedData.edit();

		numLocks = savedData.getInt("numLocks", 0);
		numPowerChecks = savedData.getInt("numPowerChecks", 0);
	}

	void startReceivers() {
		if (active)
			return;
		IntentFilter onFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		IntentFilter offFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		IntentFilter unlockFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
		// IntentFilter battfilter = new
		// IntentFilter(Intent.ACTION_SCREEN_CHANGED);

		registerReceiver(screenOn, onFilter);
		registerReceiver(screenOff, offFilter);
		registerReceiver(screenUnlocked, unlockFilter);
		// registerReceiver(battchange, battfilter);

		active = true;
		
		
	}

	void stopReceivers() {
		unregisterReceiver(screenOn);
		unregisterReceiver(screenOff);
		unregisterReceiver(screenUnlocked);
	}

	BroadcastReceiver screenOn = new BroadcastReceiver() {
		public static final String TAG = "screenOn";
		public static final String OnIntent = "android.intent.action.SCREEN_ON";

		@Override
		public void onReceive(Context context, Intent intent) {

			if (!intent.getAction().equals(OnIntent))
				return;

			numPowerChecks++;
			System.out.println("NumPowerChecks: " + numPowerChecks);
			//saveData();
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

			userPresent = false;
			stopwatch.stop();
			//saveData();
			return;
		}
	};

	BroadcastReceiver screenUnlocked = new BroadcastReceiver() {



		public static final String TAG = "screenUnlocked";
		public static final String UnlockIntent = "android.intent.action.USER_PRESENT";

		@Override
		public void onReceive(Context context, Intent intent) {
			if(!intent.getAction().equals(UnlockIntent)) return;
			stopwatch.start();
			userPresent = true;

			numLocks++;
			System.out.println("NumLocks: " + numLocks);
			//saveData();
			UnlocksFragment.update();
			return;
		}
	};
	
}