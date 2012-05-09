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
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class ConsiderateAppActivity extends Activity {

	//global variables
	public WebView wv;
	Timer dailyTimer = new Timer();
	//long delay = 86400 * 1000; //number of millisec in 24 hours
	long delay = 60 * 1000; //number of millisec in 1 minute
	//StopWatch stopwatch = new StopWatch();

	String graphString = "<img src='http://chart.apis.google.com/chart?" +
		 		"chf=bg,s,000000|c,s,000000&chxl=0:|T-5|T-4|T-3|T-2|T-1" +
		 		"&chxr=0,1,5|1,0,1" + //maX!
		 		"&chxs=0,FFFFFF,11.5,0,lt,FFFFFF|1,FFFFFF,11.5,0,l,FFFFFF" +
		 		"&chxt=x,y&chs=300x150" +
		 		"&cht=lxy&chco=224499,FFF700" +
		 		"&chd=t:-1|0,0,0,0,0|-1|0,0,0,0,0" +
		 		"&chdl=Power|Unlocks&chls=3|3" +
		 		"&chm=o,0000FF,0,-1,7|o,FFFF00,1,-1,7|B,0000FF34,0,0,0|B,FFFF0065,1,0,0" +
		 		"&chtt=Number+of+phone+unlocks+%26+power+presses&chts=FFFFFF,11.5' " +
		 		"width='300' height='150' " +
		 		"alt='Number of phone unlocks & power presses' />";
	
	double max = 1;
	//Power Check
	double tMinus5_pc = 0;
	double tMinus4_pc = 0;
	double tMinus3_pc = 0;
	double tMinus2_pc = 0;
	double tMinus1_pc = 0;
	//Num Unlock
	double tMinus5_nu = 0;
	double tMinus4_nu = 0;
	double tMinus3_nu = 0;
	double tMinus2_nu = 0;
	double tMinus1_nu = 0;

	class timerClearTask extends TimerTask {
		 public void run() {

			 /* Set up the graph*/
			 if (StatsService.getNumPowerChecks() > max) max = StatsService.getNumPowerChecks();
			 
			 //Power Checks
			 tMinus5_pc = tMinus4_pc;
			 tMinus4_pc = tMinus3_pc;
			 tMinus3_pc = tMinus2_pc;
			 tMinus2_pc = tMinus1_pc;
			 tMinus1_pc = StatsService.getNumPowerChecks();
			 
			 //Num Unlocks
			 tMinus5_nu = tMinus4_nu;
			 tMinus4_nu = tMinus3_nu;
			 tMinus3_nu = tMinus2_nu;
			 tMinus2_nu = tMinus1_nu;
			 tMinus1_nu = StatsService.getNumLocks();

			 String plotPointsPowerCheck = "" + Double.toString(((tMinus5_pc/max)*100.00)) + "," + Double.toString(((tMinus4_pc/max)*100.00)) + "," + Double.toString(((tMinus3_pc/max)*100.00)) + "," + Double.toString(((tMinus2_pc/max)*100.00)) + "," + Double.toString(((tMinus1_pc/max)*100.00));
			 String plotPointsNumLocks = "" + Double.toString(((tMinus5_nu/max)*100.00)) + "," + Double.toString(((tMinus4_nu/max)*100.00)) + "," + Double.toString(((tMinus3_nu/max)*100.00)) + "," + Double.toString(((tMinus2_nu/max)*100.00)) + "," + Double.toString(((tMinus1_nu/max)*100.00));
			 
			 //graphString = "<img src='http://chart.apis.google.com/chart?chtt=Number+of+Unlocks&amp;chts=ffffff,12&amp;chs=300x150&amp;chf=bg,s,000000|c,s,000000&amp;chxt=x,y&amp;chxl=0:|T-5|T-4|T-3|T-2|T-1|1:|0|" + (double)((double)max/2.0) + "|" + max + "&amp;cht=lxy&amp;chd=t:0.00,25.00,50.00,75.00,100.00|" + plotPoints + "&amp;chco=ffff00&amp;chm=o,ffff00,0,0.0,10|o,ffff00,0,1.0,10|o,ffff00,0,2.0,10|o,ffff00,0,3.0,10|o,ffff00,0,4.0,10' alt='Google Chart'/>";
			 graphString = "<img src='http://chart.apis.google.com/chart?" +
			 		"chf=bg,s,000000|c,s,000000&chxl=0:|T-5|T-4|T-3|T-2|T-1" +
			 		"&chxr=0,1,5|1,0," + max +
			 		"&chxs=0,FFFFFF,11.5,0,lt,FFFFFF|1,FFFFFF,11.5,0,l,FFFFFF" +
			 		"&chxt=x,y&chs=300x150" +
			 		"&cht=lxy&chco=224499,FFF700" +
			 		"&chd=t:-1|" + plotPointsPowerCheck + "|-1|" + plotPointsNumLocks +
			 		"&chdl=Power|Unlocks&chls=3|3" +
			 		"&chm=o,0000FF,0,-1,7|o,FFFF00,1,-1,7|B,0000FF34,0,0,0|B,FFFF0065,1,0,0" +
			 		"&chtt=Number+of+phone+unlocks+%26+power+presses&chts=FFFFFF,11.5' " +
			 		"width='300' height='150' " +
			 		"alt='Number of phone unlocks & power presses' />";

		     String phoneUnlockText = "<body bgcolor = 'black'><font color= 'white' size = 5><center>You have checked <br />your phone " + StatsService.getNumPowerChecks() + " times <br /> and unlocked your phone " + StatsService.getNumLocks() + " times today</center></font></body> <br /> <br />";
			 String data = phoneUnlockText + graphString;
             wv.loadData(data, "text/html", "UTF-8");
		 }
	 }

	/*
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
	 */

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	wv = new WebView(this);
    	
        StatsService.initContext(getApplicationContext());
        FlipService.start(getApplicationContext());
        StatsService.start(getApplicationContext());
         
    	String phoneUnlockText = "<body bgcolor = 'black'><font color= 'white' size = 5><center>You have checked <br />your phone " + StatsService.getNumPowerChecks() + " times <br /> and unlocked your phone " + StatsService.getNumLocks() + " times today</center></font></body> <br /> <br />";
    	String data = phoneUnlockText + graphString;
        wv.loadData(data, "text/html", "UTF-8");
        setContentView(wv);  
        dailyTimer.schedule(new timerClearTask(), 0, delay);

    }

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
    }
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.settings_menu, menu);
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.toggle_lockscreen:
    			toggleSleepMonitor();
	    		return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }

    protected void toggleSleepMonitor(){
    	if (SleepMonitorService.toggleService(getApplicationContext()))
    	{
	        Toast.makeText(getApplicationContext(),
	        	"Lockscreen is currently being replaced.", Toast.LENGTH_SHORT).show();

    	} else {
	        Toast.makeText(getApplicationContext(),
	        	"Lockscreen is no longer being replaced.", Toast.LENGTH_SHORT).show();
    	}
    }
}
