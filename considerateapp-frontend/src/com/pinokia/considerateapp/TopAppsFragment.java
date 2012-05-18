package com.pinokia.considerateapp;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class TopAppsFragment extends Fragment {

	// global variables
	WebView wv;
	TextView text;
	
	static int chartWidth = 500;// = 400;
	static int chartHeight = 220;// = 240;

	ActivityManager am;
	PackageManager pack;
	int numTopApps = 5;

	Timer dailyTimer = new Timer();
	long dailyDelay = 60 * 1000; // number of millisec in 1 minute

	Timer pollTimer = new Timer();
	int pollDelay = 5 * 1000; // 5 seconds
	int pollElapsed = pollDelay / 1000;

	HashMap<String, Double> appsMap;

	String graphString = "<img src='http://2.chart.apis.google.com/chart?"
			+ "chf=bg,s,67676700|c,s,67676700" // transparent background
			+ "&chs=" + chartWidth + "x" + chartHeight // chart size
			+ "&cht=p" // chart type
			+ "&chco=58D9FC,EE58FC" // slice colors
			+ "&chds=0,942" // range
			+ "&chd=t:200,842,942,432,594" // chart data
			+ "&chdl=App+1|App+2|App+3|App+4+|App+5&chdlp=l' />"; // chart labels

	class timerPollTask extends TimerTask {
		public void run() {

			if (StatsService.getUserPresent()) {
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
					appsMap.put(appName, currValue + pollElapsed);
				} else {
					appsMap.put(appName, (double) 5);
				}

				System.out.println(appsMap);
			}
		}
	}

	class timerDailyTask extends TimerTask {
		public void run() {

			HashMap<String, Double> tempMap = appsMap;
			ValueComparator bvc = new ValueComparator(tempMap);
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
			graphString = "<img src='http://2.chart.apis.google.com/chart?"
					+ "chf=bg,s,67676700|c,s,67676700" // transparent background
					+ "&chs=" + chartWidth + "x" + chartHeight // chart size
					+ "&cht=p" // chart type
					+ "&chco=58D9FC,EE58FC" // slice colors
					+ "&chds=0," + max // range
					+ "&chd=t:" + plotPointsTime // data
					+ "&chdl=" + plotPointsApps + "' />"; // labels

			appsMap.clear();

			System.out.println("finished loading graph: Top Apps");
		}
	}

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
	}

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = inflater.inflate(R.layout.stats_layout, container, false);
		text = (TextView) view.findViewById(R.id.text);
		wv = (WebView) view.findViewById(R.id.graph);
		wv.setBackgroundColor(0);
		
		am = (ActivityManager) getActivity().getSystemService(
				Context.ACTIVITY_SERVICE);
		pack = getActivity().getPackageManager();
		appsMap = new HashMap<String, Double>();

		pollTimer.schedule(new timerPollTask(), 0, pollDelay);
		dailyTimer.schedule(new timerDailyTask(), 0, dailyDelay);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		setUserVisibleHint(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		System.out.println("OnPause: TopApps");
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("OnResume: TopApps");

		text.setText("Here are the apps you've spent the most time on today:");
		wv.loadData(graphString, "text/html", "UTF-8");
	}
}
