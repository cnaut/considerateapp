package com.pinokia.considerateapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;

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

public class TotalTimeFragment extends Fragment {

	// global variables
	private TextView text;
	private ChartView chart;

	private Timer secondTimer;
	private static final long secondDelay = 1000; // 1 second

	Timer constantUpdateTimer;
	private static final long constantUpdateDelay = 10 * 1000; // 10 seconds

	class timerSecondTask extends TimerTask {
		public void run() {

			Activity a = getActivity();
			if (a == null) {
				// do nothing
				// System.out.println("activity is null");
			} else {
				a.runOnUiThread(new Runnable() {
					public void run() {
						double timeSpentSeconds = (double) StatsService
								.getStopWatch().getTotalTime() / 1000.00;

						int hours = (int) timeSpentSeconds / (60 * 60);
						int mins = (int) (timeSpentSeconds / (60)) % 60;
						int secs = (int) (timeSpentSeconds) % 60;
						text.setText("You have been on your phone for\n"
								+ hours + " hours " + mins + " mins and "
								+ secs + " secs today.");
					}

				});
			}
		}
	}

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
		chart = (ChartView) view.findViewById(R.id.chart);
		chart.setType(ChartView.chartType.LINE);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		setUserVisibleHint(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("OnStop: TotalTime");
	}

	@Override
	public void onStart() {
		super.onStart();
		System.out.println("OnStart: TotalTime");
	}

	@Override
	public void onPause() {
		super.onPause();
		System.out.println("OnPause: TotalTime");

		if (ConsiderateAppActivity.testing) {
			constantUpdateTimer.cancel();
		} else {
			getActivity().unregisterReceiver(broadcastReceiver);
		}
		secondTimer.cancel();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (ConsiderateAppActivity.testing) {
			constantUpdateTimer = new Timer();
			constantUpdateTimer.schedule(new timerConstantUpdateTask(), 0,
					constantUpdateDelay);
		} else {
			getActivity().registerReceiver(broadcastReceiver,
					new IntentFilter(Intent.ACTION_TIME_TICK));
			update();
		}
		secondTimer = new Timer();
		secondTimer.schedule(new timerSecondTask(), 0, secondDelay);
	}

	private void update() {
		ArrayList<Long> totalTime = StatsService.getTotalTime();
		XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		TimeSeries time = new TimeSeries("Total Time");

		Date today = new Date();
		for (int i = 0; i < totalTime.size(); i++) {
			Date date = new Date(today.getTime() - (totalTime.size() - 1 - i)
					* 24 * 60 * 60 * 1000);
			time.add(date, totalTime.get(i));
		}
		data.addSeries(time);
		chart.createChart(data);
		chart.invalidate();
		System.out.println("TOTAL TIME UPDATE");
	}
}