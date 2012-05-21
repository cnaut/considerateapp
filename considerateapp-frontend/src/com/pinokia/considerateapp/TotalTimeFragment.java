package com.pinokia.considerateapp;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.TextView;

public class TotalTimeFragment extends Fragment {

	// global variables
	WebView wv;
	TextView text;

	static int chartWidth = 500;
	static int chartHeight = 220;

	double max = 0;
	
	Timer secondTimer;
	long secondDelay = 1000;

	Timer constantUpdateTimer;
	long constantUpdateDelay = 10 * 1000;

	String graphString = "";

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

			// Update while user is in the app
			System.out.println("OnResume timer task: TotalTime");
	
			ArrayList<Double> totalTime = StatsService.getTotalTime();
			String plotPointsTotalTime = "";
			if (totalTime == null) { // StatsService hasn't created it yet
				plotPointsTotalTime = "0,0,0,0,0";
			} else {

				int lastIndex = totalTime.size() - 1;
				if (totalTime.get(lastIndex) > max)
					max = totalTime.get(lastIndex);

				for (int i = 0; i <= lastIndex; i++) {
					plotPointsTotalTime += Double
							.toString((totalTime.get(i) / max) * 100.00) + ",";
				}

				plotPointsTotalTime = plotPointsTotalTime.substring(0,
						plotPointsTotalTime.length() - 1);
			}

			graphString = "<center><img src='http://1.chart.apis.google.com/chart"
					+ "?chf=bg,s,67676700|c,s,67676700" // transparent background
					+ "&chxl=0:|3 days ago|2 days ago|1 day ago|yesterday|today" // chart labels
					+ "&chxr=0,1,5,1|1,0," + max + "" // axis range
					+ "&chxs=0,000000,14,0,lt,000000|1,000000,14,1,l,000000" // chart axis style
					+ "&chxt=x,y" // chart axis ordering
					+ "&chs=" + chartWidth + "x" + chartHeight // chart size
					+ "&cht=lc" // chart type
					+ "&chco=58D9FC,EE58FC" // line colors
					+ "&chd=t:" + plotPointsTotalTime // chart data
					+ "&chls=3' />"; // line style (thickness)

			wv.loadData(graphString, "text/html", "UTF-8");
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
		wv.getSettings().setRenderPriority(RenderPriority.HIGH);
		wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		wv.setBackgroundColor(0);

		System.out.println("at oncreate: Total Time");

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

		constantUpdateTimer.cancel();
		secondTimer.cancel();
	}

	@Override
	public void onResume() {
		super.onResume();
		constantUpdateTimer = new Timer();
		constantUpdateTimer.schedule(new timerConstantUpdateTask(), 0,
				constantUpdateDelay);
		secondTimer = new Timer();
		secondTimer.schedule(new timerSecondTask(), 0, secondDelay);
	}
}
