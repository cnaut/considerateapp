
package com.pinokia.considerateapp;
import java.util.List;

import android.app.IntentService;
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

public class FlipIntentService extends IntentService implements SensorEventListener  {

	String tag = "CONSIDERATE_APP";
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private Sensor proximitySensor;
	private AudioManager am;
	TextView flippedText;

	boolean silentModeEngaged;
	private PrevState pv;

	public FlipIntentService() {
		super("FlipIntentService");
	}

	//State to recover
	private class PrevState {
		int audioState;
	}


	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub	
	}

	public void onSensorChanged(SensorEvent event) {
		boolean faceDown = false;
		boolean closeToObject = false;
		// Handle accelerometer change
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float z_value = event.values[2];
			Log.v(tag, "ACCELEROMETER:" + z_value);
			if (z_value >= -9) {
				faceDown = false;
			} else {
				faceDown = true;
			}
			// Handle proximity sensor change
		} else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			float distance = event.values[0];
			Log.v(tag, "DISTANCE:" + distance);
			if (distance < 1.0) {
				closeToObject = true;
			} else {
				closeToObject = false;
			}
		}
		boolean newSilentModeState = faceDown && closeToObject;
		if (newSilentModeState && !silentModeEngaged) {
			Log.i(tag, "going silent");
			// Change phone to Silent mode
			pv.audioState = am.getRingerMode();
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		} else if (!newSilentModeState && silentModeEngaged) {
			Log.i(tag, "ungoing silent");
			// Change phone back to previous state
			am.setRingerMode(pv.audioState);
		}
	}

	protected void onHandleIntent(Intent intent) {

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
		proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
		if (proximitySensor == null) {
			Log.e(tag, "No proximity sensor present!");
		}
		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		pv = new PrevState();
		pv.audioState = am.getRingerMode();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
