package com.pinokia.considerateapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class Lockscreen extends Activity implements OnClickListener, OnTouchListener {
	public int timeleft = 0;
	public SharedPreferences savedData;
	public boolean starting = true;// flag off after we successfully gain focus.
									// flag on when we send task to back
	public boolean waking = false;// any time quiet or active wake are up
	public boolean finishing = false;// flag on when an event causes unlock,
										// back off when onStart comes in again
										// (relocked)

	public boolean paused = false;

	public boolean shouldFinish = false;

	public boolean screenwake = false;// set true when a wakeup key or external
										// event turns screen on

	String tag = "LOCK SCREEN";
	HorizontalScrollView lock;

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Log.v("Lockscreen", "starting to create!");
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		setContentView(R.layout.lockscreen);

		TextView phoneScore = (TextView) findViewById(R.id.phoneScore);
		phoneScore.setOnClickListener(this);

		lock = (HorizontalScrollView) findViewById(R.id.lock);
		lock.postDelayed(new Runnable() {
			public void run() {
				lock.scrollTo(435, 0);
			}
		}, 250);
		lock.setOnTouchListener(this);

	}
	// Implement the OnClickListener callback
		public void onClick(View v) {
			// do something when the button is clicked
			if (v.getId() == R.id.phoneScore) {
				Log.i(tag, "phone score clicked");
				Intent intent = new Intent(this, ConsiderateAppActivity.class);
				startActivity(intent);
			}
		}

		public boolean onTouch(View v, MotionEvent event) {
			if (v.getId() == R.id.lock) {
				if (event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_MOVE) {
					if (lock.getScrollX() < 200 || lock.getScrollX() > 700) {
						Intent startMain = new Intent(Intent.ACTION_MAIN);
						startMain.addCategory(Intent.CATEGORY_HOME);
						startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(startMain);
					}
					return false;
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					lock.scrollTo(435, 0);
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

	protected void onResume() {
		super.onResume();
		Log.v("Lockscreen", "resuming!");
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

			return;// avoid unresponsive receiver error outcome
		}
	};

	public void wakeUp() {
		setBright((float) 0.1);// tell screen to go on with 10% brightness
		PowerManager myPM = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		myPM.userActivity(SystemClock.uptimeMillis(), false);

		screenwake = true;
		timeleft = 0;// this way the task doesn't keep going
	}

	public void setBright(float value) {
		Window mywindow = getWindow();

		WindowManager.LayoutParams lp = mywindow.getAttributes();

		lp.screenBrightness = value;

		mywindow.setAttributes(lp);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		boolean up = event.getAction() == KeyEvent.ACTION_UP;
		int code = event.getKeyCode();
		Log.v("dispatching a key event", "Is this the up? -" + up);

		int reaction = 1;// wakeup, the preferred behavior in advanced mode

		if (code == KeyEvent.KEYCODE_BACK)
			reaction = 3;// check for wake, if yes, exit
		else if (code == KeyEvent.KEYCODE_POWER)
			reaction = 2;// unlock
		else if (code == KeyEvent.KEYCODE_FOCUS)
			reaction = 0;// locked (advanced power save)

		switch (reaction) {
		case 3:
			onBackPressed();
			return true;
		case 2:
			if (up && !finishing) {
				Log.v("unlock key", "power key UP, unlocking");
				finishing = true;

				setBright((float) 0.1);

				moveTaskToBack(true);

			}
			return true;

		case 1:
			if (up && !screenwake) {
				waking = true;
				Log.v("key event", "wake key");
				wakeUp();
			}
			return true;

		case 0:
			if (!screenwake && up) {
				timeleft = 10;

				if (!waking) {
					Log.v("key event", "locked key timer starting");

					waking = true;
					// serviceHandler.postDelayed(myTask, 500L);
				}
			}

			return true;
		}
		return false;
	}
}