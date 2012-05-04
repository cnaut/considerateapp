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
import android.widget.TextView;

public class FlipIntentService extends IntentService implements SensorEventListener  {
	
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private AudioManager am;
    TextView flippedText;
    boolean accelerometerPresent;
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
		float z_value = event.values[2];
		if (z_value >= 0){
		  am.setRingerMode(pv.audioState);
		}
		else{
		  //Turn phone to Silent mode
		  am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		
		}
	}
    
	protected void onHandleIntent(Intent intent) {
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    
	    List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
	    if(sensorList.size() > 0){
	     accelerometerPresent = true;
	     accelerometerSensor = sensorList.get(0);  
	    }
	    else{
	     accelerometerPresent = false;  
	     flippedText.setText("No accelerometer present!");
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
