package com.pinokia.considerateapp;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.TextView;

public class ConsiderateAppActivity extends Activity implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private AudioManager am;
    private PrevState pv;
    TextView flippedText;
    boolean accelerometerPresent;
    
    //State to recover
    private class PrevState {
      int audioState;
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	flippedText = (TextView)findViewById(R.id.textView1);
    	flippedText.setText("Not flipped");
     	
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
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
  @Override
	public void onSensorChanged(SensorEvent event) {
    
		float z_value = event.values[2];
		if (z_value >= 0){
		  am.setRingerMode(pv.audioState);
			flippedText.setText("Face UP");
		}
		else{
		  //Turn phone to Silent mode
		  am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		  
			flippedText.setText("Face down");
		}
	}
	
}