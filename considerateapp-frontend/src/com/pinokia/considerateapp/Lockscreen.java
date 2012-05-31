package com.pinokia.considerateapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class Lockscreen extends Activity implements OnTouchListener {
	public int timeleft = 0;
	// flag off after we successfully gain focus.
	// flag on when we send task to back
	public boolean starting = true;
	// any time quiet or active wake are up
	public boolean waking = false;
	// flag on when an event causes unlock, back off when onStart comes in again
	// (relocked)
	public boolean finishing = false;

	public boolean paused = false;

	public boolean shouldFinish = false;

	public boolean screenwake = false;// set true when a wakeup key or external
	// event turns screen on

	public TextView phoneScore;
	private int sliderTrackWidth;

	String tag = "LOCK SCREEN";
	HorizontalScrollView slider;

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Log.v("Lockscreen", "starting to create!");
		if (android.os.Build.VERSION.SDK_INT < 14) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		setContentView(R.layout.lockscreen);
		if (android.os.Build.VERSION.SDK_INT > 14) {
			View v = findViewById(android.R.id.content).getRootView();
			// Right now doesn't work -- to enable this we need to make an
			// action bar to replace our menus.
			ICSNavHider.DisableNav(v);
		}

		phoneScore = (TextView) findViewById(R.id.phoneScore);

		slider = (HorizontalScrollView) findViewById(R.id.slider);
		slider.postDelayed(new Runnable() {
			public void run() {
				sliderTrackWidth = findViewById(R.id.sliderTrack).getWidth();
				slider.scrollTo(sliderTrackWidth / 2 - slider.getWidth() / 2, 0);
			}
		}, 250);
		slider.setOnTouchListener(this);
	}

	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.slider) {
			if (event.getAction() == MotionEvent.ACTION_DOWN
					|| event.getAction() == MotionEvent.ACTION_MOVE) {
				if (slider.getScrollX() < sliderTrackWidth / 2
						- slider.getWidth() * 7 / 8) {
					Intent startMain = new Intent(Intent.ACTION_MAIN);
					startMain.addCategory(Intent.CATEGORY_HOME);
					startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(startMain);
				}
				if (slider.getScrollX() > sliderTrackWidth / 2
						- slider.getWidth() / 8) {
					Intent intent = new Intent(this,
							ConsiderateAppActivity.class);
					startActivity(intent);
				}
				return false;
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				slider.scrollTo(sliderTrackWidth / 2 - slider.getWidth() / 2, 0);
				return true;
			}
		}
		return false;
	}

	protected void onStart() {
		super.onStart();
		Log.v("Lockscreen", "done creating!");
	}

	protected void onPause() {
		super.onPause();
		Log.v("Lockscreen", "pausing!");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v("Lockscreen", "resuming!");
		ArrayList<Integer> numScreenViews = StatsService.getNumScreenViews();
		int currNumScreenViews = numScreenViews.get(numScreenViews.size() - 1);
		
		SharedPreferences prefs = this.getSharedPreferences(ConsiderateAppActivity.prefsName, 0);
		int considerateTime = prefs.getInt("considerate_time", 0);
		
		int score = 99 - currNumScreenViews + considerateTime;
		if (score > 99)
			score == 99;
		System.out.println("Phone Score: " + score);

		phoneScore.setText(Integer.toString(score));
	}

	@Override
	public void onBackPressed() {
		if (screenwake) {
			finishing = true;
			moveTaskToBack(true);
		}
		return;
	}

	BroadcastReceiver screenoff = new BroadcastReceiver() {
		public static final String Screenoff = "android.intent.action.SCREEN_OFF";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(Screenoff))
				return;

			if (screenwake && hasWindowFocus()) {

				screenwake = false;
				setBright((float) 0.0);
			} else if (waking) {
				shouldFinish = true;
			}
			waking = false; // reset lifecycle

			return; // avoid unresponsive receiver error outcome
		}
	};

	// public void wakeUp() {
	// setBright((float) 0.1);// tell screen to go on with 10% brightness
	// PowerManager myPM = (PowerManager) getApplicationContext()
	// .getSystemService(Context.POWER_SERVICE);
	// myPM.userActivity(SystemClock.uptimeMillis(), false);

	// screenwake = true;
	// timeleft = 0;// this way the task doesn't keep going
	// }

	public void setBright(float value) {
		Window mywindow = getWindow();

		WindowManager.LayoutParams lp = mywindow.getAttributes();

		lp.screenBrightness = value;

		mywindow.setAttributes(lp);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// This blocks key events from firing.
		return true;

		// Log.v("dispatchKeyEvent", "HERE'S A KEY EVENT. LOOK AT ME.");
		// boolean up = event.getAction() == KeyEvent.ACTION_UP;
		// int code = event.getKeyCode();
		// Log.v("dispatching a key event", "Is this the up? -" + up);

		// int reaction = 1;// wakeup, the preferred behavior in advanced mode

		// if (code == KeyEvent.KEYCODE_BACK)
		// reaction = 3;// check for wake, if yes, exit
		// else if (code == KeyEvent.KEYCODE_POWER)
		// reaction = 2;// unlock
		// // else if (code == KeyEvent.KEYCODE_FOCUS)
		// // reaction = 0;// locked (advanced power save)

		// switch (reaction) {
		// case 3:
		// onBackPressed();
		// return true;
		// case 2:
		// Log.v("here i am", "search for me");
		// if (up && !finishing) {
		// Log.v("unlock key", "power key UP, unlocking");
		// finishing = true;

		// setBright((float) 0.1);

		// moveTaskToBack(true);

		// }
		// return true;

		// case 1:
		// if (up && !screenwake) {
		// waking = true;
		// Log.v("key event", "wake key");
		// wakeUp();
		// }
		// return true;

		// case 0:
		// if (!screenwake && up) {
		// timeleft = 10;

		// if (!waking) {
		// Log.v("key event", "locked key timer starting");

		// waking = true;
		// // serviceHandler.postDelayed(myTask, 500L);
		// }
		// }

		// return true;
		// }
		// return false;
	}
}
