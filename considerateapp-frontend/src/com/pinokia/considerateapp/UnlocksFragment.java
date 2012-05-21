package com.pinokia.considerateapp;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class UnlocksFragment extends Fragment {

	// global variables
	static WebView wv;
	static TextView text;

	static int chartWidth = 500;
	static int chartHeight = 220;
	
	static double max = 0.0;
	
	public static String graphString = "";

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.stats_layout, container, false);
		
		text = (TextView) view.findViewById(R.id.text);
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
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(broadcastReceiver);
		System.out.println("OnPause: Unlocks");
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(broadcastReceiver, new IntentFilter(StatsService.BROADCAST_ACTION));
		update();
	}
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			update();
		}
	};
	
	public static void update() { 
		
		ArrayList<Integer> numScreenViews = StatsService.getNumScreenViews();
		ArrayList<Integer> numUnlocks = StatsService.getNumUnlocks();
		
		String plotPointsScreenViews = "";
		String plotPointsNumUnlocks = "";
		if (numScreenViews == null || numUnlocks == null) {
			plotPointsScreenViews = "0,0,0,0,0";
			plotPointsNumUnlocks = "0,0,0,0,0";
			text.setText("You have checked your phone 0 times\n and "
					+ "unlocked your phone 0 times today.");

		} else {

			int lastIndex = numScreenViews.size() - 1;

			if (numScreenViews.get(lastIndex) > max) {
				max = numScreenViews.get(lastIndex);
			}

			for (int i = 0; i <= lastIndex; i++) {
				plotPointsScreenViews += Double
						.toString(((double) numScreenViews.get(i) / max) * 100.0)
						+ ",";
				plotPointsNumUnlocks += Double.toString(((double) numUnlocks
						.get(i) / max) * 100.0) + ",";
			}
			plotPointsScreenViews = plotPointsScreenViews.substring(0,
					plotPointsScreenViews.length() - 1);
			plotPointsNumUnlocks = plotPointsNumUnlocks.substring(0,
					plotPointsNumUnlocks.length() - 1);

			text.setText("You have checked your phone "
					+ numScreenViews.get(lastIndex).intValue()
					+ " times\n and unlocked your phone "
					+ numUnlocks.get(lastIndex).intValue() + " times today.");

		}

		graphString = "<center><img src='http://0.chart.apis.google.com/chart?"
				+ "chf=bg,s,67676700|c,s,67676700" // transparent background
				+ "&chxl=0:|3 days ago|2 days ago|1 day ago|yesterday|today" // chart labels
				+ "&chxr=0,1,5,1|1,0," + max
				+ ",1" // axis range
				+ "&chxs=0,000000,14,0,lt,000000|1,000000,14,1,l,000000" // chart axis style
				+ "&chxt=x,y" // chart axis ordering
				+ "&chs=" + chartWidth + "x"
				+ chartHeight // chart size
				+ "&cht=lxy" // chart type
				+ "&chco=58D9FC,EE58FC" // line colors
				+ "&chd=t:-1|" + plotPointsScreenViews + "|-1|"
				+ plotPointsNumUnlocks // chart data
				+ "&chdl=Number of Screen Views|Number of Unlocks" // chart legend text
				+ "&chls=3|3" // line style (thickness)
				+ "&chm=B,58D9FC36,0,0,0,1|B,EE58FC34,1,0,0' />"; // area fill colors

		wv.loadData(graphString, "text/html", "UTF-8");
	}
}
