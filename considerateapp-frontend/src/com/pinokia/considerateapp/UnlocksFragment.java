package com.pinokia.considerateapp;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class UnlocksFragment extends Fragment {
	
	// global variables
	public static WebView wv;
	Timer dailyTimer = new Timer();
	// long delay = 86400 * 1000; //number of millisec in 24 hours
	long delay = 60 * 1000; // number of millisec in 1 minute
	
	public static String graphString = "";

	double max = 5;
	// Power Check
	double tMinus5_pc = 0;
	double tMinus4_pc = 0;
	double tMinus3_pc = 0;
	double tMinus2_pc = 0;
	double tMinus1_pc = 0;
	// Num Unlock
	double tMinus5_nu = 0;
	double tMinus4_nu = 0;
	double tMinus3_nu = 0;
	double tMinus2_nu = 0;
	double tMinus1_nu = 0;

	class timerClearTask extends TimerTask {
		public void run() {

			/* Set up the graph */
			if (StatsService.getNumPowerChecks() > max)
				max = StatsService.getNumPowerChecks();

			// Power Checks
			tMinus5_pc = tMinus4_pc;
			tMinus4_pc = tMinus3_pc;
			tMinus3_pc = tMinus2_pc;
			tMinus2_pc = tMinus1_pc;
			tMinus1_pc = StatsService.getNumPowerChecks();
			StatsService.setNumPowerChecks(0);

			// Num Unlocks
			tMinus5_nu = tMinus4_nu;
			tMinus4_nu = tMinus3_nu;
			tMinus3_nu = tMinus2_nu;
			tMinus2_nu = tMinus1_nu;
			tMinus1_nu = StatsService.getNumLocks();
			StatsService.setNumLocks(0);

			String plotPointsPowerCheck = ""
					+ Double.toString(((tMinus5_pc / max) * 100.00)) + ","
					+ Double.toString(((tMinus4_pc / max) * 100.00)) + ","
					+ Double.toString(((tMinus3_pc / max) * 100.00)) + ","
					+ Double.toString(((tMinus2_pc / max) * 100.00)) + ","
					+ Double.toString(((tMinus1_pc / max) * 100.00));
			String plotPointsNumLocks = ""
					+ Double.toString(((tMinus5_nu / max) * 100.00)) + ","
					+ Double.toString(((tMinus4_nu / max) * 100.00)) + ","
					+ Double.toString(((tMinus3_nu / max) * 100.00)) + ","
					+ Double.toString(((tMinus2_nu / max) * 100.00)) + ","
					+ Double.toString(((tMinus1_nu / max) * 100.00));

			graphString = "<img src='http://chart.apis.google.com/chart?"
					+ "chf=bg,s,000000|c,s,000000&chxl=0:|T-5|T-4|T-3|T-2|T-1"
					+ "&chxr=0,1,5|1,0,"
					+ max
					+ "&chxs=0,FFFFFF,11.5,0,lt,FFFFFF|1,FFFFFF,11.5,0,l,FFFFFF"
					+ "&chxt=x,y&chs=300x150"
					+ "&cht=lxy&chco=224499,FFF700"
					+ "&chd=t:-1|"
					+ plotPointsPowerCheck
					+ "|-1|"
					+ plotPointsNumLocks
					+ "&chdl=Power|Unlocks&chls=3|3"
					+ "&chm=o,0000FF,0,-1,7|o,FFFF00,1,-1,7|B,0000FF34,0,0,0|B,FFFF0065,1,0,0"
					+ "&chtt=Number+of+phone+unlocks+%26+power+presses&chts=FFFFFF,11.5' "
					+ "width='300' height='150' "
					+ "alt='Number of phone unlocks & power presses' />";

			String phoneUnlockText = "<body bgcolor = 'black'><font color= 'white' size = 5><center>You have checked <br />your phone "
					+ StatsService.getNumPowerChecks()
					+ " times <br /> and unlocked your phone "
					+ StatsService.getNumLocks()
					+ " times today</center></font></body> <br /> <br />";
			String data = phoneUnlockText + graphString;
			wv.loadData(data, "text/html", "UTF-8");
			System.out.println("finished loading graph: Unlocks");
			
		}
	}


	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("OnCreate: Unlocks");
		
		wv = new WebView(getActivity());
		StatsService.initContext(getActivity().getApplicationContext());
		StatsService.start(getActivity().getApplicationContext());
		
		String phoneUnlockText = "<body bgcolor = 'black'><font color= 'white' size = 5><center>You have checked <br />your phone "
				+ StatsService.getNumPowerChecks()
				+ " times <br /> and unlocked your phone "
				+ StatsService.getNumLocks()
				+ " times today</center></font></body> <br /> <br />";
		String data = phoneUnlockText + graphString;
		wv.loadData(data, "text/html", "UTF-8");
		dailyTimer.schedule(new timerClearTask(), 0, delay);
		return wv;

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
	}
	
	
	public static void update() {
		String phoneUnlockText = "<body bgcolor = 'black'><font color= 'white' size = 5><center>You have checked <br />your phone "
				+ StatsService.getNumPowerChecks()
				+ " times <br /> and unlocked your phone "
				+ StatsService.getNumLocks()
				+ " times today</center></font></body> <br /> <br />";
		String data = phoneUnlockText + graphString;
		wv.loadData(data, "text/html", "UTF-8");
	}

	
}

