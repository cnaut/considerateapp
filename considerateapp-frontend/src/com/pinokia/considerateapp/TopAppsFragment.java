package com.pinokia.considerateapp;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.achartengine.model.CategorySeries;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TopAppsFragment extends Fragment {

	// global variables
	private TextView text;
	private ChartView chart;

	private static final int numTopApps = 5;

	private Timer constantUpdateTimer;
	private static final long constantUpdateDelay = 10 * 1000; // 10 seconds

	class timerConstantUpdateTask extends TimerTask {
		public void run() {
			Activity a = getActivity();
			if (a == null) {
				// do nothing
				// System.out.println("activity is null");
			} else {
				a.runOnUiThread(new Runnable() {
					public void run() {
						update();
					}
				});
			}
		}
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("Top Apps Broadcast Received");
			update();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = inflater.inflate(R.layout.stats_layout, container, false);
		text = (TextView) view.findViewById(R.id.text);
		text.setText("Here are the apps you've spent the most time on today:");
		chart = (ChartView) view.findViewById(R.id.chart);
		chart.setType(ChartView.chartType.PIE);

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

		if (ConsiderateAppActivity.testing) {
			constantUpdateTimer.cancel();
		} else {
			getActivity().unregisterReceiver(broadcastReceiver);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("OnResume: TopApps");
		if (ConsiderateAppActivity.testing) {
			constantUpdateTimer = new Timer();
			constantUpdateTimer.schedule(new timerConstantUpdateTask(), 0,
					constantUpdateDelay);

		} else {
			getActivity().registerReceiver(broadcastReceiver,
					new IntentFilter(Intent.ACTION_TIME_TICK));
			update();
		}
	}

	private void update() {
		TreeMap<String, Double> sorted_map = StatsService.getAppsMap();
		CategorySeries data = new CategorySeries("Top Apps");

		int size = sorted_map.size();
		if (size > numTopApps) {
			size = numTopApps;
		}

		int numAdded = 0;
		for (String key : sorted_map.keySet()) {
			assert (sorted_map != null);
			double value = sorted_map.get(key);
			data.add(key, value);
			numAdded += 1;
			if (numAdded == size)
				break;
		}
		chart.createChart(data);
		chart.invalidate();
		System.out.println("TOP APPS UPDATE");
	}

}
