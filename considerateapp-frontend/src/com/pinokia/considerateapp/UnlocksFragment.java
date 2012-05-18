package com.pinokia.considerateapp;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UnlocksFragment extends Fragment {

	// global variables
	static WebView wv;
	static TextView text;

	static int chartWidth = 500;
	static int chartHeight = 220;
	
	Timer dailyTimer = new Timer();
	// long delay = 86400 * 1000; //number of millisec in 24 hours
	long delay = 120 * 1000; // number of millisec in 1 minute

	public static String graphString = "<center><img src='http://0.chart.apis.google.com/chart?"
			+ "chf=bg,s,67676700|c,s,67676700" // transparent background
			+ "&chxl=0:|3 days ago|2 days ago|1 day ago|yesterday|today" // chart labels
			+ "&chxr=0,1,5,1|1,0,5,1" // axis range
			+ "&chxs=0,000000,14,0,lt,000000|1,000000,14,1,l,000000" // chart axis style
			+ "&chxt=x,y" // chart axis ordering
			+ "&chs=" + chartWidth + "x" + chartHeight // chart size
			+ "&cht=lxy" // chart type
			+ "&chco=58D9FC,EE58FC" // line colors
			+ "&chd=t:-1|0,0,0,0,0|-1|0,0,0,0,0" // chart data
			+ "&chdl=Number of Screen Views|Number of Unlocks" // chart legend text
			+ "&chls=3|3" // line style (thickness)
			+ "&chm=B,58D9FC36,0,0,0,1|B,EE58FC34,1,0,0' />"; // area fill colors;

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

	class timerClearTask extends TimerTask {
		public void run() {
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

			System.out.println("Unlocks day passed");

		}
	}

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("OnCreate: Unlocks");

		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.stats_layout, container, false);
		text = (TextView) view.findViewById(R.id.text);
		wv = (WebView) view.findViewById(R.id.graph);
		wv.setBackgroundColor(0);

		StatsService.initContext(getActivity().getApplicationContext());
		StatsService.start(getActivity().getApplicationContext());

		dailyTimer.schedule(new timerClearTask(), 0, delay);
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
		System.out.println("OnPause: Unlocks");
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("OnResume: Unlocks");

		text.setText("You have checked your phone "
				+ tMinus1_pc
				+ " times\n and unlocked your phone "
				+ tMinus1_nu + " times today.");
		wv.loadData(graphString, "text/html", "UTF-8");

		
		System.out.println("UNLOCKS_URL:" + graphString);
	}

	public static void update() { 
		tMinus1_pc = StatsService.getNumPowerChecks();
		tMinus1_nu = StatsService.getNumLocks();
		
		if (tMinus1_pc > max)
			max = tMinus1_pc;
		
		String plotPointsPowerCheck = ""
				+ Double.toString((tMinus5_pc/max) * 100.0) + ","
				+ Double.toString((tMinus4_pc/max) * 100.0) + ","
				+ Double.toString((tMinus3_pc/max) * 100.0) + ","
				+ Double.toString((tMinus2_pc/max) * 100.0) + ","
				+ Double.toString((tMinus1_pc/max) * 100.0);
		String plotPointsNumLocks = ""
				+ Double.toString((tMinus5_nu/max) * 100.0) + ","
				+ Double.toString((tMinus4_nu/max) * 100.0) + ","
				+ Double.toString((tMinus3_nu/max) * 100.0) + ","
				+ Double.toString((tMinus2_nu/max) * 100.0) + ","
				+ Double.toString((tMinus1_nu/max) * 100.0);

		graphString = "<center><img src='http://0.chart.apis.google.com/chart?"
				+ "chf=bg,s,67676700|c,s,67676700" // transparent background
				+ "&chxl=0:|3 days ago|2 days ago|1 day ago|yesterday|today" // chart labels
				+ "&chxr=0,1,5,1|1,0," + max + ",1" // axis range
				+ "&chxs=0,000000,14,0,lt,000000|1,000000,14,1,l,000000" // chart axis style
				+ "&chxt=x,y" // chart axis ordering
				+ "&chs=" + chartWidth + "x" + chartHeight // chart size
				+ "&cht=lxy" // chart type
				+ "&chco=58D9FC,EE58FC" // line colors
				+ "&chd=t:-1|" + plotPointsPowerCheck + "|-1|" + plotPointsNumLocks // chart data
				+ "&chdl=Number of Screen Views|Number of Unlocks" // chart legend text
				+ "&chls=3|3" // line style (thickness)
				+ "&chm=B,58D9FC36,0,0,0,1|B,EE58FC34,1,0,0' />"; // area fill colors
		
		text.setText("You have checked your phone "
				+ tMinus1_pc
				+ " times\n and unlocked your phone "
				+ tMinus1_nu + " times today.");
		wv.loadData(graphString, "text/html", "UTF-8");
	}
}
