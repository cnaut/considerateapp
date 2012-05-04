package com.pinokia.considerateapp;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

public class ConsiderateAppActivity extends Activity {

	//global variables
	WebView wv;
	public static int numLocks = 0;
	Timer dailyTimer = new Timer();
	//long delay = 86400 * 1000; //number of millisec in 24 hours
	long delay = 60 * 1000; //number of millisec in 24 hours
	StopWatch stopwatch = new StopWatch();

	String graphString = "<img src='http://chart.apis.google.com/chart?chtt=Number+of+Unlocks&amp;chts=ffffff,12&amp;chs=300x150&amp;chf=bg,s,000000|c,s,000000&amp;chxt=x,y&amp;chxl=0:|T-5|T-4|T-3|T-2|T-1|1:|00.00|25.00|50.00&amp;cht=lxy&amp;chd=t:0.00,25.00,50.00,75.00,100.00|0,0,0,0,0&amp;chco=ffff00&amp;chm=o,ffff00,0,0.0,10|o,ffff00,0,1.0,10|o,ffff00,0,2.0,10|o,ffff00,0,3.0,10|o,ffff00,0,4.0,10' alt='Google Chart'/>";
	double max = 1;
	double tMinus5 = 0;
	double tMinus4 = 0;
	double tMinus3 = 0;
	double tMinus2 = 0;
	double tMinus1 = 0;

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
	    public void onReceive(Context context, Intent intent) {
	    	if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {	    		
	    		//WHAT TO DO WHEN SCREEN IS OFF
	    		stopwatch.stop();
	    		
	    	} else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) { //VS ACTION_SCREEN_ON
	    		//WHAT TO DO WHEN SCREEN IS ON
	    		numLocks ++;
	        	stopwatch.start();
	    		
	    		String phoneUnlockText = "<body bgcolor = 'black'><font color= 'white' size = 5><center>You have unlocked your phone " + numLocks + " times today</center></font></body> <br /> <br />";
	            String data = phoneUnlockText + graphString;
	            wv.loadData(data, "text/html", "UTF-8");
	    	}
	    }
	};


	class timerClearTask extends TimerTask {
		 public void run() {
			 
			 /* Set up the graph*/
			 if (numLocks > max) max = numLocks;
			 tMinus5 = tMinus4;
			 tMinus4 = tMinus3;
			 tMinus3 = tMinus2;
			 tMinus2 = tMinus1;
			 tMinus1 = numLocks;
			 numLocks = 0;
			 
			 String plotPoints = "" + Double.toString(((tMinus5/max)*100.00)) + "," + Double.toString(((tMinus4/max)*100.00)) + "," + Double.toString(((tMinus3/max)*100.00)) + "," + Double.toString(((tMinus2/max)*100.00)) + "," + Double.toString(((tMinus1/max)*100.00));
			 graphString = "<img src='http://chart.apis.google.com/chart?chtt=Number+of+Unlocks&amp;chts=ffffff,12&amp;chs=300x150&amp;chf=bg,s,000000|c,s,000000&amp;chxt=x,y&amp;chxl=0:|T-5|T-4|T-3|T-2|T-1|1:|0|" + (double)((double)max/2.0) + "|" + max + "&amp;cht=lxy&amp;chd=t:0.00,25.00,50.00,75.00,100.00|" + plotPoints + "&amp;chco=ffff00&amp;chm=o,ffff00,0,0.0,10|o,ffff00,0,1.0,10|o,ffff00,0,2.0,10|o,ffff00,0,3.0,10|o,ffff00,0,4.0,10' alt='Google Chart'/>";
			 
			 String phoneUnlockText = "<body bgcolor = 'black'><font color= 'white' size = 5><center>You have unlocked your phone " + numLocks + " times today</center></font></body> <br /> <br />";
             String data = phoneUnlockText + graphString;
             wv.loadData(data, "text/html", "UTF-8");
		 }
	 }

 	public class StopWatch { 
		 private long startTime = 0;
		 private long stopTime = 0;
		 private boolean running = false;
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
		 
	 }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	wv = new WebView(this);
         
        String phoneUnlockText = "<body bgcolor = 'black'><font color= 'white' size = 5><center>You have unlocked your phone " + numLocks + " times today</center></font></body> <br /> <br />";
        String data = phoneUnlockText + graphString;
        wv.loadData(data, "text/html", "UTF-8");
        setContentView(wv);  
        dailyTimer.schedule(new timerClearTask(), 0, delay);
	    
	    //setContentView(R.layout.main);
	    Intent flipIntent = new Intent(getApplicationContext(), FlipIntentService.class);
        startService(flipIntent);

        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT); //vs ACTION_SCREEN_ON
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
		super.onResume();
    }
}
