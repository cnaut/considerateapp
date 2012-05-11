package com.pinokia.considerateapp;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class FlipService extends Service implements SensorEventListener  {

	//This is a bit of a hacky way to see if the service is running, but from sources, it looks like it's the best way to do it.
	protected static boolean running = false;
	//This makes the running variable read-only.
	public static boolean isRunning() {return running;}

	String tag = "CONSIDERATE_APP";
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private Sensor proximitySensor;
	private AudioManager am;
	TextView flippedText;

	private boolean faceDown = false;
	private PrevState pv;

	public FlipService() {
	}

	//State to recover
	private class PrevState {
		int audioState;
	}

	//Code to toggle the service
	public static boolean toggleService(Context context) {
		if (isRunning())
		{
			stop(context);
			return false;
		} else
		{
			start(context);
			return true;
		}
	}

	public static void start(Context context) {
	    Intent serviceIntent = new Intent(context, FlipService.class);
		if(!isRunning())
			context.startService(serviceIntent);
	}
	public static void stop(Context context) {
	    Intent serviceIntent = new Intent(context, FlipService.class);
		if(isRunning())
			context.stopService(serviceIntent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		running = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		running = false;
		sensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub	
	}

	public void onSensorChanged(SensorEvent event) {
		boolean newFaceDown = faceDown;

		// Handle accelerometer change
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float z_value = event.values[2];
			Log.v(tag, "ACCELEROMETER:" + z_value);
			if (z_value >= -9) {
				newFaceDown = false;
			} else {
				newFaceDown = true;
			}
		}	
		// Handle proximity sensor change
		 /*else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			float distance = event.values[0];
			Log.v(tag, "DISTANCE:" + distance);
			if (distance < 1.0) {
				newCloseToObject = true;
			} else {
				newCloseToObject = false;
			}
		}*/

		if (!faceDown && newFaceDown) {
			// Change phone to Silent mode
			pv.audioState = am.getRingerMode();
			Log.i(tag, "going silent");
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		} else if (faceDown && !newFaceDown) {
			Log.i(tag, "ungoing silent");
			// Change phone back to previous state
			am.setRingerMode(pv.audioState);
		}

		faceDown = newFaceDown;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);	
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

		// Configure accelerometer
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensorList.size() > 0) {
			accelerometerSensor = sensorList.get(0);  
		} else {
			Log.e(tag, "No accelerometer present!");
		}

		// Configure proximity sensor
		// proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		// sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
		// if (proximitySensor == null) {
		// 	Log.e(tag, "No proximity sensor present!");
		// }
		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		pv = new PrevState();
		pv.audioState = am.getRingerMode();
		return 1;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
