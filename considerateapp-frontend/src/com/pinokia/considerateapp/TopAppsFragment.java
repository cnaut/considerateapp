package com.pinokia.considerateapp;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

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

	private final int numTopApps = 5;
	
	static int chartWidth = 500;
	static int chartHeight = 220;

	Timer constantUpdateTimer;
	long constantUpdateDelay = 10 * 1000;

	static String graphString = "";

	class timerConstantUpdateTask extends TimerTask {
		public void run() {

			TreeMap<String, Double> sorted_map = StatsService.getAppsMap();

			String plotPointsApps = "";
			String plotPointsTime = "";
			double max = -1;

			int size = sorted_map.size();
			if (size > numTopApps) {
				size = numTopApps;
			}

			for (String key : sorted_map.keySet()) {
				assert (sorted_map != null);
				double value = sorted_map.get(key);
				if (value > max)
					max = value;

				if (size == 1) {
					plotPointsApps = plotPointsApps + key;
					plotPointsTime = plotPointsTime + value;
					break;
				} else {
					plotPointsApps = plotPointsApps + key + "|";
					plotPointsTime = plotPointsTime + value + ",";
				}
				size -= 1;
			}

			graphString = "<img src='http://2.chart.apis.google.com/chart?"
					+ "chf=bg,s,67676700|c,s,67676700" // transparent background
					+ "&chs=" + TopAppsFragment.chartWidth + "x"
					+ TopAppsFragment.chartHeight // chart size
					+ "&cht=p" // chart type
					+ "&chco=58D9FC,EE58FC" // slice colors
					+ "&chds=0," + max // range
					+ "&chd=t:" + plotPointsTime // data
					+ "&chdl=" + plotPointsApps + "' />"; // labels

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
		text.setText("Here are the apps you've spent the most time on today:");
		wv = (WebView) view.findViewById(R.id.graph);
		wv.setBackgroundColor(0);

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
		constantUpdateTimer.cancel();
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("OnResume: TopApps");

		constantUpdateTimer = new Timer();
		constantUpdateTimer.schedule(new timerConstantUpdateTask(), 0,
				constantUpdateDelay);
	}
}
