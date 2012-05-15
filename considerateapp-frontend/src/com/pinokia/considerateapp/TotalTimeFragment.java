package com.pinokia.considerateapp;

import java.util.Timer;
import java.util.TimerTask;

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
import android.webkit.WebView;


public class TotalTimeFragment extends Fragment {

	//global variables
	WebView wv;
	Timer dailyTimer = new Timer();
	//long delay = 86400 * 1000; //number of millisec in 24 hours
	long dailyDelay = 60 * 1000; //number of millisec in 1 minute

	//StopWatch stopwatch = new StopWatch();

	//String graphString = "";
	
	//String graphString = "<img src='http://chart.apis.google.com/chart?chf=bg,s,000000|c,s,000000&chxl=0:|T-5|T-4|T-3|T-2|T-1&chxs=0,FFFFFF,11.5,0,lt,FFFFFF|1,FFFFFF,11.5,0,l,676767&chxt=x,y&chs=440x220&cht=lc&chco=FF0000&chds=0,134&chd=t:80,62,68,45,37&chdlp=b&chls=2&chma=5,5,5,25&chm=o,FF0000,0,-1,10&chtt=Total+Time+on+Phone+over+Past+Five+Days&chts=FFFFFF,11.5' width='300' height='150' alt='Total Time on Phone over Past Five Days' />";
	
	
	String graphString = "<img src='http://chart.apis.google.com/chart" +
			"?chf=bg,s,000000|c,s,000000" +
			"&chxl=0:|T-5|T-4|T-3|T-2|T-1" +
			"&chxr=1,0,10" +
			"&chxs=0,FFFFFF,11.5,0,lt,FFFFFF|1,FFFFFF,11.5,0,l,676767" +
			"&chxt=x,y" +
			"&chs=440x220&cht=lc" +
			"&chco=FF0000" +
			"&chd=t:0,0,0,0,0" + 
			"&chdlp=b&chls=2" +
			"&chma=5,5,5,25" +
			"&chm=o,FF0000,0,-1,10" +
			"&chtt=Total+Time+on+Phone+over+Past+Five+Days" +
			"&chts=FFFFFF,11.5' " +
			"width='300' " +
			"height='150' " +
			"alt='Total Time on Phone over Past Five Days' />";
			
	
	double max = 10;
	// Power Check
	double tMinus5_tt = 0;
	double tMinus4_tt = 0;
	double tMinus3_tt = 0;
	double tMinus2_tt = 0;
	double tMinus1_tt = 0;

	/*
	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {	    		
				//WHAT TO DO WHEN SCREEN IS OFF
				stopwatch.stop();

			} else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
				//WHAT TO DO WHEN SCREEN IS UNLOCKED
				stopwatch.start();

			} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) { 
				//WHAT TO DO WHEN SCREEN IS ON
			} 
		}
	};*/

	

	class timerDailyTask extends TimerTask {
		public void run() {

			double timeSpentSeconds = (double) StatsService.getStopWatch().getTotalTime() / 1000.00;
			if (timeSpentSeconds > max) 
				max = timeSpentSeconds;
			
			//System.out.println("BEFORE: "+ stopwatch.getTotalTime());
			tMinus5_tt = tMinus4_tt;
			tMinus4_tt = tMinus3_tt;
			tMinus3_tt = tMinus2_tt;
			tMinus2_tt = tMinus1_tt;
			tMinus1_tt = timeSpentSeconds;
			StatsService.getStopWatch().setTotalTime(0);
			//System.out.println("AFTER: "+ stopwatch.getTotalTime());
			
			String plotPointsTotalTime = ""
					+ Double.toString(((tMinus5_tt / max) * 100.00)) + ","
					+ Double.toString(((tMinus4_tt / max) * 100.00)) + ","
					+ Double.toString(((tMinus3_tt / max) * 100.00)) + ","
					+ Double.toString(((tMinus2_tt / max) * 100.00)) + ","
					+ Double.toString(((tMinus1_tt / max) * 100.00));
			
			
		
			graphString = "<img src='http://chart.apis.google.com/chart" +
					"?chf=bg,s,000000|c,s,000000" +
					"&chxl=0:|T-5|T-4|T-3|T-2|T-1" +
					"&chxr=1,0," +
					max +
					"&chxs=0,FFFFFF,11.5,0,lt,FFFFFF|1,FFFFFF,11.5,0,l,676767" +
					"&chxt=x,y" +
					"&chs=440x220&cht=lc" +
					"&chco=FF0000" +
					"&chd=t:" + 
					plotPointsTotalTime +
					"&chdlp=b&chls=2" +
					"&chma=5,5,5,25" +
					"&chm=o,FF0000,0,-1,10" +
					"&chtt=Total+Time+on+Phone+over+Past+Five+Days" +
					"&chts=FFFFFF,11.5' " +
					"width='300' " +
					"height='150' " +
					"alt='Total Time on Phone over Past Five Days' />";
			
			 int hours = (int) timeSpentSeconds / (60 * 60);
			 int mins = (int) (timeSpentSeconds / 60) % 60;
			 int secs = (int) ((timeSpentSeconds) % 60) + 1;
			 String timeSpentText = "<body bgcolor = 'black'><font color= 'white' size = 4><center>You have been on your phone for <br />" + hours + " hours " + mins + " mins and " + secs + " secs today.</center></font></body> <br /><br />";
			 String data = timeSpentText + graphString;
             wv.loadData(data, "text/html", "UTF-8");
             System.out.println("finished loading graph: Total Time");
		}
	}
/*
	public class StopWatch { 
		private long startTime = System.currentTimeMillis();
		private long stopTime = 0;
		private boolean running = true;
		private long totalTime = 0;

		public long getTotalTime() {
			if (this.running) {
				long elapsed = this.totalTime + (System.currentTimeMillis() - this.startTime);
				return elapsed;
			}
			return this.totalTime;
		}

		public void setTotalTime(long setTime) {
			this.totalTime = setTime;
			startTime = System.currentTimeMillis();
			stopTime = 0;
			running = true;
		}

		public void start() {
			this.startTime = System.currentTimeMillis();
			this.running = true;
		}

		public void stop() {
			//if phone starts off in SCREEN_OFF state
			if (this.startTime == 0) {
				return;
			}

			this.stopTime = System.currentTimeMillis();
			this.running = false;

			long elapsed = this.stopTime-this.startTime;
			this.totalTime = this.totalTime + elapsed;

			this.startTime = 0;
			this.stopTime = 0;
		}

	}*/

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//wv = new WebView(this);
		//System.out.println("OnCreate: TotalTime");
		wv = new WebView(getActivity());
		long timeSpentMillis = StatsService.getStopWatch().getTotalTime();
		int hours = (int) timeSpentMillis / (1000 * 60 * 60);
		int mins = (int) (timeSpentMillis / (1000 * 60)) % 60;
		int secs = (int) (timeSpentMillis / (1000)) % 60;
		String timeSpentText = "<body bgcolor = 'black'><font color= 'white' size = 4><center>You have been on your phone for <br />" + hours + " hours " + mins + " mins and " + secs + " secs today.</center></font></body> <br /><br />";

		String data = timeSpentText + graphString;
		wv.loadData(data, "text/html", "UTF-8");   
		//setContentView(wv);  

		//Intent flipIntent = new Intent(getApplicationContext(), FlipService.class);
		//startService(flipIntent);

		/*
		IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT); 
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		getActivity().registerReceiver(receiver, filter);
		*/
		
		//System.out.println("at oncreate");
		dailyTimer.schedule(new timerDailyTask(), 0, dailyDelay);
		return wv;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		setUserVisibleHint(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		System.out.println("OnPause: TotalTime");
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("OnResume: TotalTime");
		
		long timeSpentMillis = StatsService.getStopWatch().getTotalTime();
		int hours = (int) timeSpentMillis / (1000 * 60 * 60);
		int mins = (int) (timeSpentMillis / (1000 * 60)) % 60;
		int secs = (int) (timeSpentMillis / (1000)) % 60;
		String timeSpentText = "<body bgcolor = 'black'><font color= 'white' size = 4><center>You have been on your phone for <br />" + hours + " hours " + mins + " mins and " + secs + " secs today.</center></font></body> <br /><br />";
		String data = timeSpentText + graphString;
		wv.loadData(data, "text/html", "UTF-8");
	}
}
